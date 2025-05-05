<template>
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
        <div class="date-input-group">
          <span class="date-label">开始时间：</span>
          <input
              type="month"
              class="date-input"
              v-model="startDate"
              @change="handleDateRangeChange"
          >
        </div>
        <div class="date-input-group">
          <span class="date-label">结束时间：</span>
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
import {ref, computed, onMounted, watch} from 'vue'
import {useStore} from 'vuex'

const store = useStore()

// 初始化时间范围
const initDateRange = () => {
  const now = new Date()
  const endDate = now.toISOString().slice(0, 7) // 当前年月 YYYY-MM
  const startDate = new Date(now.setFullYear(now.getFullYear() - 1)).toISOString().slice(0, 7) // 一年前的年月
  store.commit('updateDateRange', { start: startDate, end: endDate })
}

// 股票列表
const symbols = computed(() => {
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
  {value: 'day', label: '日'},
  {value: 'week', label: '周'},
  {value: 'month', label: '月'}
]
const selectedView = computed({
  get: () => store.state.selectedView,
  set: (value) => store.commit('selectView', value)
})

// 日期范围
const dateRange = computed(() => store.state.dateRange)
const startDate = computed({
  get: () => dateRange.value.start,
  set: (value) => store.commit('updateDateRange', {start: value, end: dateRange.value.end})
})
const endDate = computed({
  get: () => dateRange.value.end,
  set: (value) => store.commit('updateDateRange', {start: dateRange.value.start, end: value})
})

// 事件处理
const handleSymbolSelect = (symbol) => {
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
onMounted(async () => {
  initDateRange() // 初始化时间范围
  await store.dispatch('fetchSymbols')
  console.log('init....',symbols.value,selectedSymbol.value)
  if (symbols.value && symbols.value.length > 0) {
    selectedSymbol.value = symbols.value[0]
    await store.dispatch('fetchStockData')
  }
})

// 监听 symbols 变化
watch(symbols, (newSymbols) => {
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
  gap: 1rem;
  background-color: #f8f9fa;
  padding: 1.2rem 1rem 1rem 1rem;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  color: #495057;
  font-size: 0.875rem;
}

.control-section {
  background: #ffffff;
  border-radius: 6px;
  padding: 0.8rem 0.8rem 0.8rem 0.8rem;
  margin-bottom: 0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
}

.symbol-section {
  margin-bottom: 0.3rem;
}

.view-section {
  margin-bottom: 0.3rem;
}

.time-section {
  margin-bottom: 0;
}

.control-section-title {
  display: flex;
  align-items: center;
  margin-bottom: 0.8rem;
  color: #495057;
  font-weight: 600;
  font-size: 0.9rem;
}

.symbols-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.symbol-item {
  display: flex;
  align-items: center;
  padding: 0.4rem 0.8rem;
  background-color: #f1f3f5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #e9ecef;
  color: #495057;
  font-weight: 500;
  font-size: 0.8rem;
}

.symbol-item:hover {
  background-color: #e9ecef;
}

.symbol-item.active {
  background-color: #e7f5ff;
  color: #228be6;
  border: 1px solid #228be6;
  font-weight: 600;
}

.view-toggle {
  display: flex;
  gap: 0.4rem;
}

.view-toggle button {
  padding: 0.4rem 0.8rem;
  border: 1px solid #ced4da;
  background-color: #ffffff;
  color: #495057;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  font-size: 0.8rem;
}

.view-toggle button.active {
  background-color: #e7f5ff;
  color: #228be6;
  border-color: #228be6;
}

.view-toggle button:hover {
  background-color: #f1f3f5;
}

.time-range {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-range {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  width: 100%;
}

.date-input-group {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  width: 100%;
}

.date-label {
  color: #495057;
  font-size: 0.8rem;
  min-width: 4.5rem;
  text-align: left;
}

.date-input {
  padding: 0.4rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  background: #ffffff;
  color: #495057;
  font-weight: 500;
  font-size: 0.8rem;
  flex: 1;
  text-align: left;
}

.date-input:focus {
  outline: none;
  border-color: #228be6;
}

.loading {
  color: #868e96;
  padding: 0.8rem;
  text-align: center;
  font-size: 0.8rem;
}

.error {
  color: #fa5252;
  padding: 0.8rem;
  text-align: center;
  font-size: 0.8rem;
}
</style> 