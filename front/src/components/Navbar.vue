<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">
      <router-link class="navbar-brand" to="/">股票分析系统</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">首页</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/settings">设置</router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  padding: 0.5rem 1rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.navbar-brand {
  font-weight: 600;
  color: #495057;
}

.nav-link {
  color: #495057;
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