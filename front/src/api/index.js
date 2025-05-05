import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export default {
  initialize() {
    return api.get('/initialize')
  },
  update() {
    return api.get('/update')
  },
  getStockData(params) {
    return api.get('/stock', { params })
  }
} 