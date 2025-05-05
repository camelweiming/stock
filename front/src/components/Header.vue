<template>
  <div class="header">
    <div class="container">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h1>股票分析系统</h1>
          <p>专业的股票数据分析和可视化工具</p>
        </div>
        <div class="d-flex gap-3">
          <button class="btn btn-primary" @click="initializeData">
            <i class="bi bi-download"></i> 初始化数据
          </button>
          <button class="btn btn-success" @click="incrementalUpdate">
            <i class="bi bi-arrow-clockwise"></i> 增量更新
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Header',
  methods: {
    ...mapActions(['initializeData', 'incrementalUpdate'])
  }
}
</script>

<style scoped>
.header {
  background-color: #fff;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 1rem;
}

.header h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #333;
}

.header p {
  margin: 0.5rem 0 0;
  color: #666;
  font-size: 0.9rem;
}
</style> 