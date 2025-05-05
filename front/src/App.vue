<template>
  <div class="app-container">
    <Header />
    <main class="app-main">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import Header from './components/Header.vue'
import ChartContainer from './components/ChartContainer.vue'
</script>

<style>
@import './assets/css/style.css';

.app-container {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}
</style>
