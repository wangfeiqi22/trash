<template>
  <div class="property-dashboard">
    <AppHeader />
    <div class="dashboard-content">
      <h2>物业工作台</h2>
      <el-row :gutter="16" class="stats-row">
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.totalOrders }}</div><div class="stat-label">总订单</div></el-card></el-col>
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.pendingOrders }}</div><div class="stat-label">待处理</div></el-card></el-col>
        <el-col :span="8"><el-card><div class="stat-num">{{ stats.completedOrders }}</div><div class="stat-label">已完成</div></el-card></el-col>
      </el-row>
      <el-card header="最近订单">
        <el-table :data="orders" stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag v-if="scope && scope.row" :type="statusTagType(scope.row.status)">{{ scope.row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="router.push(`/order/detail/${row.id}`)">详情</el-button>
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
const stats = ref({ totalOrders: 0, pendingOrders: 0, completedOrders: 0 })
const orders = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/property/dashboard')
    stats.value = data.stats || {}
    orders.value = data.orders || []
  } catch {
    ElMessage.error('加载数据失败')
  }
})

function statusTagType(status) {
  return { pending: 'info', assigned: '', in_progress: 'warning', completed: 'success', cancelled: 'danger' }[status] || 'info'
}
</script>

<style scoped>
.property-dashboard { min-height: 100vh; background: #f5f5f5; }
.dashboard-content { max-width: 1000px; margin: 0 auto; padding: 20px; }
.dashboard-content h2 { margin-bottom: 20px; }
.stats-row { margin-bottom: 20px; }
.stat-num { font-size: 28px; font-weight: 700; text-align: center; color: #67C23A; }
.stat-label { text-align: center; color: #666; margin-top: 4px; }
</style>
