<template>
  <div class="order-list-view">
    <AppHeader />
    <div class="list-content">
      <h2>我的订单</h2>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待处理" name="pending" />
        <el-tab-pane label="已完成" name="completed" />
      </el-tabs>
      <div v-if="loading" class="loading"><el-icon class="is-loading"><Loading /></el-icon></div>
      <div v-else-if="filteredOrders.length === 0" class="empty">
        <el-empty description="暂无订单记录" />
      </div>
      <div v-else class="order-list">
        <el-card v-for="order in filteredOrders" :key="order.id" class="order-item" @click="router.push(`/order/detail/${order.id}`)">
          <div class="order-header">
            <span class="order-id">订单 #{{ order.id }}</span>
            <el-tag :type="statusType(order.status)">{{ order.statusText }}</el-tag>
          </div>
          <div class="order-address">{{ order.pickupAddress }}</div>
          <div class="order-footer">
            <span class="order-time">{{ order.createdAt }}</span>
            <span class="order-type">{{ order.wasteTypeText }}</span>
          </div>
        </el-card>
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
const loading = ref(true)
const orders = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/orders/my')
    orders.value = data || []
  } catch {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
})

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(o => o.status === activeTab.value)
})

function statusType(status) {
  return { pending: 'info', assigned: '', in_progress: 'warning', completed: 'success', cancelled: 'danger' }[status] || 'info'
}
</script>

<style scoped>
.order-list-view { min-height: 100vh; background: #f5f5f5; }
.list-content { max-width: 800px; margin: 0 auto; padding: 20px; }
.list-content h2 { margin-bottom: 16px; }
.order-list { display: flex; flex-direction: column; gap: 12px; }
.order-item { cursor: pointer; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.order-id { font-weight: 600; }
.order-address { color: #666; font-size: 14px; margin-bottom: 8px; }
.order-footer { display: flex; justify-content: space-between; color: #999; font-size: 12px; }
.loading, .empty { text-align: center; padding: 60px 0; }
</style>
