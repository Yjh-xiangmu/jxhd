<template>
  <div class="flex h-screen bg-[#FDFCF8] text-[#333322] overflow-hidden font-sans">
    <!-- 侧边栏 -->
    <aside class="w-64 bg-[#F5F2ED] border-r border-[#EFE9E1] flex flex-col p-6 gap-6 shrink-0">
      <div class="flex items-center gap-3 mb-2">
        <div class="w-10 h-10 bg-[#5A5A40] rounded-xl flex items-center justify-center shrink-0">
          <div class="w-5 h-5 border-2 border-white rounded-full"></div>
        </div>
        <div>
          <h1 class="text-xl font-bold tracking-tight text-[#5A5A40] uppercase leading-none">
            Smart<span class="font-light opacity-60">Kiddy</span>
          </h1>
          <p class="text-[10px] text-[#A09E94] font-medium mt-1">管理员</p>
        </div>
      </div>

      <nav class="flex-1 overflow-y-auto space-y-2">
        <p class="text-[10px] uppercase tracking-widest text-[#A09E94] font-bold mb-4">Core Functions</p>
        <router-link to="/admin/dashboard" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <LayoutDashboard class="w-5 h-5 shrink-0" /> 总览数据
        </router-link>
        <router-link to="/admin/users" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Users class="w-5 h-5 shrink-0" /> 用户与权限
        </router-link>
        <router-link to="/admin/classes" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <School class="w-5 h-5 shrink-0" /> 基础班级数据
        </router-link>
        <router-link to="/admin/schedule" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <CalendarDays class="w-5 h-5 shrink-0" /> 课表管理
        </router-link>
        <router-link to="/admin/news" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <Newspaper class="w-5 h-5 shrink-0" /> 校园资讯
        </router-link>
        <router-link to="/admin/forum" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <MessageSquareWarning class="w-5 h-5 shrink-0" /> 论坛监管
        </router-link>
        <router-link to="/admin/logs" active-class="bg-[#5A5A40] text-white shadow-md" class="nav-item">
          <ShieldAlert class="w-5 h-5 shrink-0" /> 系统操作日志
        </router-link>
      </nav>

      <div class="mt-auto">
        <button @click="handleLogout" class="w-full flex items-center gap-3 px-3 py-2 text-[#7A7A6A] hover:bg-[#EAE5DD] rounded-lg text-sm font-medium transition-colors">
          <LogOut class="w-5 h-5 shrink-0" /> 退出系统
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <header class="h-16 bg-white border-b border-[#EFE9E1] flex items-center justify-between px-8 shrink-0 shadow-sm">
        <h2 class="text-xl font-serif italic text-[#5A5A40]">管理后台</h2>
        <div class="flex items-center gap-3 border-l pl-6 border-[#EFE9E1]">
          <p class="text-xs font-bold leading-none text-[#5A5A40]">系统管理员</p>
          <div class="w-10 h-10 rounded-full bg-[#E5E0D8] border-2 border-white flex items-center justify-center text-[#A09E94] font-bold">A</div>
        </div>
      </header>
      <!-- 子路由出口 -->
      <main class="flex-1 overflow-y-auto bg-[#FDFCF8] p-8">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { LayoutDashboard, Users, School, Newspaper, MessageSquareWarning, ShieldAlert, LogOut, CalendarDays } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { logout } from '../../api/auth'

const router = useRouter()
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
