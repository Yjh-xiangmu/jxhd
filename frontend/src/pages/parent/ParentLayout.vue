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
          <p class="text-[10px] text-[#A09E94] font-medium mt-1">{{ displayName }}</p>
        </div>
      </div>

      <nav class="flex-1 overflow-y-auto space-y-2">
        <p class="text-[10px] uppercase tracking-widest text-[#A09E94] font-bold mb-4">Core Functions</p>
        <router-link to="/parent/timeline"   active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Baby class="w-5 h-5 shrink-0" /> 成长时光机
        </router-link>
        <router-link to="/parent/school"     active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Sun class="w-5 h-5 shrink-0" /> 在园动态查看
        </router-link>
        <router-link to="/parent/home"       active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Home class="w-5 h-5 shrink-0" /> 在家表现补充
        </router-link>
        <router-link to="/parent/messages"   active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <MessageSquare class="w-5 h-5 shrink-0" /> 家校沟通
        </router-link>
        <router-link to="/parent/activities" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Calendar class="w-5 h-5 shrink-0" /> 活动发现
        </router-link>
        <router-link to="/parent/forum"      active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Users class="w-5 h-5 shrink-0" /> 家长互助论坛
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
        <h2 class="text-xl font-serif italic text-[#5A5A40]">家长空间</h2>
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
import { Baby, Sun, Home, MessageSquare, Calendar, Users, LogOut } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { logout } from '../../api/auth'
import request from '../../utils/request'

const router = useRouter()
const user = ref<any>(null)

const displayName = computed(() => user.value?.realName || user.value?.username || '家长')
const avatarLetter = computed(() => displayName.value.charAt(0))

onMounted(async () => {
  try {
    const res: any = await request.get('/api/auth/current')
    user.value = res.data
  } catch { /* session expired */ }
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
