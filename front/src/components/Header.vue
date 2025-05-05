<template>
  <div class="header">
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-center" style="margin: 0 22px;">
        <div>
          <h1>股票分析系统</h1>
          <p>专业的股票数据分析和可视化工具</p>
        </div>
        <div class="d-flex gap-3">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/settings" class="nav-link">设置</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Header'
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

.nav-link {
  color: #495057;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #228be6;
}

.nav-link.router-link-active {
  color: #228be6;
}
</style> 