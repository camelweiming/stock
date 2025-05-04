let stockChart = null;
let currentView = 'day';
let loadedStartDate = null;
let loadedEndDate = null;
let isLoading = false;
let chartInstance = null;
let candlestickSeriesMap = {};
let chartInitialized = false;

// 工具函数
function showStatus(message) {
    const statusBar = document.getElementById('statusBar');
    const statusText = document.getElementById('statusText');
    statusText.textContent = message;
    statusBar.classList.add('active');
}

function hideStatus() {
    const statusBar = document.getElementById('statusBar');
    statusBar.classList.remove('active');
}

function formatNumber(num) {
    return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(num);
}

function formatMonth(date) {
    return date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0');
}

function getLastDayOfMonth(ym) {
    const [year, month] = ym.split('-').map(Number);
    if (!year || !month) return '';
    return new Date(year, month, 0).toISOString().slice(0, 10);
}

function setMonthInputValue(input, value) {
    if (!input) return;
    if (typeof value === 'string') {
        input.value = value.slice(0, 7);
    } else {
        input.value = '';
    }
}

// 图表相关函数
function initializeChart() {
    const chartContainer = document.getElementById('stockChart');
    chartInstance = LightweightCharts.createChart(chartContainer, {
        width: chartContainer.offsetWidth,
        height: chartContainer.offsetHeight,
        layout: {
            background: { color: '#F5F5F7' },
            textColor: '#2C3E50',
        },
        grid: {
            vertLines: { color: '#D2D2D7' },
            horzLines: { color: '#D2D2D7' },
        },
        crosshair: {
            mode: LightweightCharts.CrosshairMode.Normal,
        },
        rightPriceScale: {
            borderColor: '#D2D2D7',
            scaleMargins: {
                top: 0.1,
                bottom: 0.1,
            },
        },
        timeScale: {
            borderColor: '#D2D2D7',
            timeVisible: false,
            secondsVisible: false,
            tickMarkFormatter: (time) => {
                if (typeof time === 'string') return time;
                if (typeof time === 'object' && time.year && time.month && time.day) {
                    return `${time.year}-${String(time.month).padStart(2, '0')}-${String(time.day).padStart(2, '0')}`;
                }
                return '';
            }
        },
        localization: {
            dateFormat: 'yyyy-MM-dd'
        }
    });

    window.addEventListener('resize', () => {
        chartInstance.applyOptions({
            width: chartContainer.offsetWidth,
            height: chartContainer.offsetHeight
        });
    });

    chartInitialized = true;
}

