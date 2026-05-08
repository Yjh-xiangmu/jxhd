<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">总览数据</h3>
      <p class="text-sm text-[#A09E94] mt-1">欢迎回来，系统管理员</p>
    </div>

    <!-- 主要统计卡片 -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1] shadow-sm">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-9 h-9 bg-blue-50 rounded-xl flex items-center justify-center">
            <GraduationCap class="w-5 h-5 text-blue-500" />
          </div>
          <p class="text-[11px] font-bold uppercase tracking-wider text-[#A09E94]">教师</p>
        </div>
        <p class="text-3xl font-bold text-[#5A5A40]">{{ stats.teacherCount ?? '-' }}</p>
        <p class="text-xs text-[#A09E94] mt-1">在职教师账号</p>
      </div>

      <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1] shadow-sm">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-9 h-9 bg-pink-50 rounded-xl flex items-center justify-center">
            <Users class="w-5 h-5 text-pink-500" />
          </div>
          <p class="text-[11px] font-bold uppercase tracking-wider text-[#A09E94]">家长</p>
        </div>
        <p class="text-3xl font-bold text-[#5A5A40]">{{ stats.parentCount ?? '-' }}</p>
        <p class="text-xs text-[#A09E94] mt-1">注册家长账号</p>
      </div>

      <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1] shadow-sm">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-9 h-9 bg-green-50 rounded-xl flex items-center justify-center">
            <School class="w-5 h-5 text-green-500" />
          </div>
          <p class="text-[11px] font-bold uppercase tracking-wider text-[#A09E94]">班级</p>
        </div>
        <p class="text-3xl font-bold text-[#5A5A40]">{{ stats.classCount ?? '-' }}</p>
        <p class="text-xs text-[#A09E94] mt-1">开班中</p>
      </div>

      <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1] shadow-sm">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-9 h-9 bg-orange-50 rounded-xl flex items-center justify-center">
            <Baby class="w-5 h-5 text-orange-500" />
          </div>
          <p class="text-[11px] font-bold uppercase tracking-wider text-[#A09E94]">在园幼儿</p>
        </div>
        <p class="text-3xl font-bold text-[#5A5A40]">{{ stats.studentCount ?? '-' }}</p>
        <p class="text-xs text-[#A09E94] mt-1">在籍幼儿</p>
      </div>
    </div>

    <!-- 今日动态 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1]">
        <p class="text-[11px] font-bold uppercase tracking-wider text-[#A09E94] mb-3">今日出勤</p>
        <p class="text-2xl font-bold text-green-600">{{ stats.presentToday ?? 0 }} <span class="text-sm font-normal text-[#A09E94]">人</span></p>
        <div class="mt-3 h-1.5 bg-[#F5F2ED] rounded-full overflow-hidden">
          <div class="h-full bg-green-500 rounded-full transition-all"
            :style="{ width: attendanceRate + '%' }"></div>
        </div>
        <p class="text-[10px] text-[#A09E94] mt-1">出勤率 {{ attendanceRate }}%</p>
      </div>

      <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1]">
        <p class="text-[11px] font-bold uppercase tracking-wider text-[#A09E94] mb-3">今日成长动态</p>
        <p class="text-2xl font-bold text-[#5A5A40]">{{ stats.growthToday ?? 0 }} <span class="text-sm font-normal text-[#A09E94]">条</span></p>
        <p class="text-xs text-[#A09E94] mt-3">今日老师发布的成长记录</p>
      </div>

      <div class="bg-white rounded-2xl p-5 border border-[#EFE9E1]">
        <p class="text-[11px] font-bold uppercase tracking-wider text-[#A09E94] mb-3">待处理</p>
        <div class="space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs text-[#7A7A6A]">待审核绑定</span>
            <span class="text-sm font-bold" :class="stats.pendingBinds > 0 ? 'text-orange-500' : 'text-[#A09E94]'">
              {{ stats.pendingBinds ?? 0 }}
            </span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-xs text-[#7A7A6A]">开放活动</span>
            <span class="text-sm font-bold text-[#5A5A40]">{{ stats.openActivities ?? 0 }}</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-xs text-[#7A7A6A]">论坛帖子</span>
            <span class="text-sm font-bold text-[#5A5A40]">{{ stats.forumPosts ?? 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="bg-white rounded-2xl p-6 border border-[#EFE9E1]">
      <h4 class="text-sm font-bold text-[#5A5A40] uppercase tracking-wider mb-4">快捷操作</h4>
      <div class="flex flex-wrap gap-3">
        <router-link to="/admin/users"
          class="px-5 py-2 bg-[#5A5A40] text-white rounded-full text-sm font-medium hover:bg-[#4A4A35] transition-colors">
          管理用户
        </router-link>
        <router-link to="/admin/classes"
          class="px-5 py-2 border border-[#5A5A40] text-[#5A5A40] rounded-full text-sm font-medium hover:bg-[#F5F2ED] transition-colors">
          管理班级
        </router-link>
        <router-link to="/admin/news"
          class="px-5 py-2 border border-[#5A5A40] text-[#5A5A40] rounded-full text-sm font-medium hover:bg-[#F5F2ED] transition-colors">
          发布公告
        </router-link>
        <router-link to="/admin/schedule"
          class="px-5 py-2 border border-[#5A5A40] text-[#5A5A40] rounded-full text-sm font-medium hover:bg-[#F5F2ED] transition-colors">
          课表管理
        </router-link>
        <router-link to="/admin/logs"
          class="px-5 py-2 border border-[#EFE9E1] text-[#7A7A6A] rounded-full text-sm font-medium hover:bg-[#F5F2ED] transition-colors">
          操作日志
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { GraduationCap, Users, School, Baby } from 'lucide-vue-next'
import request from '../../utils/request'

const stats = ref<any>({})

const attendanceRate = computed(() => {
  const total   = stats.value.studentCount ?? 0
  const present = stats.value.presentToday ?? 0
  if (total === 0) return 0
  return Math.round((present / total) * 100)
})

onMounted(async () => {
  try {
    const res: any = await request.get('/api/dashboard/stats')
    stats.value = res.data
  } catch { /* ignore */ }
})
</script>
