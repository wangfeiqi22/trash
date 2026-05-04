<template>
  <div class="sf-view">
    <AppHeader />
    <div class="sf-content">
      <h2>站点与车队管理</h2>
      <el-tabs>
        <el-tab-pane label="回收站点">
          <el-table :data="stations" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="站点名称" />
            <el-table-column prop="address" label="地址" />
            <el-table-column prop="capacity" label="容量" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="车辆">
          <el-table :data="vehicles" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="plateNumber" label="车牌号" />
            <el-table-column prop="type" label="车辆类型" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'available' ? 'success' : row.status === 'on_duty' ? 'warning' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const stations = ref([])
const vehicles = ref([])

onMounted(async () => {
  try {
    const [s, v] = await Promise.all([
      axios.get('/api/stations').then(r => r.data),
      axios.get('/api/fleet/vehicles').then(r => r.data)
    ])
    stations.value = s || []
    vehicles.value = v || []
  } catch { ElMessage.error('加载失败') }
})
</script>

<style scoped>
.sf-view { min-height: 100vh; background: #f5f5f5; }
.sf-content { max-width: 1200px; margin: 0 auto; padding: 20px; }
.sf-content h2 { margin-bottom: 16px; }
</style>
