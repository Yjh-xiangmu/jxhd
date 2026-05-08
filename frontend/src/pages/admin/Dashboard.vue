<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">总览数据</h3>
      <p class="text-sm text-[#A09E94] mt-1">欢迎回来，系统管理员</p>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div v-for="card in statCards" :key="card.title"
        class="bg-white rounded-2xl p-6 border border-[#EFE9E1] shadow-sm">
        <p class="text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">{{ card.title }}</p>
        <p class="text-3xl font-bold text-[#5A5A40] mt-3">{{ card.value }}</p>
        <p class="text-xs text-[#A09E94] mt-1">{{ card.sub }}</p>
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
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const statCards = [
  { title: '教师账号', value: '-', sub: '点击用户管理查看' },
  { title: '家长账号', value: '-', sub: '点击用户管理查看' },
  { title: '班级总数', value: '-', sub: '点击班级管理查看' },
  { title: '在园幼儿', value: '-', sub: '点击班级管理查看' },
]
</script>
