<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">成长时光机</h3>
      <p class="text-sm text-[#A09E94] mt-1">孩子在园的每一个珍贵瞬间</p>
    </div>

    <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
    <div v-else-if="records.length === 0"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <Baby class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无成长记录</p>
      <p class="text-xs mt-1">老师发布动态后会在这里显示</p>
    </div>
    <div v-else>
      <!-- 统计卡片 -->
      <div class="grid grid-cols-3 gap-4 mb-6">
        <div class="bg-white rounded-2xl border border-[#EFE9E1] p-4 text-center">
          <p class="text-2xl font-bold text-[#5A5A40]">{{ records.length }}</p>
          <p class="text-xs text-[#A09E94] mt-1">条动态</p>
        </div>
        <div class="bg-white rounded-2xl border border-[#EFE9E1] p-4 text-center">
          <p class="text-2xl font-bold text-[#5A5A40]">{{ totalPhotos }}</p>
          <p class="text-xs text-[#A09E94] mt-1">张照片</p>
        </div>
        <div class="bg-white rounded-2xl border border-[#EFE9E1] p-4 text-center">
          <p class="text-2xl font-bold text-[#5A5A40]">{{ monthSpan }}</p>
          <p class="text-xs text-[#A09E94] mt-1">个月记录</p>
        </div>
      </div>

      <!-- 按月分组的时间轴 -->
      <div v-for="group in groupedRecords" :key="group.month" class="mb-8">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-2 h-2 rounded-full bg-[#5A5A40]"></div>
          <h4 class="text-sm font-bold text-[#5A5A40]">{{ group.month }}</h4>
          <div class="flex-1 h-px bg-[#EFE9E1]"></div>
          <span class="text-xs text-[#A09E94]">{{ group.items.length }} 条</span>
        </div>

        <div class="ml-5 space-y-4">
          <div v-for="rec in group.items" :key="rec.id"
            class="bg-white rounded-2xl border border-[#EFE9E1] p-4 hover:shadow-sm transition-shadow">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-2">
                <div class="w-7 h-7 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] font-bold text-xs">
                  {{ (rec.authorName || '?').charAt(0) }}
                </div>
                <span class="text-xs text-[#7A7A6A] font-medium">{{ rec.authorName }}</span>
              </div>
              <span class="text-xs text-[#A09E94]">{{ formatTime(rec.createTime) }}</span>
            </div>

            <p class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap mb-3">{{ rec.content }}</p>

            <!-- 图片九宫格 -->
            <div v-if="rec.images && rec.images.length > 0"
              class="grid gap-1.5"
              :class="rec.images.length === 1 ? 'grid-cols-1' : rec.images.length <= 4 ? 'grid-cols-2' : 'grid-cols-3'">
              <img v-for="(img, i) in rec.images" :key="i"
                :src="imgUrl(img)"
                @click="openLightbox(rec.images, i)"
                class="w-full rounded-lg object-cover cursor-pointer hover:opacity-90 transition-opacity"
                :class="rec.images.length === 1 ? 'max-h-60' : 'h-24'"
                alt="" />
            </div>
          </div>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { Baby, X, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { getMyChildrenRecords } from '../../api/growth'

const API_BASE = 'http://localhost:8080'

const loading = ref(false)
const records = ref<any[]>([])

const lightbox = reactive({ show: false, images: [] as string[], index: 0 })

const totalPhotos = computed(() =>
  records.value.reduce((sum, r) => sum + (r.images?.length ?? 0), 0)
)

const monthSpan = computed(() => {
  if (records.value.length === 0) return 0
  const months = new Set(records.value.map(r => r.createTime?.slice(0, 7)))
  return months.size
})

const groupedRecords = computed(() => {
  const map = new Map<string, any[]>()
  for (const rec of records.value) {
    const month = rec.createTime?.slice(0, 7) ?? '未知'
    if (!map.has(month)) map.set(month, [])
    map.get(month)!.push(rec)
  }
  return Array.from(map.entries())
    .sort((a, b) => b[0].localeCompare(a[0]))
    .map(([month, items]) => ({ month, items }))
})

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
    const res: any = await getMyChildrenRecords()
    records.value = res.data
  } finally { loading.value = false }
})
</script>
