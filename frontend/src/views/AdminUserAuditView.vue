<template>
  <div class="admin-user-view">
    <AppHeader />
    <div class="content">
      <h2>用户管理</h2>
      <el-card>
        <el-table :data="users" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="role" label="角色">
            <template #default="{ row }">
              <el-tag>{{ row.role }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="audit(row, 'approve')" v-if="row.status === 'pending'">通过</el-button>
              <el-button link type="danger" size="small" @click="audit(row, 'reject')" v-if="row.status === 'pending'">拒绝</el-button>
              <el-button link v-else disabled>已审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const users = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/admin/users')
    users.value = data || []
  } catch { ElMessage.error('加载失败') }
})

async function audit(row, action) {
  try {
    await axios.post(`/api/admin/users/${row.id}/${action}`)
    ElMessage.success(action === 'approve' ? '已通过' : '已拒绝')
    row.status = action === 'approve' ? 'active' : 'rejected'
  } catch { ElMessage.error('操作失败') }
}
</script>

<style scoped>
.admin-user-view { min-height: 100vh; background: #f5f5f5; }
.content { max-width: 1200px; margin: 0 auto; padding: 20px; }
.content h2 { margin-bottom: 16px; }
</style>
