<template>
  <div class="admin-dashboard">
    <AppHeader />
    <div class="admin-content">
      <h2>管理后台</h2>
      <el-row :gutter="16" class="stats-row">
        <el-col :span="6"><el-card><div class="stat-num">{{ stats.totalOrders }}</div><div class="stat-label">总订单数</div></el-card></el-col>
        <el-col :span="6"><el-card><div class="stat-num">{{ stats.todayOrders }}</div><div class="stat-label">今日订单</div></el-card></el-col>
        <el-col :span="6"><el-card><div class="stat-num">{{ stats.totalUsers }}</div><div class="stat-label">用户数</div></el-card></el-col>
        <el-col :span="6"><el-card><div class="stat-num">{{ stats.totalStations }}</div><div class="stat-label">回收站点</div></el-card></el-col>
      </el-row>
      <el-card class="recent-orders" header="最新订单">
        <el-table :data="recentOrders" stripe>
          <el-table-column prop="id" label="订单ID" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag v-if="scope && scope.row" :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button v-if="scope && scope.row" link type="primary" @click="router.push(`/order/detail/${scope.row.id}`)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const stats = ref({ totalOrders: 0, todayOrders: 0, totalUsers: 0, totalStations: 0 })
const recentOrders = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/admin/dashboard')
    stats.value = data.stats || {}
    recentOrders.value = data.recentOrders || []
  } catch (err) {
    ElMessage.error('加载数据失败')
  }
})

function statusType(status) {
  const map = { pending: 'info', assigned: '', in_progress: 'warning', completed: 'success', cancelled: 'danger' }
  return map[status] || 'info'
}
</script>

<style scoped>
.admin-dashboard { min-height: 100vh; background: #f5f5f5; }
.admin-content { max-width: 1200px; margin: 0 auto; padding: 20px; }
.admin-content h2 { margin-bottom: 20px; }
.stats-row { margin-bottom: 20px; }
.stat-num { font-size: 28px; font-weight: 700; text-align: center; color: #409EFF; }
.stat-label { text-align: center; color: #666; margin-top: 4px; }
.recent-orders { margin-top: 16px; }
</style>
