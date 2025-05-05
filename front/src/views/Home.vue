<template>
  <div class="home">
    <Header />
    <StatusBar v-if="isLoading" />
    <div class="container">
      <ControlPanel />
      <ChartContainer />
    </div>
  </div>
</template>

<script>
import Header from '../components/Header.vue'
import StatusBar from '../components/StatusBar.vue'
import ControlPanel from '../components/ControlPanel.vue'
import ChartContainer from '../components/ChartContainer.vue'
import { mapState } from 'vuex'

export default {
  name: 'Home',
  components: {
    Header,
    StatusBar,
    ControlPanel,
    ChartContainer
  },
  computed: {
    ...mapState(['isLoading'])
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f8f9fa;
}
</style> 