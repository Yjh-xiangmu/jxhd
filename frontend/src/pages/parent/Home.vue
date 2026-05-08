<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h3 class="text-2xl font-bold text-[#5A5A40]">在家表现补充</h3>
        <p class="text-sm text-[#A09E94] mt-1">记录孩子在家的成长点滴，与老师共享</p>
      </div>
      <button @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
        <Plus class="w-4 h-4" /> 添加记录
      </button>
    </div>

    <!-- 孩子切换 -->
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
    <div v-else-if="!currentChild"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <HomeIcon class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无绑定孩子</p>
    </div>
    <div v-else-if="records.length === 0"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <BookOpen class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无记录</p>
      <p class="text-xs mt-1">点击右上角记录今天孩子在家的表现</p>
    </div>
    <div v-else class="space-y-3">
      <div v-for="rec in records" :key="rec.id"
        class="bg-white rounded-2xl border border-[#EFE9E1] p-5 hover:shadow-sm transition-shadow">
        <div class="flex items-start justify-between gap-3">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-2">
              <span class="text-xs font-bold px-2.5 py-1 bg-[#F5F2ED] rounded-full text-[#5A5A40]">
                {{ rec.recordDate }}
              </span>
              <span class="text-[10px] text-[#A09E94]">{{ formatTime(rec.createTime) }} 提交</span>
            </div>
            <p class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap">{{ rec.content }}</p>
          </div>
          <button @click="deleteRec(rec)"
            class="text-[#A09E94] hover:text-red-400 transition-colors p-1 rounded-lg hover:bg-red-50 shrink-0">
            <Trash2 class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- 添加记录弹窗 -->
    <div v-if="modal.show" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-3xl w-full max-w-md shadow-2xl">
        <div class="px-7 py-5 bg-[#F5F2ED] border-b border-[#EFE9E1] rounded-t-3xl">
          <h4 class="text-base font-bold text-[#5A5A40]">记录在家表现</h4>
        </div>
        <div class="p-7 space-y-4">
          <div v-if="children.length > 1">
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">选择孩子</label>
            <select v-model="modal.studentId"
              class="w-full h-10 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none focus:border-[#5A5A40]">
              <option v-for="c in children" :key="c.studentId" :value="c.studentId">{{ c.studentName }}</option>
            </select>
          </div>
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">记录日期</label>
            <input type="date" v-model="modal.recordDate"
              class="w-full h-10 px-3 rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">表现描述</label>
            <textarea v-model="modal.content" rows="4"
              placeholder="描述孩子今天在家的情绪、饮食、睡眠或有趣的事情..."
              class="w-full px-3 py-2 rounded-xl border border-[#EFE9E1] text-sm outline-none resize-none focus:border-[#5A5A40]" />
          </div>
        </div>
        <div class="px-7 pb-7 flex gap-3">
          <button @click="modal.show = false"
            class="flex-1 h-9 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED]">取消</button>
          <button @click="submitRecord" :disabled="modal.submitting"
            class="flex-1 h-9 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] disabled:opacity-50">
            {{ modal.submitting ? '提交中...' : '提交' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Home as HomeIcon, BookOpen, Trash2 } from 'lucide-vue-next'
import { getMyChildren, getHomeRecords, addHomeRecord, deleteHomeRecord } from '../../api/parent'

const loading      = ref(false)
const children     = ref<any[]>([])
const currentChild = ref<any>(null)
const records      = ref<any[]>([])

const modal = reactive({
  show: false,
  studentId: '' as string | number,
  recordDate: new Date().toISOString().slice(0, 10),
  content: '',
  submitting: false,
})

async function loadChildren() {
  const res: any = await getMyChildren()
  children.value = res.data
  if (children.value.length > 0) await selectChild(children.value[0])
}

async function selectChild(child: any) {
  currentChild.value = child
  loading.value = true
  try {
    const res: any = await getHomeRecords(child.studentId)
    records.value = res.data
  } finally { loading.value = false }
}

function openAddModal() {
  modal.studentId  = currentChild.value?.studentId ?? (children.value[0]?.studentId ?? '')
  modal.recordDate = new Date().toISOString().slice(0, 10)
  modal.content    = ''
  modal.show       = true
}

async function submitRecord() {
  if (!modal.content.trim()) { alert('请填写表现描述'); return }
  modal.submitting = true
  try {
    await addHomeRecord({
      studentId: modal.studentId,
      content: modal.content,
      recordDate: modal.recordDate,
    })
    modal.show = false
    if (currentChild.value) await selectChild(currentChild.value)
  } finally { modal.submitting = false }
}

async function deleteRec(rec: any) {
  if (!confirm('确认删除这条记录？')) return
  await deleteHomeRecord(rec.id)
  if (currentChild.value) await selectChild(currentChild.value)
}

function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

onMounted(async () => {
  loading.value = true
  try { await loadChildren() } finally { loading.value = false }
})
</script>
