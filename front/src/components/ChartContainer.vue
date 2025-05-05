<template>
  <div class="chart-container">
    <div class="chart-wrapper">
      <v-chart :option="option" autoresize class="chart" />
    </div>
    <div class="news-container">
      <div class="news-header">
        <span>详情</span>
      </div>
      <div class="news-list">
        <div v-for="(news, index) in newsList" :key="index" class="news-item">
          <div class="news-date">{{ news.date }}</div>
          <div class="news-content">{{ news.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useStore } from 'vuex'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CandlestickChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, TitleComponent, DataZoomComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([CandlestickChart, GridComponent, TooltipComponent, TitleComponent, DataZoomComponent, CanvasRenderer])

const store = useStore()
const stockData = computed(() => store.state.stockData)
const newsList = computed(() => store.state.newsList)

const option = ref({
  title: { text: '股票K线图', left: 'center' },
  tooltip: { trigger: 'axis' },
  grid: { left: '5%', right: '5%', bottom: '10%', top: 50 },
  xAxis: {
    type: 'category',
    data: [],
    scale: true,
    boundaryGap: false,
    axisLine: { onZero: false },
    splitLine: { show: false },
    min: 'dataMin',
    max: 'dataMax',
  },
  yAxis: {
    scale: true,
    splitArea: { show: true }
  },
  dataZoom: [
    { type: 'inside', start: 50, end: 100 },
    { show: true, type: 'slider', top: '90%', start: 50, end: 100 }
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

// 格式化K线数据
const formatKLineData = (data) => {
  const x = []
  const y = []
  data.forEach(item => {
    x.push(item.tradeDate)
    y.push([
      item.openPrice,   // 开盘价
      item.closePrice,  // 收盘价
      item.lowPrice,    // 最低价
      item.highPrice    // 最高价
    ])
  })
  return { x, y }
}

watch(stockData, (newData) => {
  if (newData && newData.length > 0) {
    const { x, y } = formatKLineData(newData)
    option.value.xAxis.data = x
    option.value.series[0].data = y
  } else {
    option.value.xAxis.data = []
    option.value.series[0].data = []
  }
}, { immediate: true })
</script>

<style scoped>
.chart-container {
  display: flex;
  gap: 2rem;
  background-color: #fff;
  padding: 1.5rem;
  border-radius: 0.5rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.chart-wrapper {
  flex: 2;
  height: 500px;
  position: relative;
}

.chart {
  width: 100%;
  height: 100%;
}

.news-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-header {
  display: flex;
  align-items: center;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
  margin-bottom: 1rem;
  font-weight: 500;
}

.news-list {
  flex: 1;
  overflow-y: auto;
}

.news-item {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.news-item:last-child {
  border-bottom: none;
}

.news-date {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.news-content {
  color: #333;
  line-height: 1.5;
}
</style> 