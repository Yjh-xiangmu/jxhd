<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">系统操作日志</h3>
      <p class="text-sm text-[#A09E94] mt-1">记录系统内所有关键操作</p>
    </div>

    <!-- 筛选栏 -->
    <div class="flex flex-wrap items-center gap-3">
      <input v-model="filters.username" placeholder="操作人账号"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none w-36 focus:border-[#5A5A40]" />
      <input v-model="filters.module" placeholder="模块名称"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none w-32 focus:border-[#5A5A40]" />
      <input type="date" v-model="filters.dateFrom"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none focus:border-[#5A5A40]" />
      <span class="text-[#A09E94] text-sm">至</span>
      <input type="date" v-model="filters.dateTo"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none focus:border-[#5A5A40]" />
      <button @click="search"
        class="h-9 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35] transition-colors">搜索</button>
      <button @click="resetFilters"
        class="h-9 px-4 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">重置</button>
      <span class="ml-auto text-xs text-[#A09E94]">共 {{ total }} 条记录</span>
    </div>

    <!-- 表格 -->
    <div class="bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">
      <div v-if="loading" class="text-center py-16 text-[#A09E94]">加载中...</div>
      <div v-else-if="logs.length === 0" class="text-center py-16 text-[#A09E94]">
        <ShieldAlert class="w-10 h-10 mx-auto mb-2 opacity-20" />
        <p class="text-sm">暂无日志数据</p>
      </div>
      <table v-else class="w-full text-sm">
        <thead>
          <tr class="border-b border-[#F5F2ED] bg-[#FDFCF8]">
            <th class="text-left px-5 py-3 text-[10px] font-bold uppercase tracking-wider text-[#A09E94]">时间</th>
            <th class="text-left px-5 py-3 text-[10px] font-bold uppercase tracking-wider text-[#A09E94]">操作人</th>
            <th class="text-left px-5 py-3 text-[10px] font-bold uppercase tracking-wider text-[#A09E94]">模块</th>
            <th class="text-left px-5 py-3 text-[10px] font-bold uppercase tracking-wider text-[#A09E94]">操作</th>
            <th class="text-left px-5 py-3 text-[10px] font-bold uppercase tracking-wider text-[#A09E94]">详情</th>
            <th class="text-left px-5 py-3 text-[10px] font-bold uppercase tracking-wider text-[#A09E94]">IP</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in logs" :key="log.id"
            class="border-b border-[#F5F2ED] hover:bg-[#FDFCF8] transition-colors">
            <td class="px-5 py-3 text-[#A09E94] text-xs whitespace-nowrap">{{ formatTime(log.createTime) }}</td>
            <td class="px-5 py-3 font-medium text-[#333322]">{{ log.username }}</td>
            <td class="px-5 py-3">
              <span class="px-2 py-0.5 bg-[#F5F2ED] rounded text-xs text-[#5A5A40] font-medium">{{ log.module || '-' }}</span>
            </td>
            <td class="px-5 py-3 text-[#333322]">{{ log.action || '-' }}</td>
            <td class="px-5 py-3 text-[#7A7A6A] text-xs max-w-xs truncate">{{ log.detail || '-' }}</td>
            <td class="px-5 py-3 text-[#A09E94] text-xs">{{ log.ip || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="flex items-center justify-center gap-3">
      <button @click="prevPage" :disabled="pageNum === 1"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">上一页</button>
      <span class="text-xs text-[#5A5A40] font-medium">{{ pageNum }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="pageNum >= totalPages"
        class="h-8 px-4 rounded-xl border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ShieldAlert } from 'lucide-vue-next'
import request from '../../utils/request'

const loading  = ref(false)
const logs     = ref<any[]>([])
const total    = ref(0)
const pageNum  = ref(1)
const pageSize = ref(20)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const filters = reactive({ username: '', module: '', dateFrom: '', dateTo: '' })

async function fetchLogs() {
  loading.value = true
  try {
    const res: any = await request.get('/api/logs', {
      params: {
        pageNum: pageNum.value, pageSize: pageSize.value,
        username:  filters.username  || undefined,
        module:    filters.module    || undefined,
        dateFrom:  filters.dateFrom  || undefined,
        dateTo:    filters.dateTo    || undefined,
      }
    })
    logs.value  = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function search()        { pageNum.value = 1; fetchLogs() }
function resetFilters()  { Object.assign(filters, { username: '', module: '', dateFrom: '', dateTo: '' }); search() }
function prevPage()      { if (pageNum.value > 1)                 { pageNum.value--; fetchLogs() } }
function nextPage()      { if (pageNum.value < totalPages.value)  { pageNum.value++; fetchLogs() } }

function formatTime(t: string) { return t ? t.replace('T', ' ').slice(0, 16) : '-' }

onMounted(fetchLogs)
</script>
