<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">活动跟进</h3>
      <p class="text-sm text-[#A09E94] mt-1">查看活动及班级幼儿的报名情况</p>
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
          <button @click="toggleSignups(act)"
            class="shrink-0 h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] flex items-center gap-1 transition-colors">
            <ChevronDown class="w-3.5 h-3.5 transition-transform"
              :class="expandedId === act.id ? 'rotate-180' : ''" />
            报名名单
          </button>
        </div>

        <!-- 展开：报名名单 -->
        <div v-if="expandedId === act.id" class="border-t border-[#F5F2ED] px-5 py-4">
          <div v-if="signupsLoading" class="text-center py-4 text-xs text-[#A09E94]">加载中...</div>
          <div v-else-if="currentSignups.length === 0" class="text-center py-4 text-xs text-[#A09E94]">暂无报名</div>
          <div v-else>
            <!-- 本班 vs 其他 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Activity, MapPin, Clock, Users, ChevronDown } from 'lucide-vue-next'
import { getActivityList, getActivitySignups } from '../../api/news'
import { getMyClass } from '../../api/schedule'

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
