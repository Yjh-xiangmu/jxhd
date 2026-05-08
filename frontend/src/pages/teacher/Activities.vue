<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">活动跟进</h3>
      <p class="text-sm text-[#A09E94] mt-1">查看活动及班级幼儿的报名情况，并发布活动总结</p>
    </div>

    <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
    <div v-else-if="activities.length === 0"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <Activity class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无活动</p>
    </div>
    <div v-else class="space-y-4">
      <div v-for="act in activities" :key="act.id"
        class="bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">
        <!-- 活动头部 -->
        <div class="p-5 flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 flex-wrap mb-2">
              <h4 class="text-base font-bold text-[#333322]">{{ act.title }}</h4>
              <span class="text-[11px] px-2.5 py-1 rounded-full font-bold"
                :class="act.status === 1 ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'">
                {{ act.status === 1 ? '报名中' : '已结束' }}
              </span>
            </div>
            <div class="flex flex-wrap gap-3 text-xs text-[#A09E94]">
              <span v-if="act.location" class="flex items-center gap-1">
                <MapPin class="w-3 h-3" /> {{ act.location }}
              </span>
              <span v-if="act.activityTime" class="flex items-center gap-1">
                <Clock class="w-3 h-3" /> {{ formatTime(act.activityTime) }}
              </span>
              <span class="flex items-center gap-1">
                <Users class="w-3 h-3" />
                {{ act.signupCount }} 人已报名
                <template v-if="act.maxCount"> / 共 {{ act.maxCount }} 名额</template>
              </span>
            </div>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <!-- 活动总结按钮（已结束的活动） -->
            <button v-if="act.status !== 1" @click="openSummary(act)"
              class="h-8 px-3 rounded-xl bg-[#5A5A40] text-white text-xs font-bold hover:bg-[#4A4A35] flex items-center gap-1 transition-colors">
              <FileText class="w-3.5 h-3.5" /> 写总结
            </button>
            <button @click="toggleSignups(act)"
              class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] flex items-center gap-1 transition-colors">
              <ChevronDown class="w-3.5 h-3.5 transition-transform"
                :class="expandedId === act.id ? 'rotate-180' : ''" />
              报名名单
            </button>
          </div>
        </div>

        <!-- 展开：报名名单 -->
        <div v-if="expandedId === act.id" class="border-t border-[#F5F2ED] px-5 py-4">
          <div v-if="signupsLoading" class="text-center py-4 text-xs text-[#A09E94]">加载中...</div>
          <div v-else-if="currentSignups.length === 0" class="text-center py-4 text-xs text-[#A09E94]">暂无报名</div>
          <div v-else>
            <div v-if="myClassSignups.length > 0" class="mb-3">
              <p class="text-[10px] font-bold uppercase text-[#A09E94] tracking-wider mb-2">本班幼儿</p>
              <div class="space-y-2">
                <div v-for="s in myClassSignups" :key="s.id"
                  class="flex items-center justify-between py-2 px-3 bg-[#F5F2ED] rounded-xl">
                  <div class="flex items-center gap-2">
                    <div class="w-7 h-7 rounded-full bg-[#5A5A40] text-white text-xs font-bold flex items-center justify-center">
                      {{ s.studentName.charAt(0) }}
                    </div>
                    <span class="text-sm font-medium text-[#333322]">{{ s.studentName }}</span>
                  </div>
                  <div class="text-xs text-[#A09E94]">
                    {{ s.parentName }}
                    <a v-if="s.parentPhone" :href="`tel:${s.parentPhone}`"
                      class="ml-2 text-blue-500 font-medium hover:underline">{{ s.parentPhone }}</a>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="otherSignups.length > 0">
              <p class="text-[10px] font-bold uppercase text-[#A09E94] tracking-wider mb-2">其他班级</p>
              <div class="space-y-2">
                <div v-for="s in otherSignups" :key="s.id"
                  class="flex items-center justify-between py-2 px-3 rounded-xl border border-[#EFE9E1]">
                  <div class="flex items-center gap-2 text-sm text-[#7A7A6A]">
                    <div class="w-6 h-6 rounded-full bg-[#EFE9E1] text-[#A09E94] text-xs font-bold flex items-center justify-center">
                      {{ s.studentName.charAt(0) }}
                    </div>
                    {{ s.studentName }}
                    <span class="text-[10px] text-[#A09E94]">{{ s.className }}</span>
                  </div>
                  <span class="text-xs text-[#A09E94]">{{ s.parentName }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 活动总结弹窗 ── -->
    <div v-if="summary.show" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-3xl w-full max-w-lg shadow-2xl max-h-[90vh] overflow-y-auto">
        <div class="px-7 py-5 bg-[#F5F2ED] border-b border-[#EFE9E1] rounded-t-3xl flex items-center justify-between sticky top-0">
          <div>
            <h4 class="text-base font-bold text-[#5A5A40]">发布活动总结</h4>
            <p class="text-xs text-[#A09E94] mt-0.5">将自动发布到论坛，家长可查看</p>
          </div>
          <button @click="summary.show = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="p-7 space-y-4">
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">标题</label>
            <input v-model="summary.title" class="w-full h-10 px-3 rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">总结内容</label>
            <textarea v-model="summary.content" rows="6" placeholder="记录活动亮点、孩子的表现、收获与感悟..."
              class="w-full px-3 py-2 rounded-xl border border-[#EFE9E1] text-sm outline-none resize-none focus:border-[#5A5A40]" />
          </div>
          <!-- 图片上传 -->
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">活动照片（最多9张）</label>
            <div class="flex flex-wrap gap-2">
              <div v-for="(img, i) in summary.images" :key="i"
                class="relative w-20 h-20 rounded-xl overflow-hidden border border-[#EFE9E1]">
                <img :src="imgUrl(img)" class="w-full h-full object-cover" alt="" />
                <button @click="summary.images.splice(i, 1)"
                  class="absolute top-0.5 right-0.5 w-5 h-5 bg-black/50 rounded-full flex items-center justify-center">
                  <X class="w-3 h-3 text-white" />
                </button>
              </div>
              <label v-if="summary.images.length < 9"
                class="w-20 h-20 rounded-xl border-2 border-dashed border-[#EFE9E1] flex flex-col items-center justify-center cursor-pointer hover:border-[#5A5A40] transition-colors">
                <Plus class="w-5 h-5 text-[#A09E94]" />
                <span class="text-[10px] text-[#A09E94] mt-1">{{ summary.uploading ? '上传中' : '添加' }}</span>
                <input type="file" accept="image/*" class="hidden" :disabled="summary.uploading"
                  @change="uploadSummaryImage" />
              </label>
            </div>
          </div>
        </div>
        <div class="px-7 pb-7 flex gap-3">
          <button @click="summary.show = false"
            class="flex-1 h-9 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED]">取消</button>
          <button @click="submitSummary" :disabled="summary.submitting"
            class="flex-1 h-9 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] disabled:opacity-50">
            {{ summary.submitting ? '发布中...' : '发布到论坛' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Activity, MapPin, Clock, Users, ChevronDown, FileText, Plus, X } from 'lucide-vue-next'
import { getActivityList, getActivitySignups } from '../../api/news'
import { getMyClass } from '../../api/schedule'
import { addPost, uploadForumImage } from '../../api/forum'

const API_BASE = 'http://localhost:8080'

const loading    = ref(false)
const activities = ref<any[]>([])
const myClass    = ref<any>(null)

const expandedId      = ref<string | null>(null)
const signupsLoading  = ref(false)
const currentSignups  = ref<any[]>([])

const myClassSignups = computed(() =>
  myClass.value
    ? currentSignups.value.filter(s => s.className === myClass.value.className)
    : []
)
const otherSignups = computed(() =>
  myClass.value
    ? currentSignups.value.filter(s => s.className !== myClass.value.className)
    : currentSignups.value
)

const summary = reactive({
  show: false,
  title: '',
  content: '',
  images: [] as string[],
  uploading: false,
  submitting: false,
})

function imgUrl(path: string) {
  if (!path) return ''
  return path.startsWith('http') ? path : API_BASE + path
}

function openSummary(act: any) {
  summary.title    = `【活动总结】${act.title}`
  summary.content  = ''
  summary.images   = []
  summary.show     = true
}

async function uploadSummaryImage(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  summary.uploading = true
  try {
    const res: any = await uploadForumImage(file)
    summary.images.push(res.data)
  } finally {
    summary.uploading = false
    ;(e.target as HTMLInputElement).value = ''
  }
}

async function submitSummary() {
  if (!summary.content.trim()) { alert('请填写总结内容'); return }
  summary.submitting = true
  try {
    await addPost({ title: summary.title, content: summary.content, images: summary.images })
    summary.show = false
    alert('活动总结已发布到论坛！')
  } finally { summary.submitting = false }
}

async function toggleSignups(act: any) {
  const id = String(act.id)
  if (expandedId.value === id) { expandedId.value = null; return }
  expandedId.value = id
  signupsLoading.value = true
  currentSignups.value = []
  try {
    const res: any = await getActivitySignups(act.id)
    currentSignups.value = res.data
  } finally { signupsLoading.value = false }
}

function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

onMounted(async () => {
  loading.value = true
  try {
    const [actRes, clsRes]: any[] = await Promise.all([getActivityList(), getMyClass().catch(() => null)])
    activities.value = actRes.data
    if (clsRes) myClass.value = clsRes.data
  } finally { loading.value = false }
})
</script>