function updateChart(startOverride, endOverride) {
    const buttons = document.querySelectorAll('.symbol-item');
    const selectedSymbols = Array.from(buttons)
        .filter(button => button.classList.contains('active'))
        .map(button => button.dataset.symbol);
    
    const startMonth = startOverride || document.getElementById('startDate').value;
    const endMonth = endOverride || document.getElementById('endDate').value;
    const startDate = startMonth ? startMonth + '-01' : '';
    const endDate = endMonth ? getLastDayOfMonth(endMonth) : '';

    if (selectedSymbols.length === 0 || !startDate || !endDate) {
        console.log('请选择股票和时间范围');
        return;
    }

    if (isLoading) return;
    isLoading = true;

    showStatus('正在加载图表数据...');
    fetch(`/api/stock-data?symbols=${selectedSymbols.join(',')}&view=${currentView}&startDate=${startDate}&endDate=${endDate}`)
        .then(response => response.json())
        .then(data => {
            if (!Array.isArray(data) || data.length === 0) {
                console.error('没有数据');
                hideStatus();
                isLoading = false;
                return;
            }

            const groupedData = data.reduce((acc, item) => {
                if (!acc[item.symbol]) acc[item.symbol] = [];
                acc[item.symbol].push(item);
                return acc;
            }, {});

            if (!chartInitialized) {
                initializeChart();
            }

            Object.values(candlestickSeriesMap).forEach(series => {
                chartInstance.removeSeries(series);
            });
            candlestickSeriesMap = {};

            let allDates = [];

            Object.entries(groupedData).forEach(([symbol, items]) => {
                const prices = items
                    .filter(item => !('M_' in item) &&
                        (typeof (item.open ?? item.openPrice) === 'number' || !isNaN(parseFloat(item.open ?? item.openPrice))) &&
                        (typeof (item.high ?? item.highPrice) === 'number' || !isNaN(parseFloat(item.high ?? item.highPrice))) &&
                        (typeof (item.low ?? item.lowPrice) === 'number' || !isNaN(parseFloat(item.low ?? item.lowPrice))) &&
                        (typeof (item.close ?? item.closePrice) === 'number' || !isNaN(parseFloat(item.close ?? item.closePrice))) &&
                        item.tradeDate !== undefined
                    )
                    .map(item => {
                        let timeStr;
                        if (typeof item.tradeDate === 'object' && item.tradeDate !== null) {
                            const { year, month, day } = item.tradeDate;
                            timeStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
                        } else {
                            const date = new Date(item.tradeDate);
                            timeStr = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                        allDates.push(timeStr);
                        return {
                            time: timeStr,
                            open: parseFloat(item.openPrice ?? item.open),
                            high: parseFloat(item.highPrice ?? item.high),
                            low: parseFloat(item.lowPrice ?? item.low),
                            close: parseFloat(item.closePrice ?? item.close)
                        };
                    });
                
                const candlestickSeries = chartInstance.addCandlestickSeries({
                    upColor: '#e74c3c',
                    downColor: '#27ae60',
                    borderVisible: false,
                    wickUpColor: '#e74c3c',
                    wickDownColor: '#27ae60',
                });
                
                candlestickSeriesMap[symbol] = candlestickSeries;
                const sortedData = prices.sort((a, b) => a.time.localeCompare(b.time));
                candlestickSeries.setData(sortedData);

                chartInstance.subscribeCrosshairMove(param => {
                    if (param.time && param.seriesPrices) {
                        const price = param.seriesPrices.get(candlestickSeries);
                        if (price) {
                            let dateStr = '';
                            if (typeof param.time === 'string') {
                                dateStr = param.time;
                            } else if (typeof param.time === 'object' && param.time.year && param.time.month && param.time.day) {
                                dateStr = `${param.time.year}-${String(param.time.month).padStart(2, '0')}-${String(param.time.day).padStart(2, '0')}`;
                            }
                            showStatus(`日期: ${dateStr} 开盘: ${price.open} 最高: ${price.high} 最低: ${price.low} 收盘: ${price.close}`);
                        }
                    } else {
                        hideStatus();
                    }
                });
            });

            if (allDates.length > 0) {
                allDates.sort();
                const minMonth = allDates[0].slice(0, 7);
                const maxMonth = allDates[allDates.length - 1].slice(0, 7);
                
                const startDateInput = document.getElementById('startDate');
                const endDateInput = document.getElementById('endDate');
                setMonthInputValue(startDateInput, minMonth);
                setMonthInputValue(endDateInput, maxMonth);
                
                chartInstance.timeScale().fitContent();
            }

            loadedStartDate = startDate;
            loadedEndDate = endDate;
            isLoading = false;
            hideStatus();
        })
        .catch(error => {
            console.error('Error:', error);
            hideStatus();
            isLoading = false;
        });
}

// 事件监听器
function initializeEventListeners() {
    document.querySelectorAll('.symbol-item').forEach(item => {
        item.addEventListener('click', function() {
            document.querySelectorAll('.symbol-item').forEach(btn => {
                btn.classList.remove('active');
                btn.querySelector('i').classList.remove('bi-check-circle');
                btn.querySelector('i').classList.add('bi-circle');
            });
            this.classList.add('active');
            const icon = this.querySelector('i');
            icon.classList.remove('bi-circle');
            icon.classList.add('bi-check-circle');
            updateChart();
        });
    });

    document.querySelectorAll('.view-toggle button').forEach(button => {
        button.addEventListener('click', function() {
            document.querySelectorAll('.view-toggle button').forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            currentView = this.dataset.view;
            updateChart();
        });
    });

    document.getElementById('startDate').addEventListener('change', () => updateChart());
    document.getElementById('endDate').addEventListener('change', () => updateChart());
}

// 初始化函数
function initialize() {
    const end = new Date();
    const start = new Date();
    start.setMonth(end.getMonth() - 6);
    
    const minDate = new Date();
    minDate.setFullYear(end.getFullYear() - 10);
    
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    
    startDateInput.min = formatMonth(minDate);
    startDateInput.max = formatMonth(end);
    endDateInput.min = formatMonth(minDate);
    endDateInput.max = formatMonth(end);
    setMonthInputValue(startDateInput, formatMonth(start));
    setMonthInputValue(endDateInput, formatMonth(end));
    
    initializeEventListeners();
    
    setTimeout(() => {
        const firstSymbol = document.querySelector('.symbol-item');
        if (firstSymbol) {
            firstSymbol.classList.add('active');
            const icon = firstSymbol.querySelector('i');
            icon.classList.remove('bi-circle');
            icon.classList.add('bi-check-circle');
        }
        updateChart();
    }, 1000);
}

// 页面加载完成后初始化
window.onload = initialize; 