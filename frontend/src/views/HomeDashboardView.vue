<template>
  <div class="home-dashboard">
    <AppHeader />
    <div class="home-content">
      <div class="welcome-section">
        <h2>欢迎使用智慧垃圾分类系统</h2>
        <p>一键预约，轻松清运</p>
      </div>
      <div class="action-cards">
        <el-card class="action-card" @click="router.push('/order/create')">
          <el-icon :size="40" color="#67C23A"><Plus /></el-icon>
          <h3>预约清运</h3>
          <p>拍照上传，智能识别</p>
        </el-card>
        <el-card class="action-card" @click="router.push('/order/history')">
          <el-icon :size="40" color="#409EFF"><List /></el-icon>
          <h3>我的订单</h3>
          <p>查看订单历史记录</p>
        </el-card>
        <el-card class="action-card" @click="router.push('/chat')">
          <el-icon :size="40" color="#E6A23C"><ChatDotRound /></el-icon>
          <h3>AI 助手</h3>
          <p>智能垃圾分类咨询</p>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
</script>

<style scoped>
.home-dashboard { min-height: 100vh; background: #f5f5f5; }
.home-content { max-width: 800px; margin: 0 auto; padding: 20px; }
.welcome-section { text-align: center; padding: 40px 0; }
.welcome-section h2 { font-size: 24px; margin-bottom: 8px; }
.welcome-section p { color: #666; }
.action-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; }
.action-card { cursor: pointer; text-align: center; transition: transform 0.2s; }
.action-card:hover { transform: translateY(-4px); }
.action-card h3 { margin: 12px 0 4px; }
.action-card p { color: #999; font-size: 13px; }
</style>
