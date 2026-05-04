<template>
  <div class="driver-profile-view">
    <AppHeader />
    <div class="profile-content">
      <h2>司机中心</h2>
      <el-card>
        <div class="profile-info">
          <el-avatar :size="64" style="background: #409EFF;">
            {{ user?.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <div class="profile-detail">
            <h3>{{ user?.username }}</h3>
            <p>司机账号</p>
            <el-tag type="success">在线</el-tag>
          </div>
        </div>
      </el-card>
      <el-card style="margin-top: 16px;" header="工作统计">
        <el-row :gutter="16">
          <el-col :span="8"><div class="stat-item"><div class="stat-num">{{ stats.completed }}</div><div class="stat-label">已完成</div></div></el-col>
          <el-col :span="8"><div class="stat-item"><div class="stat-num">{{ stats.todayCompleted }}</div><div class="stat-label">今日完成</div></div></el-col>
          <el-col :span="8"><div class="stat-item"><div class="stat-num">{{ stats.rating }}</div><div class="stat-label">评分</div></div></el-col>
        </el-row>
      </el-card>
      <el-button type="danger" plain style="width: 100%; margin-top: 20px" @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const user = computed(() => { try { return JSON.parse(localStorage.getItem('user')) } catch { return null } })
const stats = ref({ completed: 0, todayCompleted: 0, rating: '5.0' })

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/driver/profile')
    stats.value = data.stats || stats.value
  } catch { /* ignore */ }
})

function handleLogout() {
  ElMessageBox.confirm('确定退出登录？', '提示', { type: 'warning' })
    .then(() => {
      localStorage.clear()
      router.push('/login')
      ElMessage.success('已退出')
    }).catch(() => {})
}
</script>

<style scoped>
.driver-profile-view { min-height: 100vh; background: #f5f5f5; }
.profile-content { max-width: 600px; margin: 0 auto; padding: 20px; }
.profile-content h2 { margin-bottom: 16px; }
.profile-info { display: flex; align-items: center; gap: 16px; }
.profile-detail h3 { margin: 0 0 4px; }
.profile-detail p { color: #666; margin: 0 0 8px; }
.stat-item { text-align: center; }
.stat-num { font-size: 24px; font-weight: 700; color: #409EFF; }
.stat-label { color: #666; font-size: 13px; margin-top: 4px; }
</style>
