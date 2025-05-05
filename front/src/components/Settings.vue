<template>
  <div class="settings-container">
    <div class="card">
      <div class="card-header">
        <h5 class="card-title">数据更新设置</h5>
      </div>
      <div class="card-body">
        <div class="update-section">
          <div class="update-item">
            <h6>全量更新</h6>
            <p class="text-muted">更新所有股票的历史数据，可能需要较长时间</p>
            <button
                class="btn btn-primary"
                @click="handleFullUpdate"
                :disabled="isUpdating"
            >
              {{ isFullUpdating ? '更新中...' : '开始全量更新' }}
            </button>
          </div>
          <div class="update-item">
            <h6>增量更新</h6>
            <p class="text-muted">更新最近的数据，耗时较短</p>
            <button
                class="btn btn-primary"
                @click="handleIncrementalUpdate"
                :disabled="isUpdating"
            >
              {{ isIncrementalUpdating ? '更新中...' : '开始增量更新' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import {ref, computed} from 'vue'
import {ElMessage} from 'element-plus'

const isFullUpdating = ref(false)
const isIncrementalUpdating = ref(false)
const isUpdating = computed(() => isFullUpdating.value || isIncrementalUpdating.value)

const handleFullUpdate = async () => {
  try {
    isFullUpdating.value = true
    const response = await axios.post('/api/admin/initialize')
    if (response.data.success) {
      ElMessage.success('全量更新已开始')
    } else {
      ElMessage.error(response.data.message || '全量更新失败')
    }
  } catch (error) {
    ElMessage.error('全量更新请求失败')
    console.error('Full update error:', error)
  } finally {
    isFullUpdating.value = false
  }
}

const handleIncrementalUpdate = async () => {
  try {
    isIncrementalUpdating.value = true
    const response = await axios.post('/api/admin/incremental-update')
    if (response.data.success) {
      ElMessage.success('增量更新已开始')
    } else {
      ElMessage.error(response.data.message || '增量更新失败')
    }
  } catch (error) {
    ElMessage.error('增量更新请求失败')
    console.error('Incremental update error:', error)
  } finally {
    isIncrementalUpdating.value = false
  }
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
}

.card {
  max-width: 100%;
  margin: 0 auto;
}

.update-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.update-item {
  padding: 20px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background-color: #f8f9fa;
}

.update-item h6 {
  margin-bottom: 10px;
  color: #495057;
}

.update-item p {
  margin-bottom: 15px;
  font-size: 0.9rem;
}

.btn {
  min-width: 120px;
}

.btn:disabled {
  cursor: not-allowed;
}
</style>