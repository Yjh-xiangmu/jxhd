<template>
  <div class="space-y-6 max-w-3xl mx-auto">

    <!-- 孩子切换（多孩子时显示） -->
    <div v-if="children.length > 1" class="flex gap-2 flex-wrap">
      <button v-for="c in children" :key="c.studentId" @click="selectChild(c)"
        class="px-4 py-2 rounded-xl text-sm font-medium transition-all border"
        :class="currentChild?.studentId === c.studentId
          ? 'bg-[#5A5A40] text-white border-[#5A5A40]'
          : 'bg-white text-[#7A7A6A] border-[#EFE9E1] hover:border-[#5A5A40]'">
        {{ c.studentName }}
      </button>
    </div>

    <!-- 孩子 Profile 卡 -->
    <div v-if="currentChild"
      class="bg-white rounded-[32px] p-6 border border-[#EFE9E1] shadow-sm flex flex-col md:flex-row items-center relative overflow-hidden">
      <div class="absolute top-0 left-0 w-full h-1.5 bg-[#5A5A40] rounded-t-[32px]"></div>
      <!-- 头像 -->
      <div class="relative mb-4 md:mb-0 md:mr-6">
        <div class="w-20 h-20 rounded-full bg-[#F5F2ED] border-4 border-[#EFE9E1] flex items-center justify-center text-3xl font-bold text-[#5A5A40]">
          {{ currentChild.studentName.charAt(0) }}
        </div>
        <div class="absolute bottom-1 right-0 w-5 h-5 rounded-full border-4 border-white"
          :class="todayPresent ? 'bg-green-400' : 'bg-gray-300'"></div>
      </div>
      <!-- 信息 -->
      <div class="text-center md:text-left flex-1">
        <h2 class="text-xl font-bold text-[#5A5A40]">{{ currentChild.studentName }}</h2>
        <p class="text-[11px] text-[#A09E94] font-bold uppercase tracking-widest mt-1">
          {{ currentChild.className }}
          <span v-if="currentChild.gender"> · {{ currentChild.gender === 1 ? '男生' : '女生' }}</span>
        </p>
        <div class="mt-3 inline-flex items-center px-4 py-1.5 rounded-full border border-[#EFE9E1] text-[11px] font-bold text-[#5A5A40] bg-[#F5F2ED]">
          {{ todayPresent ? '今日已到园 🌟' : '今日未登记' }}
        </div>
      </div>
      <!-- 统计 -->
      <div class="mt-4 md:mt-0 md:border-l md:border-[#EFE9E1] md:pl-8 md:ml-6 flex gap-6 md:flex-col md:gap-2 text-center">
        <div>
          <p class="text-[10px] text-[#A09E94] font-bold uppercase tracking-widest">动态记录</p>
          <p class="text-2xl font-bold text-[#5A5A40] mt-0.5">{{ timeline.length }}</p>
        </div>
        <div>
          <p class="text-[10px] text-[#A09E94] font-bold uppercase tracking-widest">照片</p>
          <p class="text-2xl font-bold text-[#5A5A40] mt-0.5">{{ totalPhotos }}</p>
        </div>
      </div>
    </div>

    <!-- 加载/空状态 -->
    <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
    <div v-else-if="!currentChild"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <Baby class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无绑定孩子</p>
    </div>
    <div v-else-if="timeline.length === 0"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <Baby class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无成长记录</p>
      <p class="text-xs mt-1">老师发布动态或你记录在家表现后会出现在这里</p>
    </div>

    <!-- 时间轴 -->
    <div v-else class="relative
        before:absolute before:left-5 before:md:left-1/2 before:top-0 before:bottom-0
        before:w-px before:bg-[#D5D0C8]">
      <div v-for="(item, i) in timeline" :key="item.id + '-' + item.type"
        class="relative flex items-start mb-6 group"
        :class="i % 2 === 0 ? 'md:flex-row' : 'md:flex-row-reverse'">

        <!-- 内容卡片 -->
        <div class="ml-14 md:ml-0 w-[calc(100%-3.5rem)] md:w-[calc(50%-2.5rem)]">
          <div class="bg-white rounded-[24px] border border-[#EFE9E1] p-5 shadow-sm hover:shadow-md transition-shadow">
            <!-- 类型标签 + 时间 -->
            <div class="flex items-center justify-between mb-3">
              <span class="text-[10px] font-bold uppercase tracking-wider px-3 py-1 rounded-full"
                :class="item.type === 'school'
                  ? 'bg-[#F5F2ED] text-[#7A7A6A]'
                  : 'bg-[#5A5A40] text-white'">
                {{ item.type === 'school' ? '🏫 在园记录' : '🏠 在家表现' }}
              </span>
              <time class="text-[10px] text-[#A09E94] italic">{{ formatShortTime(item.time) }}</time>
            </div>

            <!-- 内容 -->
            <p class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap">{{ item.content }}</p>

            <!-- 图片 -->
            <div v-if="item.images && item.images.length > 0"
              class="mt-3 grid gap-1.5"
              :class="item.images.length === 1 ? 'grid-cols-1' : item.images.length <= 4 ? 'grid-cols-2' : 'grid-cols-3'">
              <img v-for="(img, j) in item.images" :key="j"
                :src="imgUrl(img)"
                @click="openLightbox(item.images, j)"
                class="w-full rounded-xl object-cover cursor-pointer hover:opacity-90 transition-opacity"
                :class="item.images.length === 1 ? 'max-h-52' : 'h-20'"
                alt="" />
            </div>

            <!-- 署名 -->
            <p class="text-[10px] text-[#A09E94] mt-3 text-right italic">— {{ item.author }}</p>
          </div>
        </div>

        <!-- 中线圆点（桌面端居中，移动端靠左） -->
        <div class="absolute left-0 md:left-1/2 md:-translate-x-1/2 top-3 w-10 h-10 rounded-full border-[3px] flex items-center justify-center z-10 shadow-sm"
          :class="item.type === 'school'
            ? 'bg-white border-[#EFE9E1] text-[#5A5A40]'
            : 'bg-[#5A5A40] border-[#5A5A40] text-white'">
          <Sun v-if="item.type === 'school'" class="w-4 h-4" />
          <HomeIcon v-else class="w-4 h-4" />
        </div>

        <!-- 占位（桌面端另一侧） -->
        <div class="hidden md:block w-[calc(50%-2.5rem)]"></div>
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
import { Baby, Sun, Home as HomeIcon, X, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { getMyChildrenRecords } from '../../api/growth'
import { getMyChildren, getHomeRecords } from '../../api/parent'
import request from '../../utils/request'

const API_BASE = 'http://localhost:8080'

const loading      = ref(false)
const children     = ref<any[]>([])
const currentChild = ref<any>(null)
const timeline     = ref<any[]>([])
const todayPresent = ref(false)

const lightbox = reactive({ show: false, images: [] as string[], index: 0 })

const totalPhotos = computed(() =>
  timeline.value.reduce((sum, item) => sum + (item.images?.length ?? 0), 0)
)

async function selectChild(child: any) {
  currentChild.value = child
  loading.value = true
  try {
    const [growthRes, homeRes]: any[] = await Promise.all([
      request.get(`/api/growth/my-child/${child.studentId}`).catch(() => ({ data: [] })),
      getHomeRecords(child.studentId).catch(() => ({ data: [] })),
    ])

    const growthItems = (growthRes.data || []).map((r: any) => ({
      id: 'g-' + r.id,
      type: 'school',
      content: r.content,
      images: r.images || [],
      time: r.createTime,
      author: r.authorName || '老师',
    }))

    const homeItems = (homeRes.data || []).map((r: any) => ({
      id: 'h-' + r.id,
      type: 'home',
      content: r.content,
      images: [],
      time: r.recordDate + 'T12:00:00',
      author: '家长记录',
    }))

    timeline.value = [...growthItems, ...homeItems]
      .sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime())

    // 检查今日是否有出勤记录
    const today = new Date().toISOString().slice(0, 10)
    todayPresent.value = growthItems.some((r: any) =>
      r.time && r.time.startsWith(today)
    )
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

function formatShortTime(t: string) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diffDays = Math.floor((now.getTime() - d.getTime()) / 86400000)
  const timeStr = d.toTimeString().slice(0, 5)
  if (diffDays === 0) return `今天 ${timeStr}`
  if (diffDays === 1) return `昨天 ${timeStr}`
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await getMyChildren()
    children.value = res.data
    if (children.value.length > 0) await selectChild(children.value[0])
  } finally { loading.value = false }
})
</script>
