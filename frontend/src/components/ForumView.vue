<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h3 class="text-2xl font-bold text-[#5A5A40]">{{ title }}</h3>
        <p class="text-sm text-[#A09E94] mt-1">家园互动交流空间</p>
      </div>
      <button @click="openNewPost"
        class="flex items-center gap-2 px-4 py-2 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
        <Plus class="w-4 h-4" /> 发帖
      </button>
    </div>

    <!-- 搜索 -->
    <div class="flex items-center gap-3">
      <input v-model="keyword" @keyup.enter="search" placeholder="搜索帖子标题..."
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none w-52 focus:border-[#5A5A40]" />
      <button @click="search" class="h-9 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35]">搜索</button>
      <button @click="resetSearch" class="h-9 px-4 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED]">重置</button>
    </div>

    <!-- 帖子列表 -->
    <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
    <div v-else-if="posts.length === 0"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <MessageSquare class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无帖子</p>
      <p class="text-xs mt-1">来发第一帖吧</p>
    </div>
    <div v-else class="space-y-2">
      <div v-for="post in posts" :key="post.id"
        @click="openPost(post)"
        class="bg-white rounded-2xl border border-[#EFE9E1] px-5 py-4 cursor-pointer hover:shadow-sm hover:border-[#5A5A40]/20 transition-all">
        <div class="flex items-start gap-3">
          <Pin v-if="post.isPinned === 1" class="w-4 h-4 text-orange-500 mt-0.5 shrink-0" />
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 flex-wrap mb-1">
              <span v-if="post.isPinned === 1"
                class="text-[10px] px-2 py-0.5 bg-orange-50 text-orange-500 rounded-full font-bold">置顶</span>
              <p class="text-sm font-bold text-[#333322] truncate">{{ post.title }}</p>
            </div>
            <div class="flex items-center gap-3 text-xs text-[#A09E94]">
              <span class="inline-flex items-center gap-1">
                <div class="w-4 h-4 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[8px] font-bold text-[#5A5A40]">
                  {{ (post.authorName || '?').charAt(0) }}
                </div>
                {{ post.authorName }}
                <span class="text-[10px] px-1.5 py-0.5 bg-[#F5F2ED] rounded text-[#A09E94] font-medium">{{ post.authorRole }}</span>
              </span>
              <span>{{ formatTime(post.createTime) }}</span>
              <span class="flex items-center gap-1">
                <MessageCircle class="w-3 h-3" /> {{ post.replyCount }} 回复
              </span>
              <span v-if="post.images && post.images.length > 0" class="flex items-center gap-1">
                <ImageIcon class="w-3 h-3" /> {{ post.images.length }} 图
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="flex items-center justify-center gap-3">
      <button @click="prevPage" :disabled="pageNum === 1"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">上一页</button>
      <span class="text-xs text-[#5A5A40] font-medium">{{ pageNum }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="pageNum >= totalPages"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">下一页</button>
    </div>

    <!-- ── 帖子详情面板 ── -->
    <div v-if="detail.show" class="fixed inset-0 bg-black/40 flex items-start justify-center z-50 p-4 overflow-y-auto">
      <div class="bg-white rounded-3xl w-full max-w-2xl shadow-2xl my-4">
        <!-- 帖子头部 -->
        <div class="px-7 py-5 border-b border-[#EFE9E1] flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 flex-wrap mb-2">
              <span v-if="detail.post?.isPinned === 1"
                class="text-[10px] px-2 py-0.5 bg-orange-50 text-orange-500 rounded-full font-bold">置顶</span>
              <h4 class="text-lg font-bold text-[#333322]">{{ detail.post?.title }}</h4>
            </div>
            <div class="flex items-center gap-2 text-xs text-[#A09E94]">
              <span class="font-medium text-[#7A7A6A]">{{ detail.post?.authorName }}</span>
              <span class="text-[10px] px-1.5 py-0.5 bg-[#F5F2ED] rounded">{{ detail.post?.authorRole }}</span>
              <span>{{ formatTime(detail.post?.createTime) }}</span>
            </div>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <button v-if="isAdmin" @click="handlePin(detail.post)"
              class="h-8 px-3 rounded-xl border text-xs font-bold transition-colors"
              :class="detail.post?.isPinned === 1
                ? 'border-orange-200 text-orange-500 hover:bg-orange-50'
                : 'border-[#EFE9E1] text-[#7A7A6A] hover:bg-[#F5F2ED]'">
              {{ detail.post?.isPinned === 1 ? '取消置顶' : '置顶' }}
            </button>
            <button v-if="canDelete(detail.post)"
              @click="deleteCurrentPost"
              class="h-8 px-3 rounded-xl border border-red-200 text-red-400 text-xs font-bold hover:bg-red-50 transition-colors">
              删帖
            </button>
            <button @click="detail.show = false">
              <X class="w-5 h-5 text-[#A09E94] hover:text-[#5A5A40]" />
            </button>
          </div>
        </div>

        <!-- 帖子内容 -->
        <div class="px-7 py-5 border-b border-[#F5F2ED]">
          <p class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap">{{ detail.post?.content }}</p>
          <!-- 图片 -->
          <div v-if="detail.post?.images && detail.post.images.length > 0"
            class="mt-4 grid gap-2"
            :class="detail.post.images.length === 1 ? 'grid-cols-1' : detail.post.images.length <= 4 ? 'grid-cols-2' : 'grid-cols-3'">
            <img v-for="(img, i) in detail.post.images" :key="i"
              :src="imgUrl(img)"
              @click="openLightbox(detail.post.images, i)"
              class="w-full rounded-xl object-cover cursor-pointer hover:opacity-90 transition-opacity"
              :class="detail.post.images.length === 1 ? 'max-h-64' : 'h-28'"
              alt="" />
          </div>
        </div>

        <!-- 回复列表 -->
        <div class="px-7 py-5 max-h-80 overflow-y-auto space-y-4">
          <p class="text-xs font-bold uppercase tracking-widest text-[#A09E94]">
            {{ detail.replies.length }} 条回复
          </p>
          <div v-if="detail.replies.length === 0" class="text-center py-6 text-xs text-[#A09E94]">暂无回复</div>
          <div v-for="r in detail.replies" :key="r.id" class="flex items-start gap-3">
            <div class="w-8 h-8 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] font-bold text-xs shrink-0">
              {{ (r.authorName || '?').charAt(0) }}
            </div>
            <div class="flex-1 min-w-0 bg-[#FDFCF8] rounded-xl px-4 py-3">
              <div class="flex items-center gap-2 mb-1">
                <span class="text-xs font-bold text-[#333322]">{{ r.authorName }}</span>
                <span class="text-[10px] px-1.5 py-0.5 bg-[#F5F2ED] rounded text-[#A09E94]">{{ r.authorRole }}</span>
                <span class="text-[10px] text-[#A09E94] ml-auto">{{ formatTime(r.createTime) }}</span>
                <button v-if="canDeleteReply(r)" @click="deleteRep(r)"
                  class="text-[#A09E94] hover:text-red-400 transition-colors ml-1">
                  <Trash2 class="w-3.5 h-3.5" />
                </button>
              </div>
              <p class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap">{{ r.content }}</p>
            </div>
          </div>
        </div>

        <!-- 回复框 -->
        <div class="px-7 py-5 border-t border-[#EFE9E1] flex gap-3">
          <textarea v-model="replyContent" rows="2" placeholder="写下你的回复..."
            class="flex-1 px-3 py-2 rounded-xl border border-[#EFE9E1] text-sm outline-none resize-none focus:border-[#5A5A40]" />
          <button @click="submitReply" :disabled="replying"
            class="h-9 px-5 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] disabled:opacity-50 self-end">
            {{ replying ? '发送中' : '回复' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 发帖弹窗 -->
    <div v-if="newPost.show" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-3xl w-full max-w-lg shadow-2xl max-h-[90vh] overflow-y-auto">
        <div class="px-7 py-5 bg-[#F5F2ED] border-b border-[#EFE9E1] rounded-t-3xl flex items-center justify-between sticky top-0">
          <h4 class="text-base font-bold text-[#5A5A40]">{{ newPost.modalTitle || '发新帖' }}</h4>
          <button @click="newPost.show = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="p-7 space-y-4">
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">标题</label>
            <input v-model="newPost.title" placeholder="请输入帖子标题"
              class="w-full h-10 px-3 rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">内容</label>
            <textarea v-model="newPost.content" rows="5" placeholder="写下你想分享的内容..."
              class="w-full px-3 py-2 rounded-xl border border-[#EFE9E1] text-sm outline-none resize-none focus:border-[#5A5A40]" />
          </div>
          <!-- 图片上传 -->
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">
              附图（最多9张）
            </label>
            <div class="flex flex-wrap gap-2">
              <div v-for="(img, i) in newPost.images" :key="i"
                class="relative w-20 h-20 rounded-xl overflow-hidden border border-[#EFE9E1]">
                <img :src="imgUrl(img)" class="w-full h-full object-cover" alt="" />
                <button @click="newPost.images.splice(i, 1)"
                  class="absolute top-0.5 right-0.5 w-5 h-5 bg-black/50 rounded-full flex items-center justify-center">
                  <X class="w-3 h-3 text-white" />
                </button>
              </div>
              <label v-if="newPost.images.length < 9"
                class="w-20 h-20 rounded-xl border-2 border-dashed border-[#EFE9E1] flex flex-col items-center justify-center cursor-pointer hover:border-[#5A5A40] transition-colors">
                <Plus class="w-5 h-5 text-[#A09E94]" />
                <span class="text-[10px] text-[#A09E94] mt-1">
                  {{ newPost.uploading ? '上传中' : '添加' }}
                </span>
                <input type="file" accept="image/*" class="hidden" :disabled="newPost.uploading"
                  @change="uploadNewPostImage" />
              </label>
            </div>
          </div>
        </div>
        <div class="px-7 pb-7 flex gap-3">
          <button @click="newPost.show = false"
            class="flex-1 h-9 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED]">取消</button>
          <button @click="submitPost" :disabled="newPost.submitting"
            class="flex-1 h-9 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] disabled:opacity-50">
            {{ newPost.submitting ? '发布中...' : '发布' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 灯箱 -->
    <div v-if="lightbox.show" class="fixed inset-0 bg-black/90 flex items-center justify-center z-[60]"
      @click="lightbox.show = false">
      <button class="absolute top-4 right-4 text-white/70 hover:text-white" @click="lightbox.show = false">
        <X class="w-7 h-7" />
      </button>
      <button v-if="lightbox.index > 0" @click.stop="lightbox.index--"
        class="absolute left-4 text-white/70 hover:text-white p-2">
        <ChevronLeft class="w-8 h-8" />
      </button>
      <img :src="imgUrl(lightbox.images[lightbox.index])"
        class="max-w-[90vw] max-h-[90vh] object-contain rounded-xl" @click.stop alt="" />
      <button v-if="lightbox.index < lightbox.images.length - 1" @click.stop="lightbox.index++"
        class="absolute right-4 text-white/70 hover:text-white p-2">
        <ChevronRight class="w-8 h-8" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, MessageSquare, MessageCircle, X, Pin, Trash2, Image as ImageIcon, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { getForumPosts, getPostDetail, addPost, deletePost, togglePin, addReply, deleteReply, uploadForumImage } from '../api/forum'
import request from '../utils/request'

const API_BASE = 'http://localhost:8080'

const props = defineProps<{ isAdmin?: boolean; title?: string }>()

// Exposed method so parent pages can open the new-post modal with prefilled data
const openNewPostWith = (opts: { title?: string; content?: string; images?: string[] }) => {
  newPost.title      = opts.title   ?? ''
  newPost.content    = opts.content ?? ''
  newPost.images     = opts.images  ? [...opts.images] : []
  newPost.modalTitle = opts.title ? '发布活动总结' : '发新帖'
  newPost.show       = true
}
defineExpose({ openNewPostWith })

const loading  = ref(false)
const posts    = ref<any[]>([])
const total    = ref(0)
const pageNum  = ref(1)
const pageSize = ref(20)
const keyword  = ref('')
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const currentUser = ref<any>(null)

const detail = reactive({
  show: false,
  post: null as any,
  replies: [] as any[],
})
const replyContent = ref('')
const replying = ref(false)

const newPost = reactive({
  show: false,
  modalTitle: '',
  title: '',
  content: '',
  images: [] as string[],
  uploading: false,
  submitting: false,
})

const lightbox = reactive({ show: false, images: [] as string[], index: 0 })

function imgUrl(path: string) {
  if (!path) return ''
  return path.startsWith('http') ? path : API_BASE + path
}

function openLightbox(images: string[], index: number) {
  lightbox.images = images
  lightbox.index  = index
  lightbox.show   = true
}

async function fetchPosts() {
  loading.value = true
  try {
    const res: any = await getForumPosts({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined })
    posts.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function search() { pageNum.value = 1; fetchPosts() }
function resetSearch() { keyword.value = ''; pageNum.value = 1; fetchPosts() }
function prevPage() { if (pageNum.value > 1) { pageNum.value--; fetchPosts() } }
function nextPage() { if (pageNum.value < totalPages.value) { pageNum.value++; fetchPosts() } }

async function openPost(post: any) {
  detail.post    = post
  detail.replies = []
  detail.show    = true
  replyContent.value = ''
  const res: any = await getPostDetail(post.id)
  detail.post    = res.data.post
  detail.replies = res.data.replies
}

function canDelete(post: any) {
  if (!post || !currentUser.value) return false
  return post.authorId === currentUser.value.id || props.isAdmin
}
function canDeleteReply(reply: any) {
  if (!currentUser.value) return false
  return reply.authorId === currentUser.value.id || props.isAdmin
}

async function deleteCurrentPost() {
  if (!confirm('确认删除该帖子及其所有回复？')) return
  await deletePost(detail.post.id)
  detail.show = false
  fetchPosts()
}

async function deleteRep(r: any) {
  if (!confirm('确认删除该回复？')) return
  await deleteReply(r.id)
  const res: any = await getPostDetail(detail.post.id)
  detail.post    = res.data.post
  detail.replies = res.data.replies
  fetchPosts()
}

async function handlePin(post: any) {
  await togglePin(post.id)
  const res: any = await getPostDetail(post.id)
  detail.post = res.data.post
  fetchPosts()
}

function openNewPost() {
  newPost.title      = ''
  newPost.content    = ''
  newPost.images     = []
  newPost.modalTitle = ''
  newPost.show       = true
}

async function uploadNewPostImage(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  newPost.uploading = true
  try {
    const res: any = await uploadForumImage(file)
    newPost.images.push(res.data)
  } finally {
    newPost.uploading = false
    ;(e.target as HTMLInputElement).value = ''
  }
}

async function submitPost() {
  if (!newPost.title.trim())   { alert('请填写标题'); return }
  if (!newPost.content.trim()) { alert('请填写内容'); return }
  newPost.submitting = true
  try {
    await addPost({ title: newPost.title, content: newPost.content, images: newPost.images })
    newPost.show = false
    pageNum.value = 1
    fetchPosts()
  } finally { newPost.submitting = false }
}

async function submitReply() {
  if (!replyContent.value.trim()) { alert('请填写回复内容'); return }
  replying.value = true
  try {
    await addReply(detail.post.id, replyContent.value)
    replyContent.value = ''
    const res: any = await getPostDetail(detail.post.id)
    detail.post    = res.data.post
    detail.replies = res.data.replies
    fetchPosts()
  } finally { replying.value = false }
}

function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

onMounted(async () => {
  try {
    const res: any = await request.get('/api/auth/current')
    currentUser.value = res.data
  } catch { /* ignore */ }
  fetchPosts()
})
</script>
