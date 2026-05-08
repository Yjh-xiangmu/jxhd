<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">课表管理</h3>
      <p class="text-sm text-[#A09E94] mt-1">为各班级制定每周课程表，教师可在工作台查看</p>
    </div>

    <!-- 选择班级 -->
    <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1] flex items-center gap-4">
      <span class="text-sm font-bold text-[#7A7A6A] shrink-0">选择班级</span>
      <select v-model="selectedClassId" @change="onClassChange"
        class="h-10 px-4 rounded-xl border border-[#EFE9E1] bg-[#F5F2ED] text-sm text-[#5A5A40] outline-none w-48">
        <option :value="null">请选择班级</option>
        <option v-for="cls in classList" :key="cls.id" :value="cls.id">{{ cls.className }}</option>
      </select>
      <span v-if="selectedClassId" class="text-xs text-[#A09E94]">点击课表格子可新增/编辑课程</span>
    </div>

    <!-- 课表网格 -->
    <div v-if="selectedClassId" class="bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full min-w-[680px]">
          <thead>
            <tr class="bg-[#F5F2ED]">
              <th class="w-24 px-4 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">节次</th>
              <th v-for="day in days" :key="day.value"
                class="px-4 py-3 text-center text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">
                {{ day.label }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="period in periods" :key="period" class="border-t border-[#EFE9E1]">
              <!-- 节次标签 -->
              <td class="px-4 py-2 text-xs font-bold text-[#A09E94] bg-[#FDFCF8]">
                第 {{ period }} 节
              </td>
              <!-- 各天格子 -->
              <td v-for="day in days" :key="day.value" class="px-2 py-2 text-center">
                <div v-if="getCell(day.value, period)"
                  class="group relative bg-[#F0EDE6] rounded-xl px-3 py-2.5 cursor-pointer hover:bg-[#E8E4DA] transition-colors"
                  @click="openEditCell(day.value, period)">
                  <p class="text-xs font-bold text-[#5A5A40] leading-tight">{{ getCell(day.value, period)!.subject }}</p>
                  <p v-if="getCell(day.value, period)!.teacherName" class="text-[10px] text-[#A09E94] mt-0.5">
                    {{ getCell(day.value, period)!.teacherName }}
                  </p>
                  <p v-if="getCell(day.value, period)!.startTime" class="text-[10px] text-[#A09E94]">
                    {{ getCell(day.value, period)!.startTime }}
                    <template v-if="getCell(day.value, period)!.endTime"> - {{ getCell(day.value, period)!.endTime }}</template>
                  </p>
                  <button @click.stop="handleDeleteCell(getCell(day.value, period)!.id)"
                    class="absolute top-1 right-1 w-5 h-5 rounded-full bg-red-400/80 text-white text-[10px] leading-5 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                    ×
                  </button>
                </div>
                <button v-else @click="openAddCell(day.value, period)"
                  class="w-full h-14 border-2 border-dashed border-[#EFE9E1] rounded-xl text-[#D0CBC3] hover:border-[#5A5A40]/30 hover:text-[#5A5A40]/50 transition-colors text-xl leading-none">
                  +
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 节次控制 -->
      <div class="px-6 py-4 border-t border-[#EFE9E1] flex items-center gap-4">
        <span class="text-xs text-[#A09E94]">当前显示 {{ periods.length }} 节</span>
        <button @click="addPeriod" :disabled="periods.length >= 8"
          class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#5A5A40] hover:bg-[#F5F2ED] disabled:opacity-40 transition-colors">
          + 增加节次
        </button>
        <button @click="removePeriod" :disabled="periods.length <= 1"
          class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-red-400 hover:bg-red-50 disabled:opacity-40 transition-colors">
          − 减少节次
        </button>
      </div>
    </div>

    <div v-else class="bg-white rounded-2xl border border-[#EFE9E1] py-20 flex flex-col items-center justify-center text-[#A09E94]">
      <CalendarDays class="w-12 h-12 mb-3 opacity-20" />
      <p class="text-sm">请先在上方选择班级</p>
    </div>

    <!-- ===== 新增/编辑课程弹框 ===== -->
    <div v-if="cellDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">
            {{ cellIsEdit ? '编辑课程' : '新增课程' }} — {{ dayLabel }}·第{{ editPeriod }}节
          </h4>
          <button @click="cellDialog = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="label">课程名称 *</label>
            <div class="flex flex-wrap gap-2 mb-2">
              <button v-for="s in subjectSuggestions" :key="s" @click="cellForm.subject = s"
                class="px-3 py-1 rounded-full text-xs border transition-colors"
                :class="cellForm.subject === s ? 'bg-[#5A5A40] text-white border-[#5A5A40]' : 'border-[#EFE9E1] text-[#7A7A6A] hover:bg-[#F5F2ED]'">
                {{ s }}
              </button>
            </div>
            <input v-model="cellForm.subject" placeholder="或手动输入课程名称" class="input-field" />
          </div>
          <div>
            <label class="label">任课教师（可选）</label>
            <select v-model="cellForm.teacherId" class="input-field">
              <option :value="null">不指定</option>
              <option v-for="t in teacherList" :key="t.id" :value="t.id">{{ t.realName || t.username }}</option>
            </select>
          </div>
          <div class="flex gap-3">
            <div class="flex-1">
              <label class="label">开始时间</label>
              <input v-model="cellForm.startTime" type="time" class="input-field" />
            </div>
            <div class="flex-1">
              <label class="label">结束时间</label>
              <input v-model="cellForm.endTime" type="time" class="input-field" />
            </div>
          </div>
        </div>
        <div class="flex gap-3 mt-8">
          <button @click="cellDialog = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitCell" :disabled="cellSubmitting" class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ cellSubmitting ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { X, CalendarDays } from 'lucide-vue-next'
import { getClassList, getTeachers } from '../../api/class'
import { getSchedule, saveScheduleEntry, deleteScheduleEntry } from '../../api/schedule'

const classList    = ref<any[]>([])
const teacherList  = ref<any[]>([])
const selectedClassId = ref<number | null>(null)
const scheduleData = ref<any[]>([])

// 节次显示数量（最多8节）
const periodCount = ref(5)
const periods = computed(() => Array.from({ length: periodCount.value }, (_, i) => i + 1))

const days = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
]

