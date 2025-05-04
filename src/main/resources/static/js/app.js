let stockChart = null;
let currentView = 'day';
let loadedStartDate = null;
let loadedEndDate = null;
let isLoading = false;
let chartInstance = null;
let candlestickSeriesMap = {};
let chartInitialized = false;
let newsFetchTimer = null;
let currentNewsDate = '';
let currentStockDetail = null;

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
    console.log('开始初始化图表');
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
            vertLine: {
                visible: true,
                labelVisible: true,
            },
            horzLine: {
                visible: true,
                labelVisible: true,
            }
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
            timeVisible: true,
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

    chartInstance.subscribeClick(param => {
        if (!param.time) {
            hideStatus();
            clearNews();
            return;
        }
        let found = false;
        for (const s of Object.values(candlestickSeriesMap)) {
            const price = param.seriesData ? param.seriesData.get(s) : null;
            if (price) {
                found = true;
                let dateStr = '';
                if (typeof param.time === 'number') {
                    const d = new Date(param.time * 1000);
                    dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
                }
                showStatus(`日期: ${dateStr} 开盘: ${price.open} 最高: ${price.high} 最低: ${price.low} 收盘: ${price.close}`);
                // 传递详情
                fetchNews(dateStr, price);
                break;
            }
        }
        if (!found) {
            hideStatus();
            clearNews();
        }
    });

    window.addEventListener('resize', () => {
        chartInstance.applyOptions({
            width: chartContainer.offsetWidth,
            height: chartContainer.offsetHeight
        });
    });

    chartInitialized = true;
    console.log('图表初始化完成');
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
                        let timeVal;
                        if (typeof item.tradeDate === 'object' && item.tradeDate !== null) {
                            const { year, month, day } = item.tradeDate;
                            timeVal = Math.floor(new Date(`${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`).getTime() / 1000);
                        } else {
                            const date = new Date(item.tradeDate);
                            timeVal = Math.floor(date.getTime() / 1000);
                        }
                        allDates.push(timeVal);
                        return {
                            time: timeVal,
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
                const sortedData = prices.sort((a, b) => a.time - b.time);
                console.log('设置K线数据:', sortedData);
                candlestickSeries.setData(sortedData);
            });

            if (allDates.length > 0) {
                allDates.sort();
                const minDate = new Date(allDates[0] * 1000);
                const maxDate = new Date(allDates[allDates.length - 1] * 1000);
                const minMonth = `${minDate.getFullYear()}-${String(minDate.getMonth() + 1).padStart(2, '0')}`;
                const maxMonth = `${maxDate.getFullYear()}-${String(maxDate.getMonth() + 1).padStart(2, '0')}`;
                
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

// 新闻相关函数
function fetchNews(date, stockDetail) {
    currentNewsDate = date;
    currentStockDetail = stockDetail;
    updateNewsDateHeader();
    fetch(`/api/get_news?date=${date}&count=5`)
        .then(response => response.json())
        .then(data => {
            if (data.code === 0) {
                displayNews(data.data);
            } else {
                console.error('获取新闻失败:', data.message);
            }
        })
        .catch(error => {
            console.error('获取新闻出错:', error);
        });
}

function updateNewsDateHeader() {
    let newsDateHeader = document.getElementById('newsDateHeader');
    if (!newsDateHeader) {
        const newsContainer = document.querySelector('.news-container');
        newsDateHeader = document.createElement('div');
        newsDateHeader.id = 'newsDateHeader';
        newsDateHeader.style.textAlign = 'left';
        newsDateHeader.style.fontWeight = 'normal';
        newsDateHeader.style.fontSize = '15px';
        newsDateHeader.style.padding = '10px 16px 8px 16px';
        newsDateHeader.style.marginBottom = '8px';
        newsDateHeader.style.borderRadius = '6px';
        newsContainer.insertBefore(newsDateHeader, newsContainer.children[1]);
    }
    if (currentNewsDate && currentStockDetail) {
        const isUp = Number(currentStockDetail.close) > Number(currentStockDetail.open);
        newsDateHeader.style.background = isUp ? '#ffeaea' : '#eaffea';
        newsDateHeader.style.color = isUp ? '#d93030' : '#219653';
        newsDateHeader.innerHTML = `
            <div>日期：${currentNewsDate}</div>
            <div style="margin-top:4px;">开盘：${currentStockDetail.open}　收盘：${currentStockDetail.close}</div>
            <div style="margin-top:2px;">最高：${currentStockDetail.high}　最低：${currentStockDetail.low}</div>
        `;
    } else {
        newsDateHeader.textContent = '';
        newsDateHeader.style.background = '';
        newsDateHeader.style.color = '';
    }
}

function displayNews(newsList) {
    const newsListElement = document.getElementById('newsList');
    newsListElement.innerHTML = '';
    newsList.forEach(news => {
        const newsItem = document.createElement('div');
        newsItem.className = 'news-item';
        newsItem.innerHTML = `
            <h4>${news.title}</h4>
            <div class="time">${news.time}</div>
            <div class="content">${news.content}</div>
            <div class="source">来源: ${news.source}</div>
        `;
        newsListElement.appendChild(newsItem);
    });
}

function clearNews() {
    const newsListElement = document.getElementById('newsList');
    newsListElement.innerHTML = '';
    currentNewsDate = '';
    currentStockDetail = null;
    updateNewsDateHeader();
} 