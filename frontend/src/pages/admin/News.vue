<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">校园资讯管理</h3>
      <p class="text-sm text-[#A09E94] mt-1">发布与管理校园公告、新闻动态及亲子活动</p>
    </div>

    <!-- Tab 切换 -->
    <div class="flex gap-1 bg-[#F5F2ED] rounded-xl p-1 w-fit">
      <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
        class="px-5 py-2 rounded-lg text-sm font-medium transition-all"
        :class="activeTab === tab.key ? 'bg-white text-[#5A5A40] shadow-sm font-bold' : 'text-[#A09E94] hover:text-[#7A7A6A]'">
        {{ tab.label }}
      </button>
    </div>

    <!-- ========== Tab 1：公告/新闻 ========== -->
    <div v-show="activeTab === 'news'">
      <!-- 筛选栏 -->
      <div class="bg-white rounded-2xl p-4 border border-[#EFE9E1] flex flex-wrap items-center gap-3 mb-4">
        <select v-model="nq.type" @change="fetchNews"
          class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-[#F5F2ED] text-sm text-[#5A5A40] outline-none">
          <option value="">全部类型</option>
          <option value="announcement">校园公告</option>
          <option value="news">新闻动态</option>
        </select>
        <select v-model="nq.status" @change="fetchNews"
          class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-[#F5F2ED] text-sm text-[#5A5A40] outline-none">
          <option :value="undefined">全部状态</option>
          <option :value="1">已发布</option>
          <option :value="0">草稿</option>
        </select>
        <input v-model="nq.keyword" @keyup.enter="fetchNews" placeholder="搜索标题"
          class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-[#F5F2ED] text-sm outline-none w-48 focus:border-[#5A5A40]" />
        <button @click="fetchNews" class="h-9 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35] transition-colors">搜索</button>
        <button @click="resetNQ" class="h-9 px-4 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">重置</button>
        <button @click="openAddNews" class="ml-auto flex items-center gap-2 px-5 py-2 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
          <Plus class="w-4 h-4" /> 新建资讯
        </button>
      </div>

      <!-- 表格 -->
      <div class="bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-[#F5F2ED]">
            <tr>
              <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">标题</th>
              <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">类型</th>
              <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">状态</th>
              <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">发布人</th>
              <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">浏览量</th>
              <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">创建时间</th>
              <th class="px-6 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="nLoading"><td colspan="7" class="text-center py-12 text-[#A09E94]">加载中...</td></tr>
            <tr v-else-if="newsList.length === 0"><td colspan="7" class="text-center py-12 text-[#A09E94]">暂无数据</td></tr>
            <tr v-for="row in newsList" :key="row.id" class="border-t border-[#EFE9E1] hover:bg-[#FDFCF8] transition-colors">
              <td class="px-6 py-4 font-medium text-[#333322] max-w-xs">
                <button @click="openPreview(row)" class="text-left hover:text-[#5A5A40] hover:underline line-clamp-2">{{ row.title }}</button>
              </td>
              <td class="px-6 py-4">
                <span class="px-2.5 py-1 rounded-full text-[11px] font-bold"
                  :class="row.type === 'announcement' ? 'bg-purple-50 text-purple-600' : 'bg-blue-50 text-blue-600'">
                  {{ row.type === 'announcement' ? '校园公告' : '新闻动态' }}
                </span>
              </td>
              <td class="px-6 py-4">
                <span class="px-2.5 py-1 rounded-full text-[11px] font-bold"
                  :class="row.status === 1 ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'">
                  {{ row.status === 1 ? '已发布' : '草稿' }}
                </span>
              </td>
              <td class="px-6 py-4 text-[#7A7A6A]">{{ row.authorName }}</td>
              <td class="px-6 py-4 text-[#A09E94] text-xs">{{ row.viewCount }}</td>
              <td class="px-6 py-4 text-[#A09E94] text-xs">{{ formatTime(row.createTime) }}</td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <button @click="openEditNews(row)" class="text-xs text-[#5A5A40] hover:underline font-medium">编辑</button>
                  <button @click="handleNewsStatus(row)" class="text-xs hover:underline font-medium"
                    :class="row.status === 1 ? 'text-orange-500' : 'text-green-600'">
                    {{ row.status === 1 ? '撤稿' : '发布' }}
                  </button>
                  <button @click="handleDeleteNews(row.id)" class="text-xs text-red-400 hover:underline font-medium">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <div class="flex items-center justify-between px-6 py-4 border-t border-[#EFE9E1]">
          <span class="text-xs text-[#A09E94]">共 {{ nTotal }} 条</span>
          <div class="flex items-center gap-2">
            <button @click="nPrev" :disabled="nq.pageNum === 1"
              class="h-8 px-3 rounded-lg border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40 disabled:cursor-not-allowed">上一页</button>
            <span class="text-xs text-[#5A5A40] font-medium">{{ nq.pageNum }} / {{ nTotalPages }}</span>
            <button @click="nNext" :disabled="nq.pageNum >= nTotalPages"
              class="h-8 px-3 rounded-lg border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40 disabled:cursor-not-allowed">下一页</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== Tab 2：亲子活动 ========== -->
    <div v-show="activeTab === 'activity'">
      <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
        <div class="flex gap-3">
          <select v-model="aq.status" @change="fetchActivities"
            class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm text-[#5A5A40] outline-none">
            <option :value="undefined">全部状态</option>
            <option :value="1">报名中</option>
            <option :value="2">已结束</option>
            <option :value="0">草稿</option>
          </select>
        </div>
        <button @click="openAddActivity" class="flex items-center gap-2 px-5 py-2 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
          <Plus class="w-4 h-4" /> 新建活动
        </button>
      </div>

      <div v-if="aLoading" class="text-center py-16 text-[#A09E94]">加载中...</div>
      <div v-else-if="activityList.length === 0" class="text-center py-16 text-[#A09E94]">暂无活动</div>
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="act in activityList" :key="act.id"
          class="bg-white rounded-2xl border border-[#EFE9E1] p-6 hover:shadow-md transition-shadow">
          <!-- 头部 -->
          <div class="flex items-start justify-between mb-3">
            <h4 class="text-base font-bold text-[#333322] leading-tight flex-1 pr-2">{{ act.title }}</h4>
            <span class="text-[11px] px-2.5 py-1 rounded-full font-bold shrink-0" :class="actStatusClass(act.status)">
              {{ actStatusLabel(act.status) }}
            </span>
          </div>
          <!-- 详情 -->
          <div class="space-y-1.5 text-xs text-[#7A7A6A] mb-4">
            <div v-if="act.location" class="flex items-center gap-1.5"><MapPin class="w-3.5 h-3.5 shrink-0" />{{ act.location }}</div>
            <div v-if="act.activityTime" class="flex items-center gap-1.5"><Clock class="w-3.5 h-3.5 shrink-0" />活动时间：{{ formatTime(act.activityTime) }}</div>
            <div v-if="act.signupDeadline" class="flex items-center gap-1.5"><Calendar class="w-3.5 h-3.5 shrink-0" />截止报名：{{ formatTime(act.signupDeadline) }}</div>
            <div class="flex items-center gap-1.5">
              <Users class="w-3.5 h-3.5 shrink-0" />
              已报名 <span class="font-bold text-[#5A5A40]">{{ act.signupCount }}</span>
              <template v-if="act.maxCount"> / {{ act.maxCount }} 人</template>
              <template v-else> 人（不限名额）</template>
            </div>
          </div>
          <!-- 操作 -->
          <div class="flex gap-2 pt-4 border-t border-[#EFE9E1]">
            <button @click="openSignupList(act)" class="flex-1 py-1.5 text-xs font-medium text-blue-500 border border-blue-100 rounded-lg hover:bg-blue-50 transition-colors">
              查看报名
            </button>
            <button @click="openEditActivity(act)" class="flex-1 py-1.5 text-xs font-medium text-[#5A5A40] border border-[#EFE9E1] rounded-lg hover:bg-[#F5F2ED] transition-colors">
              编辑
            </button>
            <button v-if="act.status === 1" @click="handleActivityStatus(act, 2)"
              class="flex-1 py-1.5 text-xs font-medium text-orange-500 border border-orange-100 rounded-lg hover:bg-orange-50 transition-colors">结束</button>
            <button v-else-if="act.status === 2" @click="handleActivityStatus(act, 1)"
              class="flex-1 py-1.5 text-xs font-medium text-green-600 border border-green-100 rounded-lg hover:bg-green-50 transition-colors">重开</button>
            <button @click="handleDeleteActivity(act.id)" class="flex-1 py-1.5 text-xs font-medium text-red-400 border border-red-100 rounded-lg hover:bg-red-50 transition-colors">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 新建/编辑资讯弹框 ========== -->
    <div v-if="newsDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl p-8 w-full max-w-lg shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">{{ newsIsEdit ? '编辑资讯' : '新建资讯' }}</h4>
          <button @click="newsDialog = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="label">标题 *</label>
            <input v-model="newsForm.title" placeholder="请输入标题" class="input-field" />
          </div>
          <div>
            <label class="label">类型 *</label>
            <select v-model="newsForm.type" class="input-field">
              <option value="announcement">校园公告</option>
              <option value="news">新闻动态</option>
            </select>
          </div>
          <div>
            <label class="label">状态</label>
            <select v-model="newsForm.status" class="input-field">
              <option :value="1">立即发布</option>
              <option :value="0">保存草稿</option>
            </select>
          </div>
          <div>
            <label class="label">正文内容</label>
            <textarea v-model="newsForm.content" placeholder="请输入内容..." rows="6"
              class="w-full px-4 py-3 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40] transition-colors resize-none"></textarea>
          </div>
        </div>
        <div class="flex gap-3 mt-8">
          <button @click="newsDialog = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitNews" :disabled="newsSubmitting" class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ newsSubmitting ? '提交中...' : '确认' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ========== 资讯预览弹框 ========== -->
    <div v-if="previewDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl w-full max-w-2xl shadow-2xl overflow-hidden">
        <div class="bg-[#5A5A40] px-8 py-6 flex items-start justify-between">
          <div>
            <span class="text-[11px] font-bold px-2.5 py-1 rounded-full mb-3 inline-block"
              :class="previewItem?.type === 'announcement' ? 'bg-white/20 text-white' : 'bg-white/20 text-white'">
              {{ previewItem?.type === 'announcement' ? '校园公告' : '新闻动态' }}
            </span>
            <h4 class="text-xl font-bold text-white mt-1">{{ previewItem?.title }}</h4>
            <p class="text-[#A09E94] text-xs mt-2">
              {{ previewItem?.authorName }} · {{ formatTime(previewItem?.createTime) }}
            </p>
          </div>
          <button @click="previewDialog = false"><X class="w-5 h-5 text-white/70 hover:text-white" /></button>
        </div>
        <div class="p-8 max-h-[60vh] overflow-y-auto">
          <p v-if="previewItem?.content" class="text-sm text-[#333322] leading-relaxed whitespace-pre-wrap">{{ previewItem?.content }}</p>
          <p v-else class="text-[#A09E94] text-sm">（暂无正文内容）</p>
        </div>
      </div>
    </div>

    <!-- ========== 新建/编辑活动弹框 ========== -->
    <div v-if="activityDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl p-8 w-full max-w-lg shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">{{ actIsEdit ? '编辑活动' : '新建活动' }}</h4>
          <button @click="activityDialog = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="space-y-4 max-h-[65vh] overflow-y-auto pr-1">
          <div><label class="label">活动名称 *</label><input v-model="actForm.title" placeholder="请输入活动名称" class="input-field" /></div>
          <div><label class="label">活动地点</label><input v-model="actForm.location" placeholder="如：幼儿园操场" class="input-field" /></div>
          <div>
            <label class="label">活动时间</label>
            <input v-model="actForm.activityTime" type="datetime-local" class="input-field" />
          </div>
          <div>
            <label class="label">报名截止时间</label>
            <input v-model="actForm.signupDeadline" type="datetime-local" class="input-field" />
          </div>
          <div>
            <label class="label">最大报名人数（留空=不限）</label>
            <input v-model.number="actForm.maxCount" type="number" placeholder="不限填空" class="input-field" />
          </div>
          <div>
            <label class="label">状态</label>
            <select v-model="actForm.status" class="input-field">
              <option :value="1">立即开放报名</option>
              <option :value="0">保存草稿</option>
            </select>
          </div>
          <div>
            <label class="label">活动描述</label>
            <textarea v-model="actForm.description" placeholder="活动内容、注意事项等..." rows="4"
              class="w-full px-4 py-3 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40] transition-colors resize-none"></textarea>
          </div>
        </div>
        <div class="flex gap-3 mt-6">
          <button @click="activityDialog = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitActivity" :disabled="actSubmitting" class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ actSubmitting ? '提交中...' : '确认' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ========== 报名名单面板 ========== -->
    <div v-if="signupPanel" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl w-full max-w-2xl shadow-2xl overflow-hidden">
        <div class="bg-[#5A5A40] px-8 py-6 flex items-center justify-between">
          <div>
            <h4 class="text-xl font-bold text-white">报名名单</h4>
            <p class="text-[#A09E94] text-xs mt-1">{{ currentActivity?.title }} · 共 {{ signupList.length }} 人</p>
          </div>
          <button @click="signupPanel = false"><X class="w-5 h-5 text-white/70 hover:text-white" /></button>
        </div>
        <div class="p-6 max-h-[65vh] overflow-y-auto">
          <div v-if="signupLoading" class="text-center py-8 text-[#A09E94]">加载中...</div>
          <div v-else-if="signupList.length === 0" class="text-center py-8 text-[#A09E94]">
            <Calendar class="w-8 h-8 mx-auto mb-2 opacity-30" />
            <p class="text-sm">暂无报名记录</p>
          </div>
          <table v-else class="w-full text-sm">
            <thead class="bg-[#F5F2ED] rounded-xl">
              <tr>
                <th class="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">幼儿</th>
                <th class="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">班级</th>
                <th class="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">家长</th>
                <th class="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">联系方式</th>
                <th class="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">报名时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in signupList" :key="s.id" class="border-t border-[#EFE9E1] hover:bg-[#FDFCF8]">
                <td class="px-4 py-3 font-bold text-[#333322]">{{ s.studentName }}</td>
                <td class="px-4 py-3">
                  <span class="px-2 py-0.5 bg-[#F5F2ED] text-[#5A5A40] rounded-full text-[11px] font-bold">{{ s.className }}</span>
                </td>
                <td class="px-4 py-3 text-[#7A7A6A]">{{ s.parentName }}</td>
                <td class="px-4 py-3">
                  <a v-if="s.parentPhone" :href="`tel:${s.parentPhone}`" class="text-blue-500 text-xs hover:underline">{{ s.parentPhone }}</a>
                  <span v-else class="text-[#A09E94] text-xs">-</span>
                </td>
                <td class="px-4 py-3 text-[#A09E94] text-xs">{{ formatTime(s.createTime) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, X, MapPin, Clock, Calendar, Users } from 'lucide-vue-next'
import {
  getNewsList, addNews, updateNews, updateNewsStatus, deleteNews,
  getActivityList, addActivity, updateActivity, updateActivityStatus,
  deleteActivity, getActivitySignups
} from '../../api/news'

const tabs = [
  { key: 'news', label: '校园公告/新闻' },
  { key: 'activity', label: '亲子活动' },
]
const activeTab = ref('news')

// ── 资讯列表 ──────────────────────────────────────
const nLoading  = ref(false)
const newsList  = ref<any[]>([])
const nTotal    = ref(0)
const nq = reactive({ pageNum: 1, pageSize: 10, type: '', keyword: '', status: undefined as number | undefined })
const nTotalPages = computed(() => Math.max(1, Math.ceil(nTotal.value / nq.pageSize)))

async function fetchNews() {
  nLoading.value = true
  try {
    const res: any = await getNewsList(nq)
    newsList.value  = res.data.records
    nTotal.value    = res.data.total
  } finally { nLoading.value = false }
}

function resetNQ() {
  Object.assign(nq, { type: '', keyword: '', status: undefined, pageNum: 1 })
  fetchNews()
}
function nPrev() { if (nq.pageNum > 1) { nq.pageNum--; fetchNews() } }
function nNext() { if (nq.pageNum < nTotalPages.value) { nq.pageNum++; fetchNews() } }

onMounted(() => { fetchNews(); fetchActivities() })

async function handleNewsStatus(row: any) {
  const ns = row.status === 1 ? 0 : 1
  await updateNewsStatus(row.id, ns)
  fetchNews()
}
async function handleDeleteNews(id: number) {
  if (!confirm('确认删除该资讯吗？')) return
  await deleteNews(id); fetchNews()
}

// 编辑/新建资讯
const newsDialog    = ref(false)
const newsIsEdit    = ref(false)
const newsSubmitting = ref(false)
const newsForm = reactive({ id: undefined as number | undefined, title: '', type: 'announcement', content: '', status: 1 })

function openAddNews() {
  newsIsEdit.value = false
  Object.assign(newsForm, { id: undefined, title: '', type: 'announcement', content: '', status: 1 })
  newsDialog.value = true
}
function openEditNews(row: any) {
  newsIsEdit.value = true
  Object.assign(newsForm, { id: row.id, title: row.title, type: row.type, content: row.content || '', status: row.status })
  newsDialog.value = true
}
async function submitNews() {
  if (!newsForm.title) { alert('请填写标题'); return }
  newsSubmitting.value = true
  try {
    newsIsEdit.value ? await updateNews(newsForm.id!, newsForm) : await addNews(newsForm)
    newsDialog.value = false; fetchNews()
  } finally { newsSubmitting.value = false }
}

// 预览
const previewDialog = ref(false)
const previewItem   = ref<any>(null)
function openPreview(row: any) { previewItem.value = row; previewDialog.value = true }

// ── 活动列表 ──────────────────────────────────────
const aLoading     = ref(false)
const activityList = ref<any[]>([])
const aq = reactive({ status: undefined as number | undefined })

async function fetchActivities() {
  aLoading.value = true
  try {
    const res: any = await getActivityList(aq)
    activityList.value = res.data
  } finally { aLoading.value = false }
}

async function handleActivityStatus(act: any, status: number) {
  const label = status === 2 ? '结束' : '重新开放'
  if (!confirm(`确认${label}活动「${act.title}」吗？`)) return
  await updateActivityStatus(act.id, status); fetchActivities()
}
async function handleDeleteActivity(id: number) {
  if (!confirm('确认删除该活动吗？报名记录也将一并清除。')) return
  await deleteActivity(id); fetchActivities()
}

// 编辑/新建活动
const activityDialog = ref(false)
const actIsEdit      = ref(false)
const actSubmitting  = ref(false)
const actForm = reactive({
  id: undefined as number | undefined,
  title: '', description: '', location: '',
  activityTime: '', signupDeadline: '',
  maxCount: null as number | null, status: 1,
})

function openAddActivity() {
  actIsEdit.value = false
  Object.assign(actForm, { id: undefined, title: '', description: '', location: '', activityTime: '', signupDeadline: '', maxCount: null, status: 1 })
  activityDialog.value = true
}
function openEditActivity(act: any) {
  actIsEdit.value = true
  Object.assign(actForm, {
    id: act.id, title: act.title, description: act.description || '',
    location: act.location || '',
    activityTime: toDatetimeLocal(act.activityTime),
    signupDeadline: toDatetimeLocal(act.signupDeadline),
    maxCount: act.maxCount ?? null, status: act.status,
  })
  activityDialog.value = true
}
async function submitActivity() {
  if (!actForm.title) { alert('请填写活动名称'); return }
  actSubmitting.value = true
  try {
    const payload = {
      ...actForm,
      activityTime: actForm.activityTime || undefined,
      signupDeadline: actForm.signupDeadline || undefined,
      maxCount: actForm.maxCount || null,
    }
    actIsEdit.value ? await updateActivity(actForm.id!, payload) : await addActivity(payload)
    activityDialog.value = false; fetchActivities()
  } finally { actSubmitting.value = false }
}

// 报名名单
const signupPanel   = ref(false)
const signupLoading = ref(false)
const signupList    = ref<any[]>([])
const currentActivity = ref<any>(null)

async function openSignupList(act: any) {
  currentActivity.value = act
  signupPanel.value = true
  signupLoading.value = true
  try {
    const res: any = await getActivitySignups(act.id)
    signupList.value = res.data
  } finally { signupLoading.value = false }
}

// ── 工具函数 ──────────────────────────────────────
function formatTime(t: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}
function toDatetimeLocal(t: string) {
  if (!t) return ''
  return t.replace(' ', 'T').slice(0, 16)
}
function actStatusLabel(status: number) {
  return { 0: '草稿', 1: '报名中', 2: '已结束' }[status] ?? '-'
}
function actStatusClass(status: number) {
  return {
    0: 'bg-gray-100 text-gray-400',
    1: 'bg-green-50 text-green-600',
    2: 'bg-orange-50 text-orange-500',
  }[status] ?? 'bg-gray-100 text-gray-400'
}
</script>

<style scoped>
@reference "tailwindcss";
.label { @apply block text-xs font-bold text-[#7A7A6A] mb-1.5; }
.input-field { @apply w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40] transition-colors; }
</style>
