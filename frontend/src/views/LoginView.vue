<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">智慧垃圾分类系统</h1>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名 / 手机号" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名或手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="普通用户" value="user" />
            <el-option label="物业" value="property" />
            <el-option label="司机" value="driver" />
            <el-option label="回收站" value="station" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%; margin-top: 8px" @click="handleLogin">
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  role: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const roleRedirect = {
  admin: '/admin/dashboard',
  driver: '/driver/tasks',
  property: '/property/dashboard',
  station: '/station/work',
  user: '/home'
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const { data } = await axios.post('/api/auth/login', {
      username: form.username,
      password: form.password,
      role: form.role
    })
    localStorage.setItem('token', data.token || data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken || '')
    localStorage.setItem('user', JSON.stringify({ ...data.user, role: form.role }))
    ElMessage.success('登录成功')
    router.push(roleRedirect[form.role] || '/home')
  } catch (err) {
    ElMessage.error(err.response?.data?.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 400px;
}
.login-title {
  text-align: center;
  margin-bottom: 32px;
  color: #333;
  font-size: 24px;
}
</style>
