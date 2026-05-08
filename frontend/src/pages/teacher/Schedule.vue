<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">考勤与课表</h3>
      <p class="text-sm text-[#A09E94] mt-1">管理每日幼儿考勤，查看班级课程安排</p>
    </div>

    <!-- Tab -->
    <div class="flex gap-1 bg-[#F5F2ED] rounded-xl p-1 w-fit">
      <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
        class="px-5 py-2 rounded-lg text-sm font-medium transition-all"
        :class="activeTab === tab.key ? 'bg-white text-[#5A5A40] shadow-sm font-bold' : 'text-[#A09E94] hover:text-[#7A7A6A]'">
        {{ tab.label }}
      </button>
    </div>

    <!-- ========== Tab 1：今日考勤 ========== -->
    <div v-show="activeTab === 'attendance'">
      <!-- 工具栏 -->
      <div class="bg-white rounded-2xl p-4 border border-[#EFE9E1] flex flex-wrap items-center gap-3 mb-4">
        <div class="flex items-center gap-2">
          <span class="text-xs font-bold text-[#7A7A6A]">日期</span>
          <input v-model="selectedDate" type="date" @change="fetchAttendance"
            class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-[#F5F2ED] text-sm outline-none focus:border-[#5A5A40]" />
        </div>
        <div class="flex items-center gap-2 ml-2">
          <span class="text-xs font-bold text-[#7A7A6A]">班级</span>
          <span class="text-sm font-bold text-[#5A5A40]">{{ myClass?.className ?? '加载中...' }}</span>
        </div>
        <div class="flex-1"></div>
        <!-- 统计 -->
        <div class="flex items-center gap-4 text-xs">
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-green-400 inline-block"></span>
            出勤 <strong class="text-[#5A5A40]">{{ presentCount }}</strong>
          </span>
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-red-400 inline-block"></span>
            缺勤 <strong class="text-red-500">{{ absentCount }}</strong>
          </span>
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-gray-300 inline-block"></span>
            未记录 <strong class="text-[#A09E94]">{{ unrecordedCount }}</strong>
          </span>
        </div>
        <button @click="handleBatchPresent"
          class="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-xl text-xs font-bold hover:bg-green-700 transition-colors">
          <CheckCheck class="w-4 h-4" /> 全体出勤
        </button>
      </div>

      <!-- 学生列表 -->
      <div v-if="attLoading" class="text-center py-16 text-[#A09E94]">加载中...</div>
      <div v-else-if="attendanceList.length === 0 && myClass" class="text-center py-16 text-[#A09E94]">
        <Users class="w-10 h-10 mx-auto mb-2 opacity-20" />
        <p class="text-sm">班级暂无幼儿</p>
      </div>
      <div v-else-if="!myClass" class="text-center py-16 text-[#A09E94]">
        <p class="text-sm">当前账号尚未分配班级，请联系管理员</p>
      </div>
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
        <div v-for="stu in attendanceList" :key="stu.studentId"
          @click="openStudentPanel(stu)"
          class="bg-white rounded-2xl border p-4 cursor-pointer hover:shadow-md transition-all"
          :class="getCardClass(stu)">
          <!-- 头部：姓名 + 状态 -->
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2.5">
              <div class="w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold shrink-0"
                :class="stu.gender === 1 ? 'bg-blue-50 text-blue-600' : 'bg-pink-50 text-pink-500'">
                {{ stu.studentName.charAt(0) }}
              </div>
              <div>
                <p class="text-sm font-bold text-[#333322]">{{ stu.studentName }}</p>
                <p class="text-[10px] text-[#A09E94]">{{ stu.gender === 1 ? '男' : '女' }}</p>
              </div>
            </div>
            <span class="text-[11px] px-2.5 py-1 rounded-full font-bold"
              :class="getStatusClass(stu)">{{ getStatusLabel(stu) }}</span>
          </div>

          <!-- 缺勤信息 -->
          <div v-if="stu.status === 0" class="space-y-1 text-xs text-[#7A7A6A]">
            <div v-if="stu.absenceReason" class="flex items-start gap-1">
              <span class="text-[#A09E94] shrink-0">原因：</span>
              <span class="font-medium text-[#333322]">{{ stu.absenceReason }}</span>
            </div>
            <div v-if="stu.contactMethod" class="flex items-center gap-1">
              <span class="text-[#A09E94] shrink-0">联系：</span>
              <span class="px-2 py-0.5 rounded-full text-[10px] font-bold"
                :class="stu.contactMethod === '电话' ? 'bg-blue-50 text-blue-600' : stu.contactMethod === '私信' ? 'bg-purple-50 text-purple-600' : 'bg-gray-100 text-gray-500'">
                {{ stu.contactMethod }}
              </span>
            </div>
          </div>

          <p v-if="stu.status === null" class="text-[10px] text-orange-400 font-medium mt-2">点击记录考勤</p>
          <p v-else class="text-[10px] text-[#A09E94] mt-2">点击查看 / 修改</p>
        </div>
      </div>
    </div>

    <!-- ========== Tab 2：班级课表 ========== -->
    <div v-show="activeTab === 'schedule'">
      <div v-if="!myClass" class="text-center py-16 text-[#A09E94]">
        <p class="text-sm">当前账号尚未分配班级，请联系管理员</p>
      </div>
      <div v-else class="bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">
        <div class="px-6 py-4 border-b border-[#EFE9E1]">
          <h4 class="text-sm font-bold text-[#5A5A40]">{{ myClass.className }} · 本周课表</h4>
          <p class="text-xs text-[#A09E94] mt-0.5">课表由管理员维护，如有问题请联系园长</p>
        </div>
        <div v-if="scheduleLoading" class="text-center py-12 text-[#A09E94]">加载中...</div>
        <div v-else-if="scheduleData.length === 0" class="text-center py-12 text-[#A09E94]">
          <p class="text-sm">暂未安排课表</p>
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[600px]">
            <thead>
              <tr class="bg-[#F5F2ED]">
                <th class="w-20 px-4 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">节次</th>
                <th v-for="day in days" :key="day.value"
                  class="px-4 py-3 text-center text-[11px] font-bold uppercase tracking-widest"
                  :class="day.value === todayWeekday ? 'text-[#5A5A40]' : 'text-[#A09E94]'">
                  {{ day.label }}
                  <span v-if="day.value === todayWeekday" class="ml-1 text-[10px] bg-[#5A5A40] text-white px-1.5 py-0.5 rounded-full">今天</span>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="period in schedulePeriods" :key="period" class="border-t border-[#EFE9E1]">
                <td class="px-4 py-3 text-xs font-bold text-[#A09E94] bg-[#FDFCF8]">第 {{ period }} 节</td>
                <td v-for="day in days" :key="day.value" class="px-3 py-3 text-center"
                  :class="day.value === todayWeekday ? 'bg-[#F5F2ED]/50' : ''">
                  <template v-if="getScheduleCell(day.value, period)">
                    <p class="text-xs font-bold text-[#333322]">{{ getScheduleCell(day.value, period)!.subject }}</p>
                    <p v-if="getScheduleCell(day.value, period)!.teacherName" class="text-[10px] text-[#A09E94] mt-0.5">
                      {{ getScheduleCell(day.value, period)!.teacherName }}
                    </p>
                    <p v-if="getScheduleCell(day.value, period)!.startTime" class="text-[10px] text-[#A09E94]">
                      {{ getScheduleCell(day.value, period)!.startTime }}
                    </p>
                  </template>
                  <span v-else class="text-[#D0CBC3] text-xs">—</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- ========== 学生考勤面板 ========== -->
    <div v-if="studentPanel" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden">
        <!-- 头部 -->
        <div class="bg-[#5A5A40] px-8 py-6 flex items-center justify-between">
          <div>
            <h4 class="text-xl font-bold text-white">{{ currentStu?.studentName }}</h4>
            <p class="text-[#A09E94] text-xs mt-1">
              {{ currentStu?.gender === 1 ? '男' : '女' }} ·
              {{ currentStu?.birthday ?? '未填生日' }}
              <template v-if="currentStu?.remark">
                · <span class="text-orange-300">{{ currentStu.remark }}</span>
              </template>
            </p>
          </div>
          <button @click="studentPanel = false"><X class="w-5 h-5 text-white/70 hover:text-white" /></button>
        </div>

        <div class="p-6 max-h-[75vh] overflow-y-auto space-y-5">
          <!-- 家长信息 -->
          <div>
            <p class="text-[11px] font-bold uppercase tracking-widest text-[#A09E94] mb-3">家长联系信息</p>
            <div v-if="parentLoading" class="text-center py-4 text-[#A09E94] text-sm">加载中...</div>
            <div v-else-if="!studentDetail?.parents?.length" class="text-sm text-[#A09E94] py-2">暂无绑定家长</div>
            <div v-else class="space-y-2">
              <div v-for="p in studentDetail.parents.filter((x: any) => x.status === 1)" :key="p.bindId"
                class="flex items-center justify-between bg-[#F5F2ED] rounded-xl px-4 py-3">
                <div>
                  <p class="text-sm font-bold text-[#333322]">{{ p.realName || p.username }}</p>
                  <p class="text-xs text-[#A09E94]">{{ p.relation }} · {{ p.username }}</p>
                </div>
                <a v-if="p.phone" :href="`tel:${p.phone}`"
                  class="flex items-center gap-1.5 px-3 py-1.5 bg-blue-500 text-white rounded-xl text-xs font-bold hover:bg-blue-600 transition-colors">
                  <Phone class="w-3.5 h-3.5" /> {{ p.phone }}
                </a>
                <span v-else class="text-xs text-[#A09E94]">无手机号</span>
              </div>
            </div>
          </div>

          <!-- 考勤表单 -->
          <div>
            <p class="text-[11px] font-bold uppercase tracking-widest text-[#A09E94] mb-3">
              {{ selectedDate }} 考勤记录
            </p>
            <div class="space-y-3">
              <div>
                <label class="label">出勤状态 *</label>
                <div class="flex gap-3">
                  <button @click="attForm.status = 1"
                    class="flex-1 h-11 rounded-xl border-2 text-sm font-bold transition-all"
                    :class="attForm.status === 1 ? 'bg-green-500 text-white border-green-500' : 'border-[#EFE9E1] text-[#7A7A6A] hover:bg-green-50'">
                    出勤
                  </button>
                  <button @click="attForm.status = 0"
                    class="flex-1 h-11 rounded-xl border-2 text-sm font-bold transition-all"
                    :class="attForm.status === 0 ? 'bg-red-500 text-white border-red-500' : 'border-[#EFE9E1] text-[#7A7A6A] hover:bg-red-50'">
                    缺勤
                  </button>
                </div>
              </div>

              <template v-if="attForm.status === 0">
                <div>
                  <label class="label">缺勤原因</label>
                  <input v-model="attForm.absenceReason" placeholder="如：发烧请假、家事外出..." class="input-field" />
                </div>
                <div>
                  <label class="label">联系方式 <span class="font-normal text-[#A09E94]">（您用什么方式联系的家长）</span></label>
                  <div class="flex gap-2">
                    <button v-for="m in contactMethods" :key="m" @click="attForm.contactMethod = m"
                      class="flex-1 h-10 rounded-xl border-2 text-xs font-bold transition-all"
                      :class="attForm.contactMethod === m ? 'bg-[#5A5A40] text-white border-[#5A5A40]' : 'border-[#EFE9E1] text-[#7A7A6A] hover:bg-[#F5F2ED]'">
                      {{ m }}
                    </button>
                  </div>
                </div>
                <div>
                  <label class="label">联系说明（备注）</label>
                  <input v-model="attForm.contactNote" placeholder="如：已告知家长，下午接诊..." class="input-field" />
                </div>
              </template>
            </div>
          </div>
        </div>

        <div class="px-6 pb-6 flex gap-3">
          <button @click="studentPanel = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitAttendance" :disabled="attSubmitting"
            class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ attSubmitting ? '保存中...' : '保存考勤' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { X, Users, CheckCheck, Phone } from 'lucide-vue-next'
import { getMyClass, getSchedule, getAttendance, recordAttendance, batchPresent } from '../../api/schedule'
import { getStudentDetail } from '../../api/class'

const tabs = [
  { key: 'attendance', label: '今日考勤' },
  { key: 'schedule',   label: '班级课表' },
]
const activeTab = ref('attendance')

const days = [
  { label: '周一', value: 1 }, { label: '周二', value: 2 }, { label: '周三', value: 3 },
  { label: '周四', value: 4 }, { label: '周五', value: 5 },
]

const todayWeekday = computed(() => {
  const d = new Date().getDay()
  return d === 0 ? 5 : d === 6 ? 5 : d
})

function todayStr() {
  return new Date().toISOString().slice(0, 10)
}

// ── 教师班级 ──────────────────────────────────────
const myClass = ref<any>(null)

async function loadMyClass() {
  try {
    const res: any = await getMyClass()
    myClass.value = res.data
  } catch {
    myClass.value = null
  }
}

// ── 考勤 ──────────────────────────────────────────
const selectedDate    = ref(todayStr())
const attLoading      = ref(false)
const attendanceList  = ref<any[]>([])

const presentCount    = computed(() => attendanceList.value.filter(s => s.status === 1).length)
const absentCount     = computed(() => attendanceList.value.filter(s => s.status === 0).length)
const unrecordedCount = computed(() => attendanceList.value.filter(s => s.status === null).length)

async function fetchAttendance() {
  if (!myClass.value) return
  attLoading.value = true
  try {
    const res: any = await getAttendance(myClass.value.id, selectedDate.value)
    attendanceList.value = res.data
  } finally { attLoading.value = false }
}

async function handleBatchPresent() {
  if (!myClass.value || attendanceList.value.length === 0) return
  if (!confirm(`确认将 ${selectedDate.value} 全部 ${attendanceList.value.length} 名幼儿标记为出勤？`)) return
  const ids = attendanceList.value.map((s: any) => s.studentId)
  await batchPresent(myClass.value.id, selectedDate.value, ids)
  fetchAttendance()
}

// ── 课表 ──────────────────────────────────────────
const scheduleLoading = ref(false)
const scheduleData    = ref<any[]>([])

const schedulePeriods = computed(() => {
  if (!scheduleData.value.length) return []
  const max = Math.max(...scheduleData.value.map((s: any) => s.periodNo))
  return Array.from({ length: max }, (_, i) => i + 1)
})

function getScheduleCell(dayOfWeek: number, periodNo: number) {
  return scheduleData.value.find((s: any) => s.dayOfWeek === dayOfWeek && s.periodNo === periodNo) || null
}

async function fetchSchedule() {
  if (!myClass.value) return
  scheduleLoading.value = true
  try {
    const res: any = await getSchedule(myClass.value.id)
    scheduleData.value = res.data
  } finally { scheduleLoading.value = false }
}

onMounted(async () => {
  await loadMyClass()
  if (myClass.value) {
    fetchAttendance()
    fetchSchedule()
  }
})

// ── 考勤面板 ──────────────────────────────────────
const studentPanel   = ref(false)
const parentLoading  = ref(false)
const currentStu     = ref<any>(null)
const studentDetail  = ref<any>(null)
const attSubmitting  = ref(false)

const contactMethods = ['电话', '私信', '未联系']

const attForm = reactive({
  studentId: 0,
  classId: 0,
  date: '',
  status: 1 as number,
  absenceReason: '',
  contactMethod: '',
  contactNote: '',
})

async function openStudentPanel(stu: any) {
  currentStu.value   = stu
  studentPanel.value = true
  parentLoading.value = true
  Object.assign(attForm, {
    studentId: stu.studentId,
    classId: myClass.value?.id ?? 0,
    date: selectedDate.value,
    status: stu.status ?? 1,
    absenceReason: stu.absenceReason ?? '',
    contactMethod: stu.contactMethod ?? '',
    contactNote: stu.contactNote ?? '',
  })
  try {
    const res: any = await getStudentDetail(stu.studentId)
    studentDetail.value = res.data
  } finally { parentLoading.value = false }
}

async function submitAttendance() {
  attSubmitting.value = true
  try {
    await recordAttendance({
      studentId: attForm.studentId,
      classId: attForm.classId,
      date: attForm.date,
      status: attForm.status,
      absenceReason: attForm.status === 0 ? attForm.absenceReason || undefined : undefined,
      contactMethod: attForm.status === 0 ? attForm.contactMethod || undefined : undefined,
      contactNote:   attForm.status === 0 ? attForm.contactNote   || undefined : undefined,
    } as any)
    studentPanel.value = false
    fetchAttendance()
  } finally { attSubmitting.value = false }
}

// ── 样式 ──────────────────────────────────────────
function getCardClass(stu: any) {
  if (stu.status === 0)    return 'border-red-200 bg-red-50/50'
  if (stu.status === null) return 'border-orange-200 bg-orange-50/30'
  return 'border-[#EFE9E1]'
}
function getStatusClass(stu: any) {
  if (stu.status === 0)    return 'bg-red-100 text-red-500'
  if (stu.status === null) return 'bg-orange-50 text-orange-400'
  return 'bg-green-50 text-green-600'
}
function getStatusLabel(stu: any) {
  if (stu.status === 0)    return '缺勤'
  if (stu.status === null) return '未记录'
  return '出勤'
}
</script>

<style scoped>
@reference "tailwindcss";
.label { @apply block text-xs font-bold text-[#7A7A6A] mb-1.5; }
.input-field { @apply w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40] transition-colors; }
</style>
