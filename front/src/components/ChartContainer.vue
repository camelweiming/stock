<template>
  <div class="row">
    <!-- 左侧侧边栏 -->
    <div class="col-md-3">
      <div class="card card-sidebar">
        <!-- 这里可以放置你的菜单、股票选择等内容 -->
        <ControlPanel />
      </div>
    </div>
    <!-- 右侧主内容区 -->
    <div class="col-md-9">
      <div class="card card-chart">
<!--        <div class="card-header">-->
<!--          <h5 class="card-title">股票K线图</h5>-->
<!--        </div>-->
        <div class="card-body">
          <v-chart
            ref="vchartRef"
            :option="option"
            autoresize
            style="width:100%;height:500px;min-height:400px;"
            @vue:mounted="onMountedChart"
          />
        </div>
      </div>
    </div>
  </div>
  <!-- 新闻区可放在下方 -->
  <div class="row mt-4">
    <div class="col-12">
      <div class="card">
        <div class="card-header">
          <h5 class="card-title">相关新闻</h5>
        </div>
        <div class="card-body news-list">
          <div v-if="loadingNews" class="loading">加载新闻中...</div>
          <div v-else-if="newsList.length === 0" class="news-item">暂无相关新闻</div>
          <div
            v-for="(news, index) in newsList"
            :key="index"
            class="news-item"
          >
            <div class="news-header">
              <span class="news-title">{{ news.title }}</span>
              <span class="news-meta">{{ formatTime(news.time) }} | {{ news.source }}</span>
            </div>
            <div class="news-content">
              <span v-if="!expanded[index]">
                {{ news.content.slice(0, 10) }}
                <span v-if="news.content.length > 10" class="expand-btn" @click="expand(index)">...展开</span>
              </span>
              <span v-else>
                {{ news.content }}
                <span class="expand-btn" @click="collapse(index)">收起</span>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, watch, nextTick, onMounted, onUnmounted} from 'vue'
import {useStore} from 'vuex'
import axios from 'axios'
import VChart from 'vue-echarts'
import {use} from 'echarts/core'
import {CandlestickChart} from 'echarts/charts'
import {GridComponent, TooltipComponent, TitleComponent, DataZoomComponent} from 'echarts/components'
import {CanvasRenderer} from 'echarts/renderers'
import ControlPanel from './ControlPanel.vue'

use([CandlestickChart, GridComponent, TooltipComponent, TitleComponent, DataZoomComponent, CanvasRenderer])

const store = useStore()
const stockData = computed(() => store.state.stockData)
const selectedSymbol = computed(() => store.state.selectedSymbol)
const selectedView = computed(() => store.state.selectedView)

const isZooming = ref(false);
const zoomTimeout = ref(null);
const DEBOUNCE_DELAY = 800; // Adjust this value as needed

const option = ref({
  title: {text: '股票K线图', left: 'center'},
  tooltip: {trigger: 'axis'},
  grid: {left: '5%', right: '5%', bottom: '10%', top: 50},
  xAxis: {
    type: 'category',
    data: [],
    scale: true,
    boundaryGap: false,
    axisLine: {onZero: false},
    splitLine: {show: false},
    min: 'dataMin',
    max: 'dataMax',
  },
  yAxis: {
    scale: true,
    splitArea: {show: true}
  },
  dataZoom: [
    {type: 'inside', start: 0, end: 100, realtime: false},
    {show: true, type: 'slider', top: '90%', start: 0, end: 100}
  ],
  series: [
    {
      type: 'candlestick',
      name: 'K线',
      data: [],
      itemStyle: {
        color: '#26a69a',
        color0: '#ef5350',
        borderColor: '#26a69a',
        borderColor0: '#ef5350'
      }
    }
  ]
})

const formatKLineData = (data) => {
  const x = []
  const y = []
  data.forEach(item => {
    x.push(item.tradeDate)
    y.push([
      item.openPrice,
      item.closePrice,
      item.lowPrice,
      item.highPrice
    ])
  })
  return {x, y}
}

watch(stockData, (newData) => {
  if (newData && newData.length > 0) {
    const {x, y} = formatKLineData(newData)
    option.value.xAxis.data = x
    option.value.series[0].data = y
    // 初次加载时自动拉取新闻
    nextTick(() => fetchNewsByChartWindow())
  } else {
    option.value.xAxis.data = []
    option.value.series[0].data = []
  }
}, {immediate: true})

const vchartRef = ref(null)
const newsList = ref([])
const loadingNews = ref(false)
const expanded = ref({}) // 控制每条新闻的展开/收起

// 格式化时间
const formatTime = (t) => {
  if (!t) return ''
  return t.length > 16 ? t.slice(0, 16) : t
}

// 展开/收起正文
const expand = (idx) => {
  expanded.value[idx] = true
}
const collapse = (idx) => {
  expanded.value[idx] = false
}

// 监听图表缩放/拖动
const onMountedChart = () => {
  nextTick(() => {
    if (vchartRef.value && vchartRef.value.chart) {
      vchartRef.value.chart.resize();
    }
    const chartInstance = vchartRef.value.chart
    chartInstance.on('dataZoom', onDataZoom)
  })
}

const onDataZoom = (params) => {
  isZooming.value = true;
  clearTimeout(zoomTimeout.value);
  zoomTimeout.value = setTimeout(() => {
    fetchNewsByChartWindow(params);
  }, DEBOUNCE_DELAY);
};

// 获取当前K线窗口的时间范围并请求新闻
function fetchNewsByChartWindow() {
  const chartInstance = vchartRef.value?.chart
  if (!chartInstance || !option.value.xAxis.data.length) return
  const chartOption = typeof chartInstance.getOption === 'function' ? chartInstance.getOption() : null
  if (!chartOption || !chartOption.dataZoom || !Array.isArray(chartOption.dataZoom) || chartOption.dataZoom.length === 0) return
  const dataZoom = chartOption.dataZoom[0]
  let startIdx = Math.round((dataZoom?.start ?? 0) / 100 * (option.value.xAxis.data.length - 1)) || 0
  let endIdx = Math.round((dataZoom?.end ?? 100) / 100 * (option.value.xAxis.data.length - 1)) || (option.value.xAxis.data.length - 1)
  if (startIdx > endIdx) [startIdx, endIdx] = [endIdx, startIdx]
  const startTime = option.value.xAxis.data[startIdx]
  const endTime = option.value.xAxis.data[endIdx]
  fetchNews(startTime, endTime)
}

// 拉取新闻
async function fetchNews(startTime, endTime) {
  loadingNews.value = true
  newsList.value = []
  expanded.value = {}
  try {
    const res = await axios.get('/api/get_news', {
      params: {
        startTime: startTime + ' 00:00:00',
        endTime: endTime + ' 23:59:59',
        count: 50,
        mode: selectedView.value
      }
    })
    newsList.value = (res.data.data || []).sort((a, b) => (b.time || b.date || '').localeCompare(a.time || a.date || ''))
  } catch (e) {
    newsList.value = []
  } finally {
    loadingNews.value = false
  }
  isZooming.value = false;
}

onUnmounted(() => {
  // 清除定时器
  if (zoomTimeout.value) {
    clearTimeout(zoomTimeout.value)
    zoomTimeout.value = null
  }
  // 移除 ECharts 事件监听
  const chartInstance = vchartRef.value?.chart
  if (chartInstance && chartInstance.off) {
    chartInstance.off('dataZoom', onDataZoom)
  }
})
</script>