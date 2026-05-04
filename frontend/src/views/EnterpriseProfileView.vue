<template>
  <div class="enterprise-profile-view">
    <AppHeader />
    <div class="profile-content">
      <h2>企业中心</h2>
      <el-card>
        <div class="profile-info">
          <el-avatar :size="64" style="background: #E6A23C;">
            {{ user?.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <div>
            <h3>{{ user?.username }}</h3>
            <el-tag type="warning">物业账号</el-tag>
          </div>
        </div>
      </el-card>
      <el-button type="danger" plain style="width: 100%; margin-top: 20px" @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const user = computed(() => { try { return JSON.parse(localStorage.getItem('user')) } catch { return null } })

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
.enterprise-profile-view { min-height: 100vh; background: #f5f5f5; }
.profile-content { max-width: 600px; margin: 0 auto; padding: 20px; }
.profile-content h2 { margin-bottom: 16px; }
.profile-info { display: flex; align-items: center; gap: 16px; }
.profile-info h3 { margin: 0 0 8px; }
</style>
