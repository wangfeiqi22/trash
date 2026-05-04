<template>
  <div class="app-sidebar" :class="{ open: modelValue }">
    <div class="sidebar-header">
      <span>菜单</span>
      <el-icon @click="$emit('update:modelValue', false)"><Close /></el-icon>
    </div>
    <el-menu :default-active="activeMenu" router>
      <el-menu-item index="/home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/order/create">
        <el-icon><Plus /></el-icon>
        <span>预约清运</span>
      </el-menu-item>
      <el-menu-item index="/order/history">
        <el-icon><List /></el-icon>
        <span>我的订单</span>
      </el-menu-item>
      <el-menu-item index="/chat">
        <el-icon><ChatDotRound /></el-icon>
        <span>AI 助手</span>
      </el-menu-item>
      <el-divider v-if="userRole === 'admin'" />
      <el-menu-item v-if="userRole === 'admin'" index="/admin/dashboard">
        <el-icon><DataBoard /></el-icon>
        <span>管理后台</span>
      </el-menu-item>
      <el-menu-item v-if="userRole === 'admin'" index="/admin/users">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item v-if="userRole === 'admin'" index="/admin/kb">
        <el-icon><Document /></el-icon>
        <span>知识库</span>
      </el-menu-item>
      <el-menu-item v-if="userRole === 'admin'" index="/admin/sf">
        <el-icon><Van /></el-icon>
        <span>站点与车队</span>
      </el-menu-item>
      <el-divider v-if="userRole === 'driver'" />
      <el-menu-item v-if="userRole === 'driver'" index="/driver/tasks">
        <el-icon><List /></el-icon>
        <span>我的任务</span>
      </el-menu-item>
      <el-menu-item v-if="userRole === 'driver'" index="/driver/orders">
        <el-icon><Tickets /></el-icon>
        <span>订单记录</span>
      </el-menu-item>
      <el-divider v-if="userRole === 'property'" />
      <el-menu-item v-if="userRole === 'property'" index="/property/dashboard">
        <el-icon><DataBoard /></el-icon>
        <span>物业工作台</span>
      </el-menu-item>
      <el-divider v-if="userRole === 'station'" />
      <el-menu-item v-if="userRole === 'station'" index="/station/work">
        <el-icon><OfficeBuilding /></el-icon>
        <span>站点工作台</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

defineProps({ modelValue: Boolean })
defineEmits(['update:modelValue'])

const route = useRoute()
const activeMenu = computed(() => route.path)
const userRole = computed(() => {
  try { return JSON.parse(localStorage.getItem('user'))?.role }
  catch { return null }
})
</script>

<style scoped>
.app-sidebar {
  position: fixed;
  left: -280px;
  top: 0;
  width: 280px;
  height: 100vh;
  background: white;
  box-shadow: 2px 0 12px rgba(0,0,0,0.1);
  transition: left 0.3s;
  z-index: 200;
  overflow-y: auto;
}
.app-sidebar.open { left: 0; }
.sidebar-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px; border-bottom: 1px solid #eee; font-weight: 600;
}
</style>
