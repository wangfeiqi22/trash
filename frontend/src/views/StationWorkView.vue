<template>
  <div class="station-work-view">
    <AppHeader />
    <div class="work-content">
      <h2>站点工作台</h2>
      <el-row :gutter="16" class="stats-row">
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.todayIn }}</div><div class="stat-label">今日入库</div></el-card></el-col>
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.todayOut }}</div><div class="stat-label">今日出库</div></el-card></el-col>
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.pendingTask }}</div><div class="stat-label">待处理</div></el-card></el-col>
      </el-row>
      <el-card header="站点操作" style="margin-top: 16px;">
        <el-button type="primary" @click="router.push('/admin/sf')">查看车辆调度</el-button>
        <el-button @click="router.push('/admin/kb')">知识库</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const stats = ref({ todayIn: 0, todayOut: 0, pendingTask: 0 })

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/station/dashboard')
    stats.value = data.stats || stats.value
  } catch { /* ignore */ }
})
</script>

<style scoped>
.station-work-view { min-height: 100vh; background: #f5f5f5; }
.work-content { max-width: 1000px; margin: 0 auto; padding: 20px; }
.work-content h2 { margin-bottom: 20px; }
.stats-row { margin-bottom: 16px; }
.stat-num { font-size: 28px; font-weight: 700; text-align: center; color: #E6A23C; }
.stat-label { text-align: center; color: #666; margin-top: 4px; }
</style>
