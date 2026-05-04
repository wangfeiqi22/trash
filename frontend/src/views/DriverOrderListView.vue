<template>
  <div class="driver-order-list">
    <AppHeader />
    <div class="content">
      <h2>订单记录</h2>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="已完成" name="completed" />
      </el-tabs>
      <div class="order-list">
        <el-card v-for="order in filteredOrders" :key="order.id" class="order-item" @click="router.push(`/driver/order/${order.id}`)">
          <div class="order-header">
            <span class="order-id">#{{ order.id }}</span>
            <el-tag :type="statusType(order.status)">{{ order.statusText }}</el-tag>
          </div>
          <div class="order-address">{{ order.pickupAddress }}</div>
          <div class="order-time">{{ order.completedAt || order.createdAt }}</div>
        </el-card>
        <el-empty v-if="filteredOrders.length === 0" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const activeTab = ref('all')
const orders = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/driver/orders/all')
    orders.value = data || []
  } catch { ElMessage.error('加载失败') }
})

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(o => o.status === 'completed')
})

function statusType(s) {
  return { pending: 'info', assigned: '', in_progress: 'warning', completed: 'success', cancelled: 'danger' }[s] || 'info'
}
</script>

<style scoped>
.driver-order-list { min-height: 100vh; background: #f5f5f5; }
.content { max-width: 800px; margin: 0 auto; padding: 20px; }
.content h2 { margin-bottom: 16px; }
.order-list { display: flex; flex-direction: column; gap: 12px; }
.order-item { cursor: pointer; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.order-id { font-weight: 600; }
.order-address { color: #666; margin-bottom: 4px; }
.order-time { color: #999; font-size: 12px; }
</style>
