<template>
  <div class="order-detail-view">
    <AppHeader />
    <div class="detail-content" v-if="order">
      <h2>订单详情 #{{ order.id }}</h2>
      <el-card>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(order.status)">{{ order.statusText }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="垃圾类型">{{ order.wasteTypeText }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ order.pickupTime }}</el-descriptions-item>
          <el-descriptions-item label="上门地址">{{ order.pickupAddress }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ order.remark || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ order.createdAt }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card v-if="order.photos?.length" header="现场照片" style="margin-top: 16px;">
        <div class="photo-grid">
          <el-image v-for="(photo, i) in order.photos" :key="i" :src="photo" fit="cover" style="width: 100px; height: 100px;" :preview-src-list="order.photos" />
        </div>
      </el-card>
      <div class="detail-actions" v-if="order.status === 'pending'">
        <el-button type="danger" @click="cancelOrder">取消订单</el-button>
      </div>
    </div>
    <div v-else class="loading"><el-icon class="is-loading" :size="32"><Loading /></el-icon></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const order = ref(null)

onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/orders/${route.params.id}`)
    order.value = data
  } catch {
    ElMessage.error('加载订单详情失败')
  }
})

function statusType(status) {
  return { pending: 'info', assigned: '', in_progress: 'warning', completed: 'success', cancelled: 'danger' }[status] || 'info'
}

async function cancelOrder() {
  await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
  try {
    await axios.delete(`/api/orders/${order.value.id}`)
    ElMessage.success('订单已取消')
    router.push('/order/history')
  } catch {
    ElMessage.error('取消失败')
  }
}
</script>

<style scoped>
.order-detail-view { min-height: 100vh; background: #f5f5f5; }
.detail-content { max-width: 700px; margin: 0 auto; padding: 20px; }
.detail-content h2 { margin-bottom: 16px; }
.photo-grid { display: flex; gap: 8px; flex-wrap: wrap; }
.detail-actions { margin-top: 20px; text-align: center; }
.loading { text-align: center; padding: 60px; }
</style>
