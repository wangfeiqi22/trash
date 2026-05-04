<template>
  <div class="driver-task-view">
    <AppHeader />
    <div class="task-content">
      <h2>我的任务</h2>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="待接单" name="pending">
          <el-card v-for="order in pendingOrders" :key="order.id" class="order-card" @click="viewDetail(order.id)">
            <div class="order-info">
              <div class="order-id">订单 #{{ order.id }}</div>
              <div class="order-address">{{ order.pickupAddress }}</div>
              <div class="order-time">{{ order.createdAt }}</div>
            </div>
            <div class="order-actions">
              <el-button type="primary" size="small" @click.stop="acceptOrder(order.id)">接单</el-button>
            </div>
          </el-card>
          <el-empty v-if="pendingOrders.length === 0" description="暂无待接单任务" />
        </el-tab-pane>
        <el-tab-pane label="进行中" name="in_progress">
          <el-card v-for="order in activeOrders" :key="order.id" class="order-card active">
            <div class="order-info">
              <div class="order-id">订单 #{{ order.id }}</div>
              <div class="order-address">{{ order.pickupAddress }}</div>
              <div class="order-status-badge">
                <el-tag type="warning">进行中</el-tag>
              </div>
            </div>
            <div class="order-actions">
              <el-button type="success" size="small" @click.stop="completeOrder(order.id)">完成</el-button>
              <el-button size="small" @click.stop="viewDetail(order.id)">详情</el-button>
            </div>
          </el-card>
          <el-empty v-if="activeOrders.length === 0" description="暂无进行中任务" />
        </el-tab-pane>
      </el-tabs>
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
const activeTab = ref('pending')
const pendingOrders = ref([])
const activeOrders = ref([])

onMounted(async () => {
  await loadOrders()
})

async function loadOrders() {
  try {
    const { data } = await axios.get('/api/driver/orders')
    pendingOrders.value = (data.pendingOrders || []).filter(o => o.status === 'pending')
    activeOrders.value = (data.activeOrders || []).filter(o => o.status === 'in_progress')
  } catch (err) {
    ElMessage.error('加载任务失败')
  }
}

async function acceptOrder(id) {
  try {
    await axios.post(`/api/driver/orders/${id}/accept`)
    ElMessage.success('接单成功')
    await loadOrders()
  } catch (err) {
    ElMessage.error('接单失败')
  }
}

async function completeOrder(id) {
  try {
    await axios.post(`/api/driver/orders/${id}/complete`)
    ElMessage.success('订单已完成')
    await loadOrders()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

function viewDetail(id) {
  router.push(`/driver/order/${id}`)
}
</script>

<style scoped>
.driver-task-view { min-height: 100vh; background: #f5f5f5; }
.task-content { max-width: 800px; margin: 0 auto; padding: 20px; }
.task-content h2 { margin-bottom: 16px; }
.order-card { margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; cursor: pointer; }
.order-id { font-weight: 600; margin-bottom: 4px; }
.order-address { color: #666; font-size: 14px; }
.order-time { color: #999; font-size: 12px; margin-top: 4px; }
.order-actions { display: flex; gap: 8px; }
</style>
