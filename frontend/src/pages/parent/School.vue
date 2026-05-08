<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">在园动态</h3>
      <p class="text-sm text-[#A09E94] mt-1">老师记录的孩子在园精彩瞬间</p>
    </div>

    <!-- 孩子切换（如果绑定了多个孩子） -->
    <div v-if="children.length > 1" class="flex gap-2 flex-wrap">
      <button v-for="c in children" :key="c.studentId"
        @click="selectChild(c)"
        class="px-4 py-2 rounded-xl text-sm font-medium transition-all border"
        :class="currentChild?.studentId === c.studentId
          ? 'bg-[#5A5A40] text-white border-[#5A5A40]'
          : 'bg-white text-[#7A7A6A] border-[#EFE9E1] hover:border-[#5A5A40]'">
        {{ c.studentName }}
      </button>
    </div>

    <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
    <div v-else-if="!currentChild" class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <Sun class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无绑定孩子</p>
      <p class="text-xs mt-1">请联系老师绑定孩子账号</p>
    </div>
    <div v-else-if="records.length === 0"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <Camera class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无动态记录</p>
      <p class="text-xs mt-1">老师还未发布动态，敬请期待</p>
    </div>
    <div v-else class="space-y-4">
      <div v-for="rec in records" :key="rec.id"
        class="bg-white rounded-2xl border border-[#EFE9E1] p-5 hover:shadow-sm transition-shadow">
        <!-- 头部：老师 + 时间 -->
        <div class="flex items-center gap-3 mb-4">
          <div class="w-9 h-9 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] font-bold text-sm shrink-0">
            {{ (rec.authorName || '?').charAt(0) }}
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-bold text-[#333322]">{{ rec.authorName }}</p>
            <p class="text-xs text-[#A09E94]">{{ formatTime(rec.createTime) }}</p>
          </div>
          <span class="text-xs px-3 py-1 bg-[#F5F2ED] rounded-full text-[#5A5A40] font-medium">{{ rec.className }}</span>
        </div>

        <!-- 文字内容 -->
        <p class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap mb-4">{{ rec.content }}</p>

        <!-- 图片 -->
        <div v-if="rec.images && rec.images.length > 0"
          class="grid gap-2"
          :class="rec.images.length === 1 ? 'grid-cols-1' : rec.images.length === 2 ? 'grid-cols-2' : 'grid-cols-3'">
          <img v-for="(img, i) in rec.images" :key="i"
            :src="imgUrl(img)"
            @click="openLightbox(rec.images, i)"
            class="w-full rounded-xl object-cover cursor-pointer hover:opacity-90 transition-opacity"
            :class="rec.images.length === 1 ? 'max-h-72' : 'h-28'"
            alt="" />
        </div>
      </div>
    </div>

    <!-- 图片灯箱 -->
    <div v-if="lightbox.show" class="fixed inset-0 bg-black/90 flex items-center justify-center z-50"
      @click="lightbox.show = false">
      <button class="absolute top-4 right-4 text-white/70 hover:text-white" @click="lightbox.show = false">
        <X class="w-7 h-7" />
      </button>
      <button v-if="lightbox.index > 0" @click.stop="lightbox.index--"
        class="absolute left-4 text-white/70 hover:text-white p-2">
        <ChevronLeft class="w-8 h-8" />
      </button>
      <img :src="imgUrl(lightbox.images[lightbox.index])"
        class="max-w-[90vw] max-h-[90vh] object-contain rounded-xl"
        @click.stop alt="" />
      <button v-if="lightbox.index < lightbox.images.length - 1" @click.stop="lightbox.index++"
        class="absolute right-4 text-white/70 hover:text-white p-2">
        <ChevronRight class="w-8 h-8" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Sun, Camera, X, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { getMyChildrenRecords, getMyChildRecords } from '../../api/growth'
import request from '../../utils/request'

const API_BASE = 'http://localhost:8080'

const loading      = ref(false)
const children     = ref<any[]>([])
const currentChild = ref<any>(null)
const records      = ref<any[]>([])

const lightbox = reactive({ show: false, images: [] as string[], index: 0 })

async function loadChildren() {
  // 获取绑定的孩子列表（已通过）
  const res: any = await request.get('/api/parent/my-children')
  children.value = res.data
  if (children.value.length > 0) {
    await selectChild(children.value[0])
  }
}

async function selectChild(child: any) {
  currentChild.value = child
  loading.value = true
  try {
    const res: any = await getMyChildRecords(child.studentId)
    records.value = res.data
  } finally { loading.value = false }
}

function imgUrl(path: string) {
  if (!path) return ''
  return path.startsWith('http') ? path : API_BASE + path
}

function openLightbox(images: string[], index: number) {
  lightbox.images = images
  lightbox.index  = index
  lightbox.show   = true
}

function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

onMounted(async () => {
  loading.value = true
  try {
    await loadChildren()
  } catch {
    loading.value = false
  }
})
</script>