onMounted(async () => {
  const r1: any = await getClassList({})
  classList.value = r1.data
  const r2: any = await getTeachers()
  teacherList.value = r2.data
})

async function onClassChange() {
  if (!selectedClassId.value) return
  const res: any = await getSchedule(selectedClassId.value)
  scheduleData.value = res.data
  // 自动调整显示节次
  if (scheduleData.value.length > 0) {
    const maxPeriod = Math.max(...scheduleData.value.map((s: any) => s.periodNo))
    periodCount.value = Math.max(periodCount.value, maxPeriod)
  }
}

function getCell(dayOfWeek: number, periodNo: number) {
  return scheduleData.value.find((s: any) => s.dayOfWeek === dayOfWeek && s.periodNo === periodNo) || null
}

function addPeriod() { if (periodCount.value < 8) periodCount.value++ }
function removePeriod() {
  if (periodCount.value <= 1) return
  // 检查最后一节是否有课
  const hasData = scheduleData.value.some((s: any) => s.periodNo === periodCount.value)
  if (hasData && !confirm(`第${periodCount.value}节有课程，确认删除该节次吗？`)) return
  if (hasData) {
    scheduleData.value = scheduleData.value.filter((s: any) => s.periodNo !== periodCount.value)
  }
  periodCount.value--
}

async function handleDeleteCell(id: number) {
  if (!confirm('确认删除该节课程吗？')) return
  await deleteScheduleEntry(id)
  scheduleData.value = scheduleData.value.filter((s: any) => s.id !== id)
}

// ── 弹框 ──────────────────────────────────────────
const cellDialog    = ref(false)
const cellIsEdit    = ref(false)
const cellSubmitting = ref(false)
const editDay       = ref(1)
const editPeriod    = ref(1)

const cellForm = reactive({
  id: undefined as number | undefined,
  classId: 0,
  dayOfWeek: 1,
  periodNo: 1,
  subject: '',
  teacherId: null as number | null,
  startTime: '',
  endTime: '',
})

const dayLabel = computed(() => days.find(d => d.value === editDay.value)?.label ?? '')

const subjectSuggestions = ['语言', '数学', '艺术手工', '音乐舞蹈', '体能运动', '科学探索', '品德生活', '阅读']

function openAddCell(day: number, period: number) {
  cellIsEdit.value = false
  editDay.value    = day
  editPeriod.value = period
  Object.assign(cellForm, {
    id: undefined, classId: selectedClassId.value, dayOfWeek: day, periodNo: period,
    subject: '', teacherId: null, startTime: '', endTime: '',
  })
  cellDialog.value = true
}

function openEditCell(day: number, period: number) {
  const cell = getCell(day, period)
  if (!cell) return
  cellIsEdit.value = true
  editDay.value    = day
  editPeriod.value = period
  Object.assign(cellForm, {
    id: cell.id, classId: cell.classId, dayOfWeek: day, periodNo: period,
    subject: cell.subject, teacherId: cell.teacherId ?? null,
    startTime: cell.startTime || '', endTime: cell.endTime || '',
  })
  cellDialog.value = true
}

async function submitCell() {
  if (!cellForm.subject) { alert('请填写课程名称'); return }
  cellSubmitting.value = true
  try {
    await saveScheduleEntry({
      ...cellForm,
      teacherId: cellForm.teacherId || undefined,
      startTime: cellForm.startTime || undefined,
      endTime: cellForm.endTime || undefined,
    })
    cellDialog.value = false
    await onClassChange()
  } finally { cellSubmitting.value = false }
}
</script>

<style scoped>
@reference "tailwindcss";
.label { @apply block text-xs font-bold text-[#7A7A6A] mb-1.5; }
.input-field { @apply w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40] transition-colors; }
</style>
