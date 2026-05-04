<template>
  <div class="mall-view">
    <AppHeader />
    <div class="mall-content">
      <h2>积分商城</h2>
      <el-row :gutter="16">
        <el-col :span="8" v-for="product in products" :key="product.id">
          <el-card :body-style="{ padding: '0px' }" class="product-card">
            <img :src="product.image" class="product-image" />
            <div class="product-info">
              <h4>{{ product.name }}</h4>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-price">
                <span class="points">{{ product.points }} 积分</span>
                <el-button type="primary" size="small" @click="exchange(product)">兑换</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="products.length === 0" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const products = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/mall/products')
    products.value = data || []
  } catch { /* ignore */ }
})

function exchange(product) {
  ElMessage.info(`兑换 ${product.name} 需要 ${product.points} 积分`)
}
</script>

<style scoped>
.mall-view { min-height: 100vh; background: #f5f5f5; }
.mall-content { max-width: 1200px; margin: 0 auto; padding: 20px; }
.mall-content h2 { margin-bottom: 20px; }
.product-card { margin-bottom: 16px; }
.product-image { width: 100%; height: 160px; object-fit: cover; }
.product-info { padding: 12px; }
.product-info h4 { margin: 0 0 4px; }
.product-desc { color: #666; font-size: 13px; margin: 0 0 8px; }
.product-price { display: flex; justify-content: space-between; align-items: center; }
.points { color: #F56C6C; font-weight: 700; }
</style>
