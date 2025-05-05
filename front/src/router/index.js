import { createRouter, createWebHistory } from 'vue-router'
import ChartContainer from '../components/ChartContainer.vue'
import Settings from '../components/Settings.vue'

const routes = [
  { path: '/', name: 'Home', component: ChartContainer },
  { path: '/settings', name: 'Settings', component: Settings }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 