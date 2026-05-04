<template>
  <div class="driver-order-detail">
    <AppHeader />
    <div class="content" v-if="order">
      <h2>订单详情 #{{ order.id }}</h2>
      <el-card>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="状态"><el-tag :type="statusType(order.status)">{{ order.statusText }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="上门地址">{{ order.pickupAddress }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ order.pickupTime }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ order.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <div class="actions" v-if="order.status === 'in_progress'">
        <el-button type="success" size="large" @click="complete">确认完成</el-button>
      </div>
    </div>
    <div v-else class="loading"><el-icon class="is-loading" :size="32"><Loading /></el-icon></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const order = ref(null)

onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/driver/orders/${route.params.id}`)
    order.value = data
  } catch { ElMessage.error('加载失败') }
})

function statusType(s) {
  return { pending: 'info', assigned: '', in_progress: 'warning', completed: 'success', cancelled: 'danger' }[s] || 'info'
}

async function complete() {
  try {
    await axios.post(`/api/driver/orders/${order.value.id}/complete`)
    ElMessage.success('订单已完成')
    router.push('/driver/orders')
  } catch { ElMessage.error('操作失败') }
}
</script>

<style scoped>
.driver-order-detail { min-height: 100vh; background: #f5f5f5; }
.content { max-width: 700px; margin: 0 auto; padding: 20px; }
.content h2 { margin-bottom: 16px; }
.actions { margin-top: 20px; text-align: center; }
.loading { text-align: center; padding: 60px; }
</style>
