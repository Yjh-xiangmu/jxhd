<template>
  <div class="flex h-screen bg-[#FDFCF8] text-[#333322] overflow-hidden font-sans">
    <aside class="w-64 bg-[#F5F2ED] border-r border-[#EFE9E1] flex flex-col p-6 gap-6 shrink-0">
      <div class="flex items-center gap-3 mb-2">
        <div class="w-10 h-10 bg-[#5A5A40] rounded-xl flex items-center justify-center shrink-0">
          <div class="w-5 h-5 border-2 border-white rounded-full"></div>
        </div>
        <div>
          <h1 class="text-xl font-bold tracking-tight text-[#5A5A40] uppercase leading-none">
            Smart<span class="font-light opacity-60">Kiddy</span>
          </h1>
          <p class="text-[10px] text-[#A09E94] font-medium mt-1">{{ sidebarSubtitle }}</p>
        </div>
      </div>

      <nav class="flex-1 overflow-y-auto space-y-2">
        <p class="text-[10px] uppercase tracking-widest text-[#A09E94] font-bold mb-4">Core Functions</p>
        <router-link to="/teacher/students"   active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Users class="w-5 h-5 shrink-0" /> 班级花名册
        </router-link>
        <router-link to="/teacher/messages"   active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <MessageCircle class="w-5 h-5 shrink-0" /> 家校通讯录
        </router-link>
        <router-link to="/teacher/growth"     active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Camera class="w-5 h-5 shrink-0" /> 成长动态发布
        </router-link>
        <router-link to="/teacher/schedule"   active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <CalendarClock class="w-5 h-5 shrink-0" /> 考勤与课表
        </router-link>
        <router-link to="/teacher/activities" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Activity class="w-5 h-5 shrink-0" /> 活动跟进
        </router-link>
        <router-link to="/teacher/forum"      active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <BookOpenCheck class="w-5 h-5 shrink-0" /> 论坛互动
        </router-link>
      </nav>

      <div class="mt-auto">
        <button @click="handleLogout" class="w-full flex items-center gap-3 px-3 py-2 text-[#7A7A6A] hover:bg-[#EAE5DD] rounded-lg text-sm font-medium transition-colors">
          <LogOut class="w-5 h-5 shrink-0" /> 退出系统
        </button>
      </div>
    </aside>

    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <header class="h-16 bg-white border-b border-[#EFE9E1] flex items-center justify-between px-8 shrink-0 shadow-sm">
        <h2 class="text-xl font-serif italic text-[#5A5A40]">教师工作台</h2>
        <div class="flex items-center gap-3 border-l pl-6 border-[#EFE9E1]">
          <p class="text-xs font-bold leading-none text-[#5A5A40]">{{ displayName }}</p>
          <div class="w-10 h-10 rounded-full bg-[#E5E0D8] border-2 border-white flex items-center justify-center text-[#5A5A40] font-bold text-sm">
            {{ avatarLetter }}
          </div>
        </div>
      </header>
      <main class="flex-1 overflow-y-auto bg-[#FDFCF8] p-8">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Users, MessageCircle, Camera, CalendarClock, Activity, BookOpenCheck, LogOut } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { logout } from '../../api/auth'
import request from '../../utils/request'

const router = useRouter()
const user      = ref<any>(null)
const className = ref('')

const displayName    = computed(() => user.value?.realName || user.value?.username || '老师')
const avatarLetter   = computed(() => displayName.value.charAt(0))
const sidebarSubtitle = computed(() =>
  className.value ? `${className.value}·${displayName.value}` : displayName.value
)

onMounted(async () => {
  try {
    const [userRes, clsRes]: any[] = await Promise.all([
      request.get('/api/auth/current'),
      request.get('/api/schedules/my-class').catch(() => null),
    ])
    user.value = userRes.data
    if (clsRes?.data) className.value = clsRes.data.className || ''
  } catch { /* ignore */ }
})

const handleLogout = async () => {
  await logout()
  router.push('/login')
}
</script>

<style scoped>
@reference "tailwindcss";
.nav-item {
  @apply flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors text-[#7A7A6A] hover:bg-[#EAE5DD];
}
</style>
