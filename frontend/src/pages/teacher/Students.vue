<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h3 class="text-2xl font-bold text-[#5A5A40]">班级花名册</h3>
        <p class="text-sm text-[#A09E94] mt-1">
          {{ myClass ? myClass.className + ' · ' + myClass.grade : '加载中...' }}
        </p>
      </div>
      <!-- 待审核角标 -->
      <div v-if="pendingCount > 0" @click="activeTab = 'pending'"
        class="flex items-center gap-2 px-4 py-2 bg-orange-50 border border-orange-200 rounded-2xl cursor-pointer hover:bg-orange-100 transition-colors">
        <span class="w-5 h-5 bg-orange-500 text-white rounded-full text-[11px] font-bold flex items-center justify-center">
          {{ pendingCount > 9 ? '9+' : pendingCount }}
        </span>
        <span class="text-sm font-medium text-orange-600">待审核绑定</span>
      </div>
    </div>

    <!-- Tab -->
    <div class="flex gap-1 bg-[#F5F2ED] rounded-xl p-1 w-fit">
      <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
        class="px-5 py-2 rounded-lg text-sm font-medium transition-all relative"
        :class="activeTab === tab.key ? 'bg-white text-[#5A5A40] shadow-sm font-bold' : 'text-[#A09E94] hover:text-[#7A7A6A]'">
        {{ tab.label }}
        <span v-if="tab.key === 'pending' && pendingCount > 0"
          class="absolute -top-1 -right-1 w-4 h-4 bg-orange-500 text-white rounded-full text-[9px] font-bold flex items-center justify-center">
          {{ pendingCount }}
        </span>
      </button>
    </div>

    <!-- ========== Tab 1：花名册 ========== -->
    <div v-show="activeTab === 'roster'">
      <!-- 搜索栏 -->
      <div class="flex flex-wrap items-center gap-3 mb-4">
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
          <!-- 头像 -->
          <div class="w-12 h-12 rounded-full mx-auto mb-3 flex items-center justify-center text-lg font-bold"
            :class="stu.gender === 1 ? 'bg-blue-50 text-blue-500' : 'bg-pink-50 text-pink-500'">
            {{ stu.name.charAt(0) }}
          </div>
          <!-- 姓名 -->
          <p class="text-sm font-bold text-[#333322] text-center">{{ stu.name }}</p>
          <!-- 性别 + 年龄 -->
          <p class="text-[11px] text-[#A09E94] text-center mt-0.5">
            {{ stu.gender === 1 ? '男生' : '女生' }}
            <template v-if="stu.birthday"> · {{ calcAge(stu.birthday) }}</template>
          </p>
          <!-- 绑定家长数量 -->
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
    </div>

    <!-- ========== Tab 2：待审核绑定 ========== -->
    <div v-show="activeTab === 'pending'">
      <div v-if="pendingLoading" class="text-center py-16 text-[#A09E94]">加载中...</div>
      <div v-else-if="pendingList.length === 0"
        class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
        <CheckCircle class="w-12 h-12 mx-auto mb-3 opacity-20" />
        <p class="text-sm font-medium">暂无待审核申请</p>
        <p class="text-xs mt-1">所有家长绑定申请都已处理完毕</p>
      </div>
      <div v-else class="space-y-3">
        <div v-for="item in pendingList" :key="item.bindId"
          class="bg-white rounded-2xl border border-orange-100 p-5 hover:shadow-sm transition-shadow">
          <div class="flex items-start justify-between gap-4">
            <!-- 左侧信息 -->
            <div class="flex items-start gap-4 flex-1 min-w-0">
              <!-- 家长头像 -->
              <div class="w-11 h-11 rounded-full bg-orange-50 flex items-center justify-center text-orange-500 font-bold text-sm shrink-0">
                {{ (item.parentName || item.parentUsername || '?').charAt(0) }}
              </div>
              <div class="min-w-0">
                <div class="flex items-center gap-2 flex-wrap">
                  <p class="text-sm font-bold text-[#333322]">{{ item.parentName || item.parentUsername }}</p>
                  <span class="text-[11px] px-2 py-0.5 bg-orange-50 text-orange-500 rounded-full font-bold">待审核</span>
                </div>
                <p class="text-xs text-[#A09E94] mt-0.5">账号：{{ item.parentUsername }}</p>
                <div class="flex items-center gap-3 mt-2 flex-wrap text-xs text-[#7A7A6A]">
                  <span>申请绑定：<strong class="text-[#333322]">{{ item.studentName }}</strong></span>
                  <span>关系：<strong class="text-[#333322]">{{ item.relation || '未填写' }}</strong></span>
                  <span v-if="item.parentPhone">
                    <a :href="`tel:${item.parentPhone}`" class="text-blue-500 hover:underline font-medium">
                      {{ item.parentPhone }}
                    </a>
                  </span>
                </div>
                <p class="text-[10px] text-[#A09E94] mt-1">申请时间：{{ formatTime(item.applyTime) }}</p>
              </div>
            </div>
            <!-- 右侧操作 -->
            <div class="flex items-center gap-2 shrink-0">
              <button @click="handleAudit(item, 2)"
                class="h-9 px-4 border border-red-200 text-red-400 rounded-xl text-xs font-bold hover:bg-red-50 transition-colors">
                拒绝
              </button>
              <button @click="handleAudit(item, 1)"
                class="h-9 px-4 bg-green-600 text-white rounded-xl text-xs font-bold hover:bg-green-700 transition-colors">
                通过
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 幼儿详情面板 ========== -->
    <div v-if="detailPanel" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden">
        <!-- 头部 -->
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
            <div v-else-if="!currentDetail?.parents?.length" class="text-center py-6 text-[#A09E94]">
              <p class="text-sm">暂无绑定家长</p>
            </div>
            <div v-else class="space-y-2">
              <div v-for="p in currentDetail.parents" :key="p.bindId"
                class="rounded-2xl p-4 border"
                :class="p.status === 1 ? 'border-[#EFE9E1]' : 'border-orange-100 bg-orange-50/50'">
                <div class="flex items-start justify-between gap-3">
                  <div class="flex items-center gap-3">
                    <div class="w-9 h-9 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] font-bold text-sm shrink-0">
                      {{ (p.realName || p.username || '?').charAt(0) }}
                    </div>
                    <div>
                      <p class="text-sm font-bold text-[#333322]">{{ p.realName || p.username }}</p>
                      <p class="text-xs text-[#A09E94]">{{ p.relation || '关系未填' }} · {{ p.username }}</p>
                    </div>
                  </div>
                  <span class="text-[11px] px-2 py-1 rounded-full font-bold shrink-0"
                    :class="p.status === 0 ? 'bg-orange-50 text-orange-500' : p.status === 1 ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-400'">
                    {{ p.status === 0 ? '待审核' : p.status === 1 ? '已绑定' : '已拒绝' }}
                  </span>
                </div>
                <!-- 联系方式 -->
                <div v-if="p.status === 1 && p.phone" class="mt-3 flex items-center gap-2">
                  <a :href="`tel:${p.phone}`"
                    class="flex items-center gap-1.5 px-3 py-1.5 bg-blue-500 text-white rounded-xl text-xs font-bold hover:bg-blue-600 transition-colors">
                    <Phone class="w-3.5 h-3.5" /> {{ p.phone }}
                  </a>
                </div>
                <!-- 待审核操作 -->
                <div v-if="p.status === 0" class="mt-3 flex gap-2">
                  <button @click="handleDetailAudit(p.bindId, 1)"
                    class="flex-1 h-8 bg-green-600 text-white rounded-xl text-xs font-bold hover:bg-green-700 transition-colors">通过</button>
                  <button @click="handleDetailAudit(p.bindId, 2)"
                    class="flex-1 h-8 border border-red-200 text-red-400 rounded-xl text-xs font-bold hover:bg-red-50 transition-colors">拒绝</button>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { X, Users, Baby, CheckCircle, Phone } from 'lucide-vue-next'
