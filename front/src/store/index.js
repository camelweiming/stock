import { createStore } from 'vuex'
import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default createStore({
  state: {
    symbols: [],
    selectedSymbol: '',
    selectedView: 'day',
    dateRange: {
      start: '2023-01',
      end: '2023-12'
    },
    stockData: [],
    newsList: [],
    isLoading: false,
    error: null
  },
  mutations: {
    setSymbols(state, symbols) {
      console.log('Setting symbols in store:', symbols)
      state.symbols = symbols
    },
    selectSymbol(state, symbol) {
      console.log('Selecting symbol:', symbol)
      state.selectedSymbol = symbol
    },
    selectView(state, view) {
      state.selectedView = view
    },
    updateDateRange(state, range) {
      state.dateRange = range
    },
    setStockData(state, data) {
      state.stockData = data
    },
    setNewsList(state, news) {
      state.newsList = news
    },
    setLoading(state, loading) {
      state.isLoading = loading
    },
    setError(state, error) {
      state.error = error
    }
  },
  actions: {
    async fetchSymbols({ commit }) {
      try {
        commit('setLoading', true)
        commit('setError', null)
        
        console.log('Fetching symbols from API...')
        const response = await api.get('/symbols')
        console.log('API response:', response.data)
        
        if (response.data && response.data.symbols) {
          commit('setSymbols', response.data.symbols)
          if (response.data.defaultSymbol) {
            commit('selectSymbol', response.data.defaultSymbol)
          } else if (response.data.symbols.length > 0) {
            commit('selectSymbol', response.data.symbols[0])
          }
        } else {
          console.error('Invalid response format:', response.data)
          commit('setError', '获取股票列表失败：返回数据格式错误')
        }
      } catch (error) {
        console.error('获取股票列表失败:', error)
        commit('setError', error.message || '获取股票列表失败')
      } finally {
        commit('setLoading', false)
      }
    },
    async fetchStockData({ commit, state }) {
      try {
        commit('setLoading', true)
        commit('setError', null)
        
        console.log('Fetching stock data with params:', {
          symbol: state.selectedSymbol,
          view: state.selectedView,
          startDate: state.dateRange.start,
          endDate: state.dateRange.end
        })

        const response = await api.get('/stock/data', {
          params: {
            symbol: state.selectedSymbol,
            view: state.selectedView,
            startDate: state.dateRange.start,
            endDate: state.dateRange.end
          }
        })

        console.log('Received response:', response.data)
        
        commit('setStockData', response.data.stockData)
        commit('setNewsList', response.data.newsList)
      } catch (error) {
        console.error('获取股票数据失败:', error)
        commit('setError', error.message || '获取数据失败')
      } finally {
        commit('setLoading', false)
      }
    }
  }
}) 