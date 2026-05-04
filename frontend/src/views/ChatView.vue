<template>
  <div class="chat-view">
    <AppHeader />
    <div class="chat-container">
      <div class="chat-messages" ref="messagesEl">
        <div v-for="msg in messages" :key="msg.id" :class="['message', msg.role]">
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-time">{{ msg.createdAt }}</div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="message-content">正在思考...</div>
        </div>
      </div>
      <div class="chat-input">
        <el-input v-model="input" placeholder="输入您的问题..." @keyup.enter="sendMessage" :disabled="loading">
          <template #append>
            <el-button :disabled="!input || loading" @click="sendMessage">发送</el-button>
          </template>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/api'
import AppHeader from '@/components/AppHeader.vue'

const messages = ref([
  { id: 0, role: 'assistant', content: '您好！我是智能垃圾分类助手。请问有什么可以帮您？', createdAt: new Date().toLocaleString() }
])
const input = ref('')
const loading = ref(false)
const messagesEl = ref(null)

async function sendMessage() {
  if (!input.value.trim() || loading.value) return
  const content = input.value.trim()
  messages.value.push({ id: Date.now(), role: 'user', content, createdAt: new Date().toLocaleString() })
  input.value = ''
  loading.value = true
  await nextTick()
  messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  try {
    const { data } = await axios.post('/api/ai/chat', { message: content })
    messages.value.push({ id: Date.now(), role: 'assistant', content: data.reply || data.message || '好的，我已收到您的问题。', createdAt: new Date().toLocaleString() })
  } catch (err) {
    ElMessage.error('发送消息失败，请重试')
  } finally {
    loading.value = false
    await nextTick()
    messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  }
}
</script>

<style scoped>
.chat-view { display: flex; flex-direction: column; height: 100vh; background: #f5f5f5; }
.chat-container { flex: 1; display: flex; flex-direction: column; max-width: 800px; margin: 0 auto; width: 100%; }
.chat-messages { flex: 1; overflow-y: auto; padding: 20px; display: flex; flex-direction: column; gap: 12px; }
.message { max-width: 70%; padding: 12px 16px; border-radius: 12px; }
.message.user { align-self: flex-end; background: #409EFF; color: white; }
.message.assistant { align-self: flex-start; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
.message-content { word-break: break-word; }
.message-time { font-size: 11px; opacity: 0.7; margin-top: 4px; }
.chat-input { padding: 16px 20px; background: white; border-top: 1px solid #eee; }
</style>
