<template>
  <div class="min-h-screen bg-[#FDFCF8] flex flex-col items-center justify-center p-6 font-sans">

    <div class="text-center max-w-4xl text-[#333322] mb-10 transition-all">
      <h1 class="text-3xl md:text-4xl font-extrabold tracking-tight mb-4 text-[#5A5A40] leading-snug">
        老河乡幼儿园<br class="md:hidden">
        <span class="opacity-90 font-semibold">基于协同教育的家校互动平台</span>
      </h1>
      <p class="text-lg text-[#7A7A6A] tracking-wide">
        协同共育 · 记录成长 · 开启智慧家园新篇章
      </p>
    </div>

    <div class="bg-white p-10 rounded-[32px] border border-[#EFE9E1] shadow-sm hover:shadow-md transition-shadow w-full max-w-md relative">
      <h2 class="text-2xl font-bold text-[#5A5A40] mb-8 text-center">系统登录</h2>

      <div class="space-y-6">
        <div>
          <label class="block text-sm font-bold text-[#7A7A6A] mb-2 pl-1">账号</label>
          <input
              v-model="form.username"
              type="text"
              placeholder="请输入账号"
              class="w-full h-14 px-5 bg-[#F5F2ED] rounded-2xl border border-[#EFE9E1] focus:border-[#5A5A40] focus:bg-white outline-none transition-colors text-gray-800 placeholder-gray-400"
          >
        </div>

        <div>
          <label class="block text-sm font-bold text-[#7A7A6A] mb-2 pl-1">密码</label>
          <input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              @keyup.enter="handleLogin"
              class="w-full h-14 px-5 bg-[#F5F2ED] rounded-2xl border border-[#EFE9E1] focus:border-[#5A5A40] focus:bg-white outline-none transition-colors text-gray-800 placeholder-gray-400"
          >
        </div>

        <p v-if="errorMessage" class="text-red-500 text-sm pl-1">{{ errorMessage }}</p>

        <button
            @click="handleLogin"
            class="w-full h-14 bg-[#5A5A40] text-white rounded-2xl mt-4 hover:bg-[#4A4A35] transition-all duration-300 font-bold text-lg tracking-widest flex items-center justify-center group"
        >
          进 入 系 统
          <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 ml-2 group-hover:translate-x-2 transition-transform duration-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14"/><path d="m12 5 7 7-7 7"/>
          </svg>
        </button>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const errorMessage = ref('')

const handleLogin = async () => {
  if (!form.username || !form.password) {
    errorMessage.value = '账号和密码不能为空'
    return
  }

  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', form)
    if (res.data.code === 200) {
      const user = res.data.data
      if (user.role === 'admin') router.push('/admin')
      else if (user.role === 'teacher') router.push('/teacher')
      else if (user.role === 'parent') router.push('/parent')
    } else {
      errorMessage.value = res.data.msg
    }
  } catch (error) {
    errorMessage.value = '网络请求失败，请检查后端服务是否启动'
  }
}
</script>