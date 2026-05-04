<template>
  <div class="app-header">
    <div class="header-left">
      <el-icon class="menu-icon" @click="sidebarOpen = !sidebarOpen"><Menu /></el-icon>
      <span class="app-title">智慧垃圾分类</span>
    </div>
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-icon><User /></el-icon>
          {{ user?.username || '用户' }}
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const sidebarOpen = ref(false)

const user = computed(() => {
  try { return JSON.parse(localStorage.getItem('user')) }
  catch { return null }
})

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
      .then(() => {
        localStorage.clear()
        router.push('/login')
        ElMessage.success('已退出登录')
      })
      .catch(() => {})
  } else if (command === 'profile') {
    const role = user.value?.role
    if (role === 'admin') router.push('/admin/dashboard')
    else if (role === 'driver') router.push('/profile/driver')
    else if (role === 'property') router.push('/profile/enterprise')
    else router.push('/profile/user')
  }
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 56px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.menu-icon { font-size: 22px; cursor: pointer; }
.app-title { font-size: 18px; font-weight: 600; color: #333; }
.header-right { display: flex; align-items: center; }
.user-info { display: flex; align-items: center; gap: 4px; cursor: pointer; color: #333; }
</style>
