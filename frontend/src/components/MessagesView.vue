<template>
  <div class="flex gap-0 h-[calc(100vh-128px)] bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">

    <!-- 左侧：会话 + 联系人 -->
    <div class="w-72 border-r border-[#EFE9E1] flex flex-col shrink-0">
      <!-- 标题栏 -->
      <div class="px-4 py-4 border-b border-[#EFE9E1]">
        <h4 class="text-base font-bold text-[#5A5A40]">{{ title }}</h4>
      </div>

      <!-- Tab -->
      <div class="flex border-b border-[#EFE9E1]">
        <button v-for="t in ['会话', '联系人']" :key="t" @click="sideTab = t"
          class="flex-1 py-2.5 text-xs font-bold transition-colors"
          :class="sideTab === t ? 'text-[#5A5A40] border-b-2 border-[#5A5A40]' : 'text-[#A09E94] hover:text-[#7A7A6A]'">
          {{ t }}
          <span v-if="t === '会话' && totalUnread > 0"
            class="ml-1 inline-flex items-center justify-center w-4 h-4 bg-red-500 text-white text-[9px] rounded-full font-bold">
            {{ totalUnread > 9 ? '9+' : totalUnread }}
          </span>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto">
        <!-- 会话列表 -->
        <div v-if="sideTab === '会话'">
          <div v-if="conversations.length === 0" class="text-center py-10 text-xs text-[#A09E94]">暂无会话</div>
          <button v-for="conv in conversations" :key="conv.peerId"
            @click="openThread(conv.peerId, conv.peerName)"
            class="w-full text-left px-4 py-3 border-b border-[#F5F2ED] hover:bg-[#F5F2ED] transition-colors"
            :class="activePeerId === conv.peerId ? 'bg-[#F5F2ED]' : ''">
            <div class="flex items-center gap-3">
              <div class="relative shrink-0">
                <div class="w-9 h-9 rounded-full bg-[#EFE9E1] flex items-center justify-center font-bold text-sm text-[#5A5A40]">
                  {{ (conv.peerName || '?').charAt(0) }}
                </div>
                <span v-if="conv.unreadCount > 0"
                  class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 text-white text-[9px] rounded-full flex items-center justify-center font-bold">
                  {{ conv.unreadCount > 9 ? '9+' : conv.unreadCount }}
                </span>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-bold text-[#333322] truncate">{{ conv.peerName }}</span>
                  <span class="text-[10px] text-[#A09E94] shrink-0 ml-1">{{ formatShortTime(conv.lastTime) }}</span>
                </div>
                <p class="text-xs text-[#A09E94] truncate mt-0.5">
                  <span class="text-[10px] text-[#A09E94] mr-1">{{ conv.peerRole }}</span>
                  {{ conv.lastMessage }}
                </p>
              </div>
            </div>
          </button>
        </div>

        <!-- 联系人列表 -->
        <div v-if="sideTab === '联系人'">
          <div v-if="contacts.length === 0" class="text-center py-10 text-xs text-[#A09E94]">暂无可联系人</div>
          <button v-for="c in contacts" :key="c.id"
            @click="openThread(c.id, c.name)"
            class="w-full text-left px-4 py-3 border-b border-[#F5F2ED] hover:bg-[#F5F2ED] transition-colors"
            :class="activePeerId === c.id ? 'bg-[#F5F2ED]' : ''">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-full bg-[#EFE9E1] flex items-center justify-center font-bold text-sm text-[#5A5A40] shrink-0">
                {{ (c.name || '?').charAt(0) }}
              </div>
              <div>
                <p class="text-sm font-bold text-[#333322]">{{ c.name }}</p>
                <p class="text-xs text-[#A09E94]">{{ c.role }}</p>
              </div>
            </div>
          </button>
        </div>
      </div>
    </div>

    <!-- 右侧：消息线程 -->
    <div class="flex-1 flex flex-col min-w-0">
      <!-- 空状态 -->
      <div v-if="!activePeerId" class="flex-1 flex flex-col items-center justify-center text-[#A09E94]">
        <MessageCircle class="w-14 h-14 mb-3 opacity-20" />
        <p class="text-sm font-medium">从左侧选择一个会话开始沟通</p>
      </div>

      <template v-else>
        <!-- 对话头部 -->
        <div class="px-6 py-4 border-b border-[#EFE9E1] flex items-center gap-3 shrink-0">
          <div class="w-9 h-9 rounded-full bg-[#EFE9E1] flex items-center justify-center font-bold text-sm text-[#5A5A40]">
            {{ (activePeerName || '?').charAt(0) }}
          </div>
          <div>
            <p class="text-sm font-bold text-[#333322]">{{ activePeerName }}</p>
          </div>
        </div>

        <!-- 消息列表 -->
        <div ref="msgListEl" class="flex-1 overflow-y-auto px-6 py-4 space-y-3">
          <div v-if="threadLoading" class="text-center py-8 text-xs text-[#A09E94]">加载中...</div>
          <div v-else-if="thread.length === 0" class="text-center py-8 text-xs text-[#A09E94]">暂无消息，发送第一条吧</div>
          <div v-for="msg in thread" :key="msg.id"
            class="flex" :class="msg.isMine ? 'justify-end' : 'justify-start'">
            <div class="max-w-[70%]">
              <div class="px-4 py-2.5 rounded-2xl text-sm leading-relaxed"
                :class="msg.isMine
                  ? 'bg-[#5A5A40] text-white rounded-br-sm'
                  : 'bg-[#F5F2ED] text-[#333322] rounded-bl-sm'">
                {{ msg.content }}
              </div>
              <p class="text-[10px] text-[#A09E94] mt-1" :class="msg.isMine ? 'text-right' : ''">
                {{ formatTime(msg.createTime) }}
              </p>
            </div>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="px-6 py-4 border-t border-[#EFE9E1] flex gap-3 shrink-0">
          <textarea v-model="inputText"
            @keydown.enter.exact.prevent="sendMessage"
            rows="2" placeholder="输入消息，按 Enter 发送..."
            class="flex-1 px-4 py-2.5 rounded-xl border border-[#EFE9E1] text-sm outline-none resize-none focus:border-[#5A5A40]" />
          <button @click="sendMessage" :disabled="sending || !inputText.trim()"
            class="h-10 px-5 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] disabled:opacity-50 self-end transition-colors">
            发送
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { MessageCircle } from 'lucide-vue-next'
import request from '../utils/request'

const props = defineProps<{ title?: string }>()

const sideTab        = ref('会话')
const conversations  = ref<any[]>([])
const contacts       = ref<any[]>([])
const activePeerId   = ref<number | string | null>(null)
const activePeerName = ref('')
const thread         = ref<any[]>([])
const threadLoading  = ref(false)
const inputText      = ref('')
const sending        = ref(false)
const msgListEl      = ref<HTMLElement | null>(null)
let pollTimer: ReturnType<typeof setInterval> | null = null

const totalUnread = computed(() =>
  conversations.value.reduce((sum, c) => sum + (c.unreadCount ?? 0), 0)
)

async function fetchConversations() {
  const res: any = await request.get('/api/messages/conversations')
  conversations.value = res.data
}

async function fetchContacts() {
  const res: any = await request.get('/api/messages/contacts')
  contacts.value = res.data
}

async function openThread(peerId: number | string, peerName: string) {
  activePeerId.value   = peerId
  activePeerName.value = peerName
  threadLoading.value  = true
  try {
    const res: any = await request.get(`/api/messages/thread/${peerId}`)
    thread.value = res.data
    // 标记已读后刷新会话列表
    await fetchConversations()
    scrollToBottom()
  } finally { threadLoading.value = false }
}

async function sendMessage() {
  if (!inputText.value.trim() || !activePeerId.value || sending.value) return
  const content = inputText.value.trim()
  sending.value   = true
  inputText.value = ''
  try {
    await request.post('/api/messages/send', { toId: activePeerId.value, content })
    await refreshThread()
  } finally { sending.value = false }
}

async function refreshThread() {
  if (!activePeerId.value) return
  const res: any = await request.get(`/api/messages/thread/${activePeerId.value}`)
  thread.value = res.data
  scrollToBottom()
  fetchConversations()
}

function scrollToBottom() {
  nextTick(() => {
    if (msgListEl.value) msgListEl.value.scrollTop = msgListEl.value.scrollHeight
  })
}

function formatTime(t: string) { return t ? t.replace('T', ' ').slice(0, 16) : '' }

function formatShortTime(t: string) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) return d.toTimeString().slice(0, 5)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

onMounted(async () => {
  await Promise.all([fetchConversations(), fetchContacts()])
  pollTimer = setInterval(async () => {
    await fetchConversations()
    if (activePeerId.value) await refreshThread()
  }, 5000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>
