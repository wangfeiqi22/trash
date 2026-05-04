<template>
  <el-popover :width="320" trigger="click" placement="top-end">
    <template #reference>
      <div class="ai-avatar-btn" @click="openChat">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="9">
          <el-icon :size="24" color="white"><Service /></el-icon>
        </el-badge>
      </div>
    </template>
    <template #default>
      <div class="mini-chat">
        <div class="mini-chat-header">AI 助手</div>
        <div class="mini-chat-messages" ref="messagesEl">
          <div v-for="msg in messages" :key="msg.id" :class="['mini-msg', msg.role]">
            {{ msg.content }}
          </div>
        </div>
        <el-input v-model="input" size="small" placeholder="输入..." @keyup.enter="sendMessage">
          <template #append>
            <el-button @click="sendMessage" :disabled="!input">发送</el-button>
          </template>
        </el-input>
      </div>
    </template>
  </el-popover>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import axios from '@/api'

const messages = ref([{ id: 0, role: 'assistant', content: '您好，有什么可以帮您？' }])
const input = ref('')
const unreadCount = ref(0)
const messagesEl = ref(null)

function openChat() {
  unreadCount.value = 0
}

async function sendMessage() {
  if (!input.value.trim()) return
  const content = input.value.trim()
  messages.value.push({ id: Date.now(), role: 'user', content })
  input.value = ''
  await nextTick()
  messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  try {
    const { data } = await axios.post('/api/ai/chat', { message: content })
    const reply = data.reply || data.message || '好的'
    messages.value.push({ id: Date.now(), role: 'assistant', content: reply })
    unreadCount.value++
  } catch {
    messages.value.push({ id: Date.now(), role: 'assistant', content: '抱歉，服务暂时不可用。' })
  }
}
</script>

<style scoped>
.ai-avatar-btn {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  z-index: 1000;
}
.mini-chat { display: flex; flex-direction: column; gap: 8px; }
.mini-chat-header { font-weight: 600; font-size: 15px; border-bottom: 1px solid #eee; padding-bottom: 8px; }
.mini-chat-messages { max-height: 200px; overflow-y: auto; display: flex; flex-direction: column; gap: 6px; }
.mini-msg { padding: 6px 10px; border-radius: 8px; font-size: 13px; }
.mini-msg.user { align-self: flex-end; background: #409EFF; color: white; }
.mini-msg.assistant { align-self: flex-start; background: #f0f0f0; color: #333; }
</style>
