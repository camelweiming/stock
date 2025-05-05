<template>
  <div class="status-bar">
    <div class="container">
      <div class="d-flex align-items-center">
        <div class="loading-spinner me-2"></div>
        <span>{{ statusText }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'StatusBar',
  computed: {
    ...mapState({
      statusText: state => state.statusText
    })
  }
}
</script>

<style scoped>
.status-bar {
  background-color: #f8f9fa;
  padding: 0.5rem 0;
  border-bottom: 1px solid #dee2e6;
}

.loading-spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 