<template>
  <div class="station-dashboard">
    <AppHeader />
    <div class="dashboard-content">
      <h2>站点工作台</h2>
      <el-row :gutter="16" class="stats-row">
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.todayIn }}</div><div class="stat-label">今日入库</div></el-card></el-col>
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.todayOut }}</div><div class="stat-label">今日出库</div></el-card></el-col>
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.pendingTask }}</div><div class="stat-label">待处理</div></el-card></el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const stats = ref({ todayIn: 0, todayOut: 0, pendingTask: 0 })

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/station/dashboard')
    stats.value = data.stats || stats.value
  } catch {}
})
</script>

<style scoped>
.station-dashboard { min-height: 100vh; background: #f5f5f5; }
.dashboard-content { max-width: 1000px; margin: 0 auto; padding: 20px; }
.dashboard-content h2 { margin-bottom: 20px; }
.stats-row { margin-bottom: 16px; }
.stat-num { font-size: 28px; font-weight: 700; text-align: center; color: #E6A23C; }
.stat-label { text-align: center; color: #666; margin-top: 4px; }
</style>
