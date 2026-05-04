<template>
  <div class="order-create-view">
    <AppHeader />
    <div class="create-content">
      <h2>预约清运</h2>
      <el-form ref="formRef" :model="form" label-position="top" :rules="rules">
        <el-form-item label="垃圾类型" prop="wasteType">
          <el-select v-model="form.wasteType" placeholder="请选择垃圾类型" style="width: 100%">
            <el-option label="可回收物" value="recyclable" />
            <el-option label="厨余垃圾" value="kitchen" />
            <el-option label="有害垃圾" value="hazardous" />
            <el-option label="其他垃圾" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" prop="pickupTime">
          <el-date-picker v-model="form.pickupTime" type="datetime" placeholder="选择上门时间" style="width: 100%" :disabled-date="disabledDate" />
        </el-form-item>
        <el-form-item label="上门地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可补充说明" />
        </el-form-item>
        <el-form-item label="上传照片">
          <el-upload action="/api/files/upload" :headers="{ Authorization: `Bearer ${token}` }" list-type="picture-card" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-button type="primary" :loading="submitting" style="width: 100%" @click="handleSubmit">
          提交预约
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const token = computed(() => localStorage.getItem('token'))
const photos = ref([])

const form = reactive({
  wasteType: '',
  pickupTime: '',
  address: '',
  remark: ''
})

const rules = {
  wasteType: [{ required: true, message: '请选择垃圾类型', trigger: 'change' }],
  pickupTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

function disabledDate(date) {
  return date < new Date()
}

function beforeUpload(file) {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  const maxSize = 10 * 1024 * 1024
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('仅支持 JPG/PNG/WEBP 格式')
    return false
  }
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

function handleUploadSuccess(res) {
  photos.value.push(res.url || res.data)
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await axios.post('/api/orders', { ...form, photos: photos.value })
    ElMessage.success('预约成功')
    router.push('/order/history')
  } catch (err) {
    ElMessage.error(err.response?.data?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.order-create-view { min-height: 100vh; background: #f5f5f5; }
.create-content { max-width: 600px; margin: 0 auto; padding: 20px; }
.create-content h2 { margin-bottom: 20px; }
</style>
