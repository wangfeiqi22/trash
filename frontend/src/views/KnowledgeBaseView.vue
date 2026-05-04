<template>
  <div class="kb-view">
    <AppHeader />
    <div class="kb-content">
      <h2>知识库管理</h2>
      <el-card>
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索知识库..." clearable />
        </div>
        <el-table :data="filteredItems" stripe style="margin-top: 16px;">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="question" label="问题" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" style="margin-top: 16px;" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const keyword = ref('')
const items = ref([])
const page = ref(1)
const total = computed(() => items.value.length)
const filteredItems = computed(() => {
  const kw = keyword.value.toLowerCase()
  return items.value.filter(i => !kw || i.question.toLowerCase().includes(kw)).slice((page.value - 1) * 10, page.value * 10)
})

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/knowledge-base')
    items.value = data || []
  } catch { ElMessage.error('加载失败') }
})

function viewDetail(row) {
  console.log('viewDetail', row)
}
</script>

<style scoped>
.kb-view { min-height: 100vh; background: #f5f5f5; }
.kb-content { max-width: 1000px; margin: 0 auto; padding: 20px; }
.kb-content h2 { margin-bottom: 16px; }
</style>
