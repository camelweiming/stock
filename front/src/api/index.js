import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export default {
  initialize() {
    return api.get('/admin/initialize')
  },
  update() {
    return api.get('/admin/update')
  },
  getStockData(params) {
    return api.get('/stock', { params })
  },
  postNews(params) {
    return api.post('/post_news',{params})
  }
} 