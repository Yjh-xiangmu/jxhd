<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h3 class="text-2xl font-bold text-[#5A5A40]">成长动态发布</h3>
        <p class="text-sm text-[#A09E94] mt-1">记录班级幼儿的点滴成长</p>
      </div>
      <button @click="showForm = true"
        class="flex items-center gap-2 px-4 py-2 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
        <Plus class="w-4 h-4" /> 发布动态
      </button>
    </div>

    <!-- 动态列表 -->
    <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
    <div v-else-if="records.length === 0"
      class="text-center py-20 text-[#A09E94] bg-white rounded-2xl border border-[#EFE9E1]">
      <Camera class="w-12 h-12 mx-auto mb-3 opacity-20" />
      <p class="text-sm font-medium">暂无动态</p>
      <p class="text-xs mt-1">点击右上角发布第一条成长动态吧</p>
    </div>
    <div v-else class="space-y-4">
      <div v-for="rec in records" :key="rec.id"
        class="bg-white rounded-2xl border border-[#EFE9E1] p-5 hover:shadow-sm transition-shadow">
        <!-- 头部 -->
        <div class="flex items-start justify-between gap-3 mb-3">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] font-bold shrink-0">
              {{ (rec.authorName || '?').charAt(0) }}
            </div>
            <div>
              <p class="text-sm font-bold text-[#333322]">{{ rec.authorName }}</p>
              <p class="text-xs text-[#A09E94]">{{ formatTime(rec.createTime) }}</p>
            </div>
          </div>
          <button @click="confirmDelete(rec)"
            class="text-[#A09E94] hover:text-red-400 transition-colors p-1 rounded-lg hover:bg-red-50">
            <Trash2 class="w-4 h-4" />
          </button>
        </div>

        <!-- 幼儿标签 -->
        <div class="flex items-center gap-2 mb-3">
          <span class="inline-flex items-center gap-1 px-3 py-1 bg-[#F5F2ED] rounded-full text-xs font-bold text-[#5A5A40]">
            <Baby class="w-3 h-3" /> {{ rec.studentName }}
          </span>
          <span class="text-xs text-[#A09E94]">{{ rec.className }}</span>
        </div>

        <!-- 文字内容 -->
        <p class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap">{{ rec.content }}</p>

        <!-- 图片 -->
        <div v-if="rec.images && rec.images.length > 0"
          class="mt-3 grid gap-2"
          :class="rec.images.length === 1 ? 'grid-cols-1' : rec.images.length === 2 ? 'grid-cols-2' : 'grid-cols-3'">
          <img v-for="(img, i) in rec.images" :key="i"
            :src="imgUrl(img)"
            @click="openLightbox(rec.images, i)"
            class="w-full rounded-xl object-cover cursor-pointer hover:opacity-90 transition-opacity"
            :class="rec.images.length === 1 ? 'max-h-64' : 'h-28'"
            alt="" />
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="flex items-center justify-center gap-3 mt-4">
      <button @click="prevPage" :disabled="pageNum === 1"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">上一页</button>
      <span class="text-xs text-[#5A5A40] font-medium">{{ pageNum }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="pageNum >= totalPages"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">下一页</button>
    </div>

    <!-- ── 发布动态弹窗 ── -->
    <div v-if="showForm" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden">
        <div class="flex items-center justify-between px-7 py-5 bg-[#F5F2ED] border-b border-[#EFE9E1]">
          <h4 class="text-lg font-bold text-[#5A5A40]">发布成长动态</h4>
          <button @click="closeForm"><X class="w-5 h-5 text-[#A09E94] hover:text-[#5A5A40]" /></button>
        </div>
        <div class="p-7 space-y-5">
          <!-- 选择幼儿 -->
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">选择幼儿</label>
            <select v-model="form.studentId"
              class="w-full h-10 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm text-[#5A5A40] outline-none focus:border-[#5A5A40]">
              <option value="">请选择幼儿</option>
              <option v-for="s in studentOptions" :key="s.id" :value="s.id">{{ s.name }}</option>
            </select>
          </div>

          <!-- 内容 -->
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">动态内容</label>
            <textarea v-model="form.content" rows="4" placeholder="记录幼儿今天的精彩瞬间..."
              class="w-full px-3 py-2 rounded-xl border border-[#EFE9E1] text-sm outline-none resize-none focus:border-[#5A5A40]" />
          </div>

          <!-- 图片上传 -->
          <div>
            <label class="text-xs font-bold text-[#A09E94] uppercase tracking-wider mb-2 block">
              添加图片（最多9张）
            </label>
            <div class="flex flex-wrap gap-2">
              <div v-for="(img, i) in form.images" :key="i"
                class="relative w-20 h-20 rounded-xl overflow-hidden group">
                <img :src="imgUrl(img)" class="w-full h-full object-cover" alt="" />
                <button @click="removeImage(i)"
                  class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 flex items-center justify-center transition-opacity">
                  <X class="w-5 h-5 text-white" />
                </button>
              </div>
              <label v-if="form.images.length < 9"
                class="w-20 h-20 rounded-xl border-2 border-dashed border-[#EFE9E1] flex flex-col items-center justify-center cursor-pointer hover:border-[#5A5A40] transition-colors text-[#A09E94] hover:text-[#5A5A40]">
                <ImagePlus class="w-5 h-5 mb-1" />
                <span class="text-[10px]">添加图片</span>
                <input type="file" accept="image/*" multiple class="hidden" @change="handleImageSelect" />
              </label>
              <div v-if="uploading" class="w-20 h-20 rounded-xl border border-[#EFE9E1] flex items-center justify-center">
                <Loader2 class="w-5 h-5 animate-spin text-[#A09E94]" />
              </div>
            </div>
          </div>
        </div>
        <div class="px-7 py-5 border-t border-[#EFE9E1] flex justify-end gap-3">
          <button @click="closeForm"
            class="h-9 px-5 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED]">取消</button>
          <button @click="submit" :disabled="submitting"
            class="h-9 px-6 bg-[#5A5A40] text-white rounded-xl text-sm font-bold hover:bg-[#4A4A35] disabled:opacity-50">
            {{ submitting ? '发布中...' : '发布' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 图片灯箱 -->
    <div v-if="lightbox.show" class="fixed inset-0 bg-black/90 flex items-center justify-center z-[60]"
      @click="lightbox.show = false">
      <button class="absolute top-4 right-4 text-white/70 hover:text-white" @click="lightbox.show = false">
        <X class="w-7 h-7" />
      </button>
      <button v-if="lightbox.index > 0" @click.stop="lightbox.index--"
        class="absolute left-4 text-white/70 hover:text-white p-2">
        <ChevronLeft class="w-8 h-8" />
      </button>
      <img :src="imgUrl(lightbox.images[lightbox.index])" class="max-w-[90vw] max-h-[90vh] object-contain rounded-xl"
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
import { Plus, X, Camera, Baby, Trash2, ImagePlus, Loader2, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { getGrowthRecords, addGrowthRecord, deleteGrowthRecord, uploadImage } from '../../api/growth'
import { getStudentList } from '../../api/class'
import { getMyClass } from '../../api/schedule'

const API_BASE = 'http://localhost:8080'

const loading   = ref(false)
const records   = ref<any[]>([])
const total     = ref(0)
const pageNum   = ref(1)
const pageSize  = ref(10)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const myClass       = ref<any>(null)
const studentOptions = ref<any[]>([])

const showForm  = ref(false)
const submitting = ref(false)
const uploading  = ref(false)

const form = reactive({
  studentId: '' as string | number,
  content: '',
  images: [] as string[],
})

const lightbox = reactive({ show: false, images: [] as string[], index: 0 })

async function fetchRecords() {
  loading.value = true
  try {
    const res: any = await getGrowthRecords({ pageNum: pageNum.value, pageSize: pageSize.value })
    records.value = res.data.records
    total.value   = res.data.total
  } finally { loading.value = false }
}

async function fetchStudents() {
  if (!myClass.value) return
  const res: any = await getStudentList({ pageNum: 1, pageSize: 200, classId: myClass.value.id, status: 1 })
  studentOptions.value = res.data.records
}

function prevPage() { if (pageNum.value > 1) { pageNum.value--; fetchRecords() } }
function nextPage() { if (pageNum.value < totalPages.value) { pageNum.value++; fetchRecords() } }

function imgUrl(path: string) {
  if (!path) return ''
  return path.startsWith('http') ? path : API_BASE + path
}

function closeForm() {
  showForm.value = false
  form.studentId = ''
  form.content   = ''
  form.images    = []
}

async function handleImageSelect(e: Event) {
  const files = (e.target as HTMLInputElement).files
  if (!files) return
  const remaining = 9 - form.images.length
  const toUpload  = Array.from(files).slice(0, remaining)
  uploading.value = true
  try {
    for (const file of toUpload) {
      const res: any = await uploadImage(file)
      form.images.push(res.data)
    }
  } finally {
    uploading.value = false
    ;(e.target as HTMLInputElement).value = ''
  }
}

function removeImage(i: number) { form.images.splice(i, 1) }

async function submit() {
  if (!form.studentId) { alert('请选择幼儿'); return }
  if (!form.content.trim()) { alert('请填写动态内容'); return }
  submitting.value = true
  try {
    await addGrowthRecord({ studentId: form.studentId as number, content: form.content, images: form.images })
    closeForm()
    pageNum.value = 1
    fetchRecords()
  } finally { submitting.value = false }
}

async function confirmDelete(rec: any) {
  if (!confirm(`确认删除「${rec.studentName}」的这条动态？`)) return
  await deleteGrowthRecord(rec.id)
  fetchRecords()
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
  try {
    const r: any = await getMyClass()
    myClass.value = r.data
    await fetchStudents()
  } catch { /* no class */ }
  fetchRecords()
})
</script>
