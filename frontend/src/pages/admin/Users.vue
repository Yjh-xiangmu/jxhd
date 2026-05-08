<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h3 class="text-2xl font-bold text-[#5A5A40]">用户与权限</h3>
        <p class="text-sm text-[#A09E94] mt-1">管理教师和家长账号</p>
      </div>
      <button @click="openAdd" class="flex items-center gap-2 px-5 py-2.5 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
        <Plus class="w-4 h-4" /> 新增用户
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-2xl p-4 border border-[#EFE9E1] flex flex-wrap gap-3">
      <select v-model="query.role" @change="fetchList"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-[#F5F2ED] text-sm text-[#5A5A40] outline-none">
        <option value="">全部角色</option>
        <option value="teacher">教师</option>
        <option value="parent">家长</option>
        <option value="admin">管理员</option>
      </select>
      <input v-model="query.keyword" @keyup.enter="fetchList" placeholder="搜索姓名/账号/手机号"
        class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-[#F5F2ED] text-sm outline-none w-56 focus:border-[#5A5A40]" />
      <button @click="fetchList" class="h-9 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35] transition-colors">
        搜索
      </button>
      <button @click="resetQuery" class="h-9 px-4 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">
        重置
      </button>
    </div>

    <!-- 表格 -->
    <div class="bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-[#F5F2ED]">
          <tr>
            <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">账号</th>
            <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">姓名</th>
            <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">角色</th>
            <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">手机号</th>
            <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">状态</th>
            <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">创建时间</th>
            <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="text-center py-12 text-[#A09E94]">加载中...</td>
          </tr>
          <tr v-else-if="tableData.length === 0">
            <td colspan="7" class="text-center py-12 text-[#A09E94]">暂无数据</td>
          </tr>
          <tr v-for="row in tableData" :key="row.id"
            class="border-t border-[#EFE9E1] hover:bg-[#FDFCF8] transition-colors">
            <td class="px-6 py-4 font-medium text-[#333322]">{{ row.username }}</td>
            <td class="px-6 py-4 text-[#333322]">{{ row.realName || '-' }}</td>
            <td class="px-6 py-4">
              <span class="px-2.5 py-1 rounded-full text-[11px] font-bold" :class="roleClass(row.role)">
                {{ roleLabel(row.role) }}
              </span>
            </td>
            <td class="px-6 py-4 text-[#7A7A6A]">{{ row.phone || '-' }}</td>
            <td class="px-6 py-4">
              <span class="px-2.5 py-1 rounded-full text-[11px] font-bold"
                :class="row.status === 1 ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-500'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td class="px-6 py-4 text-[#A09E94] text-xs">{{ formatTime(row.createTime) }}</td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-2">
                <button @click="openEdit(row)" class="text-xs text-[#5A5A40] hover:underline font-medium">编辑</button>
                <button @click="handleStatus(row)" class="text-xs hover:underline font-medium"
                  :class="row.status === 1 ? 'text-orange-500' : 'text-green-600'">
                  {{ row.status === 1 ? '禁用' : '启用' }}
                </button>
                <button @click="handleReset(row.id)" class="text-xs text-blue-500 hover:underline font-medium">重置密码</button>
                <button @click="handleDelete(row.id)" class="text-xs text-red-400 hover:underline font-medium">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div class="flex items-center justify-between px-6 py-4 border-t border-[#EFE9E1]">
        <span class="text-xs text-[#A09E94]">共 {{ total }} 条</span>
        <div class="flex items-center gap-2">
          <button @click="prevPage" :disabled="query.pageNum === 1"
            class="h-8 px-3 rounded-lg border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40 disabled:cursor-not-allowed transition-colors">
            上一页
          </button>
          <span class="text-xs text-[#5A5A40] font-medium">{{ query.pageNum }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="query.pageNum >= totalPages"
            class="h-8 px-3 rounded-lg border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40 disabled:cursor-not-allowed transition-colors">
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹框 -->
    <div v-if="dialogVisible" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">{{ isEdit ? '编辑用户' : '新增用户' }}</h4>
          <button @click="dialogVisible = false" class="text-[#A09E94] hover:text-[#5A5A40]">
            <X class="w-5 h-5" />
          </button>
        </div>

        <div class="space-y-4">
          <div v-if="!isEdit">
            <label class="block text-xs font-bold text-[#7A7A6A] mb-1.5">账号 *</label>
            <input v-model="form.username" placeholder="请输入登录账号"
              class="w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
          <div v-if="!isEdit">
            <label class="block text-xs font-bold text-[#7A7A6A] mb-1.5">初始密码</label>
            <input v-model="form.password" placeholder="不填则默认 123456"
              class="w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
          <div>
            <label class="block text-xs font-bold text-[#7A7A6A] mb-1.5">角色 *</label>
            <select v-model="form.role" :disabled="isEdit"
              class="w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]">
              <option value="">请选择角色</option>
              <option value="admin">管理员</option>
              <option value="teacher">教师</option>
              <option value="parent">家长</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-bold text-[#7A7A6A] mb-1.5">真实姓名</label>
            <input v-model="form.realName" placeholder="请输入真实姓名"
              class="w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
          <div>
            <label class="block text-xs font-bold text-[#7A7A6A] mb-1.5">手机号</label>
            <input v-model="form.phone" placeholder="请输入手机号"
              class="w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40]" />
          </div>
        </div>

        <div class="flex gap-3 mt-8">
          <button @click="dialogVisible = false"
            class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm font-medium hover:bg-[#F5F2ED] transition-colors">
            取消
          </button>
          <button @click="handleSubmit" :disabled="submitting"
            class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ submitting ? '提交中...' : '确认' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, X } from 'lucide-vue-next'
import {
  getUserList, addUser, updateUser,
  updateUserStatus, resetPassword, deleteUser
} from '../../api/user'

// ── 列表 ────────────────────────────────────────
const loading   = ref(false)
const tableData = ref<any[]>([])
const total     = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  role: '',
  keyword: '',
})

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / query.pageSize)))

async function fetchList() {
  loading.value = true
  try {
    const res: any = await getUserList(query)
    tableData.value = res.data.records
    total.value     = res.data.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.role    = ''
  query.keyword = ''
  query.pageNum = 1
  fetchList()
}

function prevPage() { if (query.pageNum > 1) { query.pageNum--; fetchList() } }
function nextPage() { if (query.pageNum < totalPages.value) { query.pageNum++; fetchList() } }

onMounted(fetchList)

// ── 操作 ────────────────────────────────────────
async function handleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const label     = newStatus === 0 ? '禁用' : '启用'
  if (!confirm(`确认${label}用户「${row.realName || row.username}」吗？`)) return
  await updateUserStatus(row.id, newStatus)
  fetchList()
}

async function handleReset(id: number) {
  if (!confirm('确认将该用户密码重置为 123456 吗？')) return
  await resetPassword(id)
  alert('密码已重置为 123456')
}

async function handleDelete(id: number) {
  if (!confirm('确认删除该用户吗？此操作不可撤销！')) return
  await deleteUser(id)
  fetchList()
}

// ── 弹框 ────────────────────────────────────────
const dialogVisible = ref(false)
const isEdit        = ref(false)
const submitting    = ref(false)

const form = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  role: '',
  realName: '',
  phone: '',
})

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, username: '', password: '', role: '', realName: '', phone: '' })
  dialogVisible.value = true
}

function openEdit(row: any) {
  isEdit.value = true
  Object.assign(form, { id: row.id, username: row.username, role: row.role, realName: row.realName || '', phone: row.phone || '', password: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.role) { alert('请选择角色'); return }
  if (!isEdit.value && !form.username) { alert('请输入账号'); return }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateUser(form.id!, form)
    } else {
      await addUser(form)
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

// ── 工具函数 ─────────────────────────────────────
function roleLabel(role: string) {
  return { admin: '管理员', teacher: '教师', parent: '家长' }[role] || role
}
function roleClass(role: string) {
  return {
    admin:   'bg-purple-50 text-purple-600',
    teacher: 'bg-blue-50 text-blue-600',
    parent:  'bg-orange-50 text-orange-500',
  }[role] || 'bg-gray-50 text-gray-500'
}
function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}
</script>
