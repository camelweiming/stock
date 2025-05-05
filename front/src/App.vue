<template>
  <div class="app-container">
    <header class="app-header">
      <div class="header-content">
        <h1>股票分析系统</h1>
        <div class="header-actions">
          <button class="btn btn-primary" @click="handleRefresh">
            <i class="bi bi-arrow-clockwise"></i>
            刷新数据
          </button>
        </div>
      </div>
    </header>
    <main class="app-main">
      <ControlPanel />
      <ChartContainer />
    </main>
  </div>
</template>

<script setup>
import { useStore } from 'vuex'
import ControlPanel from './components/ControlPanel.vue'
import ChartContainer from './components/ChartContainer.vue'

const store = useStore()

const handleRefresh = () => {
  store.dispatch('fetchStockData')
}
</script>

<style>
@import './assets/css/style.css';

.app-container {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.app-header {
  background-color: #fff;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.header-content h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.btn {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #0d6efd;
  color: #fff;
  border: none;
}

.btn-primary:hover {
  background-color: #0b5ed7;
}

.btn i {
  margin-right: 0.5rem;
}
</style>