import { getMyClass } from '../../api/schedule'
import { getStudentList, getStudentDetail } from '../../api/class'
import { getPendingBinds, auditBind } from '../../api/teacher'

const tabs = [
  { key: 'roster',  label: '花名册' },
  { key: 'pending', label: '待审核绑定' },
]
const activeTab = ref('roster')

// ── 教师班级 ──────────────────────────────────────
const myClass = ref<any>(null)

// ── 花名册 ────────────────────────────────────────
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
    // 给每个学生附上绑定家长数量（用 detail 已有的数据，但这里先忽略，改成后端返回的 parentCount）
    studentList.value  = res.data.records
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

// ── 待审核绑定 ────────────────────────────────────
const pendingLoading = ref(false)
const pendingList    = ref<any[]>([])
const pendingCount   = computed(() => pendingList.value.length)

async function fetchPendingBinds() {
  pendingLoading.value = true
  try {
    const res: any = await getPendingBinds()
    pendingList.value = res.data
  } finally { pendingLoading.value = false }
}

async function handleAudit(item: any, status: number) {
  const label = status === 1 ? '通过' : '拒绝'
  if (!confirm(`确认${label}「${item.parentName}」绑定「${item.studentName}」的申请？`)) return
  await auditBind(item.bindId, status, 0) // 0 为占位，实际后端用 session 记录
  fetchPendingBinds()
}

// ── 幼儿详情面板 ──────────────────────────────────
const detailPanel   = ref(false)
const detailLoading = ref(false)
const currentStu    = ref<any>(null)
const currentDetail = ref<any>(null)

async function openDetail(stu: any) {
  currentStu.value   = stu
  detailPanel.value  = true
  detailLoading.value = true
  try {
    const res: any = await getStudentDetail(stu.id)
    currentDetail.value = res.data
  } finally { detailLoading.value = false }
}

async function handleDetailAudit(bindId: number, status: number) {
  await auditBind(bindId, status, 0)
  // 刷新详情
  const res: any = await getStudentDetail(currentStu.value.id)
  currentDetail.value = res.data
  fetchPendingBinds()
}

// ── 初始化 ────────────────────────────────────────
onMounted(async () => {
  try {
    const r: any = await getMyClass()
    myClass.value = r.data
  } catch { myClass.value = null }
  fetchStudents()
  fetchPendingBinds()
})

// ── 工具函数 ──────────────────────────────────────
function calcAge(birthday: string | null) {
  if (!birthday) return ''
  const born  = new Date(birthday)
  const now   = new Date()
  const months = (now.getFullYear() - born.getFullYear()) * 12 + (now.getMonth() - born.getMonth())
  const years  = Math.floor(months / 12)
  const m      = months % 12
  return years > 0 ? `${years}岁${m > 0 ? m + '个月' : ''}` : `${m}个月`
}

function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}
</script>
