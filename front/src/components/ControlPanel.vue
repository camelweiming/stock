<template>
  <div class="control-panel">
    <div class="control-section symbol-section">
      <div class="control-section-title">
        <span>选择股票</span>
      </div>
      <div v-if="isLoading" class="loading">加载中...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else class="symbols-container">
        <div 
          v-for="symbol in symbols" 
          :key="symbol"
          class="symbol-item"
          :class="{ active: selectedSymbol === symbol }"
          @click="handleSymbolSelect(symbol)"
        >
          <span>{{ symbol }}</span>
        </div>
      </div>
    </div>
    <div class="control-section view-section">
      <div class="control-section-title">
        <span>视图设置</span>
      </div>
      <div class="view-toggle">
        <button 
          v-for="view in viewOptions" 
          :key="view.value"
          :class="{ active: selectedView === view.value }"
          @click="handleViewSelect(view.value)"
        >
          {{ view.label }}
        </button>
      </div>
    </div>
    <div class="control-section time-section">
      <div class="control-section-title">
        <span>时间范围</span>
      </div>
      <div class="time-range">
        <div class="date-range">
          <input 
            type="month" 
            class="date-input" 
            v-model="startDate"
            @change="handleDateRangeChange"
          >
          <span class="date-separator">至</span>
          <input 
            type="month" 
            class="date-input" 
            v-model="endDate"
            @change="handleDateRangeChange"
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

// 股票列表
const symbols = computed(() => {
  console.log('Current symbols in store:', store.state.symbols)
  return store.state.symbols
})

const selectedSymbol = computed({
  get: () => store.state.selectedSymbol,
  set: (value) => store.commit('selectSymbol', value)
})

const isLoading = computed(() => store.state.isLoading)
const error = computed(() => store.state.error)

// 视图选项
const viewOptions = [
  { value: 'day', label: '日视图' },
  { value: 'week', label: '周视图' },
  { value: 'month', label: '月视图' }
]
const selectedView = computed({
  get: () => store.state.selectedView,
  set: (value) => store.commit('selectView', value)
})

// 日期范围
const dateRange = computed(() => store.state.dateRange)
const startDate = computed({
  get: () => dateRange.value.start,
  set: (value) => store.commit('updateDateRange', { start: value, end: dateRange.value.end })
})
const endDate = computed({
  get: () => dateRange.value.end,
  set: (value) => store.commit('updateDateRange', { start: dateRange.value.start, end: value })
})

// 事件处理
const handleSymbolSelect = (symbol) => {
  console.log('Selected symbol:', symbol)
  selectedSymbol.value = symbol
  store.dispatch('fetchStockData')
}

const handleViewSelect = (view) => {
  selectedView.value = view
  store.dispatch('fetchStockData')
}

const handleDateRangeChange = () => {
  store.dispatch('fetchStockData')
}

// 组件挂载时获取股票列表
onMounted(() => {
  console.log('Component mounted, fetching symbols...')
  store.dispatch('fetchSymbols')
})

// 监听 symbols 变化
watch(symbols, (newSymbols) => {
  console.log('Symbols updated:', newSymbols)
  if (newSymbols && newSymbols.length > 0 && !selectedSymbol.value) {
    selectedSymbol.value = newSymbols[0]
    store.dispatch('fetchStockData')
  }
}, { immediate: true })
</script>

<style scoped>
.control-panel {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background-color: #232e47;
  padding: 2rem 1.5rem 1.5rem 1.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  color: #fff;
}

.control-section {
  background: #273352;
  border-radius: 8px;
  padding: 1.2rem 1rem 1rem 1rem;
  margin-bottom: 0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
}
.symbol-section {
  margin-bottom: 0.5rem;
}
.view-section {
  margin-bottom: 0.5rem;
}
.time-section {
  margin-bottom: 0;
}
.control-section-title {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  color: #e6b422;
  font-weight: 600;
  font-size: 1.08rem;
}
.symbols-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.symbol-item {
  display: flex;
  align-items: center;
  padding: 0.5rem 1.2rem;
  background-color: #1a233a;
  border-radius: 0.3rem;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
  color: #fff;
  font-weight: 500;
}
.symbol-item:hover {
  background-color: #2d3a5a;
}
.symbol-item.active {
  background-color: #fff;
  color: #232e47;
  border: 2px solid #e6b422;
  font-weight: 700;
  box-shadow: 0 0 0 2px #e6b42233;
}
.view-toggle {
  display: flex;
  gap: 0.5rem;
}
.view-toggle button {
  padding: 0.5rem 1.2rem;
  border: 1.5px solid #e6b422;
  background-color: #232e47;
  color: #e6b422;
  border-radius: 0.3rem;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}
.view-toggle button.active {
  background-color: #e6b422;
  color: #232e47;
}
.view-toggle button:hover {
  background-color: #fffbe6;
  color: #232e47;
}
.time-range {
  display: flex;
  align-items: center;
}
.date-range {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.date-input {
  padding: 0.5rem;
  border: 1.5px solid #e6b422;
  border-radius: 0.3rem;
  background: #232e47;
  color: #e6b422;
  font-weight: 500;
}
.date-input:focus {
  outline: none;
  border-color: #fff;
}
.date-separator {
  color: #e6b422;
  font-weight: 600;
}
.loading {
  color: #e6b422;
  padding: 1rem;
  text-align: center;
}
.error {
  color: #dc3545;
  padding: 1rem;
  text-align: center;
}
</style> 