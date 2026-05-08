<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">活动发现</h3>
      <p class="text-sm text-[#A09E94] mt-1">报名参加幼儿园精彩活动</p>
    </div>

    <!-- Tab -->
    <div class="flex gap-1 bg-[#F5F2ED] rounded-xl p-1 w-fit">
      <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
        class="px-5 py-2 rounded-lg text-sm font-medium transition-all"
        :class="activeTab === tab.key ? 'bg-white text-[#5A5A40] shadow-sm font-bold' : 'text-[#A09E94] hover:text-[#7A7A6A]'">
        {{ tab.label }}
      </button>
    </div>

    <!-- ── Tab 活动列表 ── -->
    <div v-show="activeTab === 'open'">
      <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
      <div v-else-if="activities.length === 0"
        class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
        <Calendar class="w-12 h-12 mx-auto mb-3 opacity-20" />
        <p class="text-sm font-medium">暂无开放活动</p>
      </div>
      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="act in activities" :key="act.id"
          class="bg-white rounded-2xl border p-5 hover:shadow-sm transition-shadow"
          :class="act.status === 1 ? 'border-[#EFE9E1]' : 'border-[#EFE9E1] opacity-70'">
          <!-- 状态标签 -->
          <div class="flex items-start justify-between gap-3 mb-3">
            <h4 class="text-base font-bold text-[#333322] leading-tight">{{ act.title }}</h4>
            <span class="text-[11px] px-2.5 py-1 rounded-full font-bold shrink-0"
              :class="act.status === 1 ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'">
              {{ act.status === 1 ? '报名中' : '已结束' }}
            </span>
          </div>

          <div class="space-y-1.5 mb-4 text-xs text-[#7A7A6A]">
            <div v-if="act.location" class="flex items-center gap-2">
              <MapPin class="w-3.5 h-3.5 text-[#A09E94] shrink-0" />
              {{ act.location }}
            </div>
            <div v-if="act.activityTime" class="flex items-center gap-2">
              <Clock class="w-3.5 h-3.5 text-[#A09E94] shrink-0" />
              {{ formatTime(act.activityTime) }}
            </div>
            <div v-if="act.signupDeadline" class="flex items-center gap-2">
              <CalendarClock class="w-3.5 h-3.5 text-[#A09E94] shrink-0" />
              截止：{{ formatTime(act.signupDeadline) }}
            </div>
          </div>

          <p v-if="act.description" class="text-xs text-[#A09E94] mb-4 line-clamp-2">{{ act.description }}</p>

          <!-- 名额进度 -->
          <div v-if="act.maxCount" class="mb-4">
            <div class="flex justify-between text-[10px] text-[#A09E94] mb-1">
              <span>已报名 {{ act.signupCount }} / {{ act.maxCount }}</span>
              <span>{{ act.maxCount - act.signupCount > 0 ? `还剩 ${act.maxCount - act.signupCount} 个名额` : '已满' }}</span>
            </div>
            <div class="h-1.5 bg-[#F5F2ED] rounded-full overflow-hidden">
              <div class="h-full bg-[#5A5A40] rounded-full transition-all"
                :style="{ width: Math.min(100, (act.signupCount / act.maxCount) * 100) + '%' }"></div>
            </div>
          </div>
          <div v-else class="mb-4 text-[10px] text-[#A09E94]">已报名 {{ act.signupCount }} 人 · 不限名额</div>

          <!-- 操作 -->
          <div v-if="signedUpMap.has(act.id)">
            <button @click="handleCancel(act)"
              class="w-full h-9 border border-red-200 text-red-400 rounded-xl text-xs font-bold hover:bg-red-50 transition-colors">
              取消报名
            </button>
          </div>
          <div v-else>
            <button v-if="act.status === 1 && (act.maxCount == null || act.signupCount < act.maxCount)"
              @click="openSignupModal(act)"
              class="w-full h-9 bg-[#5A5A40] text-white rounded-xl text-xs font-bold hover:bg-[#4A4A35] transition-colors">
              立即报名
            </button>
            <button v-else disabled
              class="w-full h-9 bg-[#F5F2ED] text-[#A09E94] rounded-xl text-xs font-bold cursor-not-allowed">
              {{ act.status !== 1 ? '已结束' : '名额已满' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ── Tab 我的报名 ── -->
    <div v-show="activeTab === 'mine'">
      <div v-if="mySignupsLoading" class="text-center py-16 text-[#A09E94]">加载中...</div>
      <div v-else-if="mySignups.length === 0"
        class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
        <CalendarCheck class="w-12 h-12 mx-auto mb-3 opacity-20" />
        <p class="text-sm font-medium">暂无报名记录</p>
      </div>
      <div v-else class="space-y-3">
        <div v-for="s in mySignups" :key="s.signupId"
          class="bg-white rounded-2xl border border-[#EFE9E1] p-4 flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <p class="text-sm font-bold text-[#333322]">{{ s.activityTitle }}</p>
            <div class="flex items-center gap-3 mt-1 text-xs text-[#A09E94] flex-wrap">
              <span v-if="s.activityLocation">{{ s.activityLocation }}</span>
              <span v-if="s.activityTime">{{ formatTime(s.activityTime) }}</span>
              <span>参与人：{{ s.studentName }}</span>
            </div>
            <p v-if="s.remark" class="text-xs text-[#7A7A6A] mt-1">备注：{{ s.remark }}</p>
          </div>
          <span class="text-[11px] px-2.5 py-1 rounded-full font-bold shrink-0"
            :class="s.activityStatus === 1 ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'">
            {{ s.activityStatus === 1 ? '进行中' : '已结束' }}
          </span>
        </div>
      </div>
    </div>

    <!-- 报名弹窗 -->
    <div v-if="signupModal.show" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-3xl w-full max-w-sm shadow-2xl">
        <div class="px-7 py-5 bg-[#F5F2ED] border-b border-[#EFE9E1] rounded-t-3xl">
          <h4 class="text-base font-bold text-[#5A5A40]">报名：{{ signupModal.activity?.title }}</h4>
        </div>
        <div class="p-7 space-y-4">
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">参与幼儿</label>
            <select v-model="signupModal.studentId"
              class="w-full h-10 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none focus:border-[#5A5A40]">
              <option value="">请选择</option>
              <option v-for="c in children" :key="c.studentId" :value="c.studentId">{{ c.studentName }}</option>
            </select>
          </div>
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">备注（选填）</label>
            <input v-model="signupModal.remark" placeholder="如饮食禁忌等"
              class="w-full h-10 px-3 rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
        </div>
        <div class="px-7 pb-7 flex gap-3">
          <button @click="signupModal.show = false"
            class="flex-1 h-9 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED]">取消</button>
          <button @click="confirmSignup" :disabled="signupModal.submitting"
            class="flex-1 h-9 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] disabled:opacity-50">
            {{ signupModal.submitting ? '提交中...' : '确认报名' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Calendar, MapPin, Clock, CalendarClock, CalendarCheck } from 'lucide-vue-next'
import { getActivityList, signupActivity, cancelSignup, getMySignups } from '../../api/news'
import request from '../../utils/request'

const tabs = [
  { key: 'open',  label: '活动列表' },
  { key: 'mine', label: '我的报名' },
]
const activeTab = ref('open')

const loading        = ref(false)
const activities     = ref<any[]>([])
const mySignupsLoading = ref(false)
const mySignups      = ref<any[]>([])
const children       = ref<any[]>([])

// 已报名映射：activityId → signupId
const signedUpMap = computed(() => {
  const m = new Map<string, string>()
  for (const s of mySignups.value) {
    m.set(String(s.activityId), String(s.signupId))
  }
  return m
})

const signupModal = reactive({
  show: false,
  activity: null as any,
  studentId: '',
  remark: '',
  submitting: false,
})

async function fetchActivities() {
  loading.value = true
  try {
    const res: any = await getActivityList()
    activities.value = res.data
  } finally { loading.value = false }
}

async function fetchMySignups() {
  mySignupsLoading.value = true
  try {
    const res: any = await getMySignups()
    mySignups.value = res.data
  } finally { mySignupsLoading.value = false }
}

async function fetchChildren() {
  const res: any = await request.get('/api/parent/my-children')
  children.value = res.data
}

function openSignupModal(act: any) {
  signupModal.activity  = act
  signupModal.studentId = children.value.length === 1 ? String(children.value[0].studentId) : ''
  signupModal.remark    = ''
  signupModal.show      = true
}

async function confirmSignup() {
  if (!signupModal.studentId) { alert('请选择参与幼儿'); return }
  signupModal.submitting = true
  try {
    await signupActivity(signupModal.activity.id, signupModal.studentId, signupModal.remark)
    signupModal.show = false
    await Promise.all([fetchActivities(), fetchMySignups()])
  } finally { signupModal.submitting = false }
}

async function handleCancel(act: any) {
  const signupId = signedUpMap.value.get(String(act.id))
  if (!signupId) return
  if (!confirm(`确认取消「${act.title}」的报名？`)) return
  await cancelSignup(signupId)
  await Promise.all([fetchActivities(), fetchMySignups()])
}

function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

onMounted(async () => {
  await Promise.all([fetchActivities(), fetchMySignups(), fetchChildren()])
})
</script>
