<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">班级花名册</h3>
      <p class="text-sm text-[#A09E94] mt-1">
        {{ myClass ? myClass.className + ' · ' + myClass.grade : '加载中...' }}
      </p>
    </div>

    <!-- 搜索栏 -->
    <div class="flex flex-wrap items-center gap-3">
      <input v-model="keyword" @keyup.enter="fetchStudents" placeholder="搜索幼儿姓名"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none w-44 focus:border-[#5A5A40]" />
      <select v-model="genderFilter" @change="fetchStudents"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm text-[#5A5A40] outline-none">
        <option value="">全部性别</option>
        <option value="1">男</option>
        <option value="2">女</option>
      </select>
      <button @click="fetchStudents" class="h-9 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35] transition-colors">搜索</button>
      <button @click="resetSearch" class="h-9 px-4 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">重置</button>
      <span class="ml-auto text-xs text-[#A09E94]">共 {{ totalStudents }} 名幼儿</span>
    </div>

    <div v-if="stuLoading" class="text-center py-16 text-[#A09E94]">加载中...</div>
    <div v-else-if="studentList.length === 0" class="text-center py-16 text-[#A09E94]">
      <Baby class="w-10 h-10 mx-auto mb-2 opacity-20" />
      <p class="text-sm">暂无幼儿数据</p>
    </div>
    <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3">
      <div v-for="stu in studentList" :key="stu.id"
        @click="openDetail(stu)"
        class="bg-white rounded-2xl border border-[#EFE9E1] p-4 cursor-pointer hover:shadow-md hover:border-[#5A5A40]/20 transition-all">
        <div class="w-12 h-12 rounded-full mx-auto mb-3 flex items-center justify-center text-lg font-bold"
          :class="stu.gender === 1 ? 'bg-blue-50 text-blue-500' : 'bg-pink-50 text-pink-500'">
          {{ stu.name.charAt(0) }}
        </div>
        <p class="text-sm font-bold text-[#333322] text-center">{{ stu.name }}</p>
        <p class="text-[11px] text-[#A09E94] text-center mt-0.5">
          {{ stu.gender === 1 ? '男生' : '女生' }}
          <template v-if="stu.birthday"> · {{ calcAge(stu.birthday) }}</template>
        </p>
        <div class="mt-3 pt-3 border-t border-[#F5F2ED] flex justify-center">
          <span class="text-[10px] text-[#A09E94] flex items-center gap-1">
            <Users class="w-3 h-3" />
            {{ stu.parentCount ?? 0 }} 位家长
          </span>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalStudents > pageSize" class="flex items-center justify-center gap-3 mt-6">
      <button @click="prevPage" :disabled="pageNum === 1"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">上一页</button>
      <span class="text-xs text-[#5A5A40] font-medium">{{ pageNum }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="pageNum >= totalPages"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">下一页</button>
    </div>

    <!-- 幼儿详情面板 -->
    <div v-if="detailPanel" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden">
        <div class="flex items-center justify-between px-8 py-6"
          :style="currentStu?.gender === 1 ? 'background: linear-gradient(135deg,#3B82F6,#6366F1)' : 'background: linear-gradient(135deg,#EC4899,#F97316)'">
          <div>
            <h4 class="text-2xl font-bold text-white">{{ currentDetail?.name }}</h4>
            <p class="text-white/70 text-xs mt-1">
              {{ currentDetail?.gender === 1 ? '男生' : '女生' }} ·
              {{ currentDetail?.birthday ?? '未填生日' }}
              <template v-if="calcAge(currentDetail?.birthday)"> · {{ calcAge(currentDetail?.birthday) }}</template>
            </p>
          </div>
          <button @click="detailPanel = false"><X class="w-5 h-5 text-white/70 hover:text-white" /></button>
        </div>

        <div class="p-7 max-h-[70vh] overflow-y-auto space-y-5">
          <!-- 基本信息 -->
          <div class="grid grid-cols-2 gap-3">
            <div class="bg-[#F5F2ED] rounded-xl p-3">
              <p class="text-[10px] font-bold uppercase text-[#A09E94] mb-1">班级</p>
              <p class="text-sm font-bold text-[#5A5A40]">{{ currentDetail?.className }}</p>
            </div>
            <div class="bg-[#F5F2ED] rounded-xl p-3">
              <p class="text-[10px] font-bold uppercase text-[#A09E94] mb-1">状态</p>
              <p class="text-sm font-bold" :class="currentDetail?.status === 1 ? 'text-green-600' : 'text-gray-400'">
                {{ currentDetail?.status === 1 ? '在园' : '离园' }}
              </p>
            </div>
            <div v-if="currentDetail?.remark" class="col-span-2 bg-orange-50 border border-orange-100 rounded-xl p-3">
              <p class="text-[10px] font-bold uppercase text-orange-400 mb-1">特别备注（过敏/特殊情况）</p>
              <p class="text-sm font-medium text-orange-700">{{ currentDetail.remark }}</p>
            </div>
          </div>

          <!-- 绑定家长 -->
          <div>
            <p class="text-[11px] font-bold uppercase tracking-widest text-[#A09E94] mb-3">绑定家长</p>
            <div v-if="detailLoading" class="text-center py-6 text-[#A09E94] text-sm">加载中...</div>
            <div v-else-if="!boundParents.length" class="text-center py-6 text-[#A09E94]">
              <p class="text-sm">暂无绑定家长</p>
            </div>
            <div v-else class="space-y-2">
              <div v-for="p in boundParents" :key="p.bindId"
                class="rounded-2xl p-4 border border-[#EFE9E1]">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] font-bold text-sm shrink-0">
                    {{ (p.realName || p.username || '?').charAt(0) }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-bold text-[#333322]">{{ p.realName || p.username }}</p>
                    <p class="text-xs text-[#A09E94]">{{ p.relation || '关系未填' }} · {{ p.username }}</p>
                  </div>
                  <span class="text-[11px] px-2 py-1 rounded-full font-bold bg-green-50 text-green-600 shrink-0">已绑定</span>
                </div>
                <div v-if="p.phone" class="mt-3">
                  <a :href="`tel:${p.phone}`"
                    class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-blue-500 text-white rounded-xl text-xs font-bold hover:bg-blue-600 transition-colors">
                    <Phone class="w-3.5 h-3.5" /> {{ p.phone }}
                  </a>
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
import { X, Users, Baby, Phone } from 'lucide-vue-next'
import { getMyClass } from '../../api/schedule'
import { getStudentList, getStudentDetail } from '../../api/class'

const myClass = ref<any>(null)

const stuLoading    = ref(false)
const studentList   = ref<any[]>([])
const totalStudents = ref(0)
const pageNum       = ref(1)
const pageSize      = ref(20)
const keyword       = ref('')
const genderFilter  = ref('')

const totalPages = computed(() => Math.max(1, Math.ceil(totalStudents.value / pageSize.value)))

async function fetchStudents() {
  if (!myClass.value) return
  stuLoading.value = true
  try {
    const res: any = await getStudentList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      classId: myClass.value.id,
      keyword: keyword.value || undefined,
      status: 1,
    })
    studentList.value   = res.data.records
    totalStudents.value = res.data.total
  } finally { stuLoading.value = false }
}

function resetSearch() {
  keyword.value = ''
  genderFilter.value = ''
  pageNum.value = 1
  fetchStudents()
}
function prevPage() { if (pageNum.value > 1) { pageNum.value--; fetchStudents() } }
function nextPage() { if (pageNum.value < totalPages.value) { pageNum.value++; fetchStudents() } }

const detailPanel   = ref(false)
const detailLoading = ref(false)
const currentStu    = ref<any>(null)
const currentDetail = ref<any>(null)

const boundParents = computed(() =>
  (currentDetail.value?.parents ?? []).filter((p: any) => p.status === 1)
)

async function openDetail(stu: any) {
  currentStu.value    = stu
  detailPanel.value   = true
  detailLoading.value = true
  try {
    const res: any = await getStudentDetail(stu.id)
    currentDetail.value = res.data
  } finally { detailLoading.value = false }
}

onMounted(async () => {
  try {
    const r: any = await getMyClass()
    myClass.value = r.data
  } catch { myClass.value = null }
  fetchStudents()
})

function calcAge(birthday: string | null) {
  if (!birthday) return ''
  const born   = new Date(birthday)
  const now    = new Date()
  const months = (now.getFullYear() - born.getFullYear()) * 12 + (now.getMonth() - born.getMonth())
  const years  = Math.floor(months / 12)
  const m      = months % 12
  return years > 0 ? `${years}岁${m > 0 ? m + '个月' : ''}` : `${m}个月`
}
</script>
