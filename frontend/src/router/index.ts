import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    {
      path: '/login',
      component: () => import('../pages/auth/Login.vue')
    },

    // ── 管理员 ──────────────────────────────────────
    {
      path: '/admin',
      component: () => import('../pages/admin/AdminLayout.vue'),
      children: [
        { path: '',        redirect: '/admin/dashboard' },
        { path: 'dashboard', component: () => import('../pages/admin/Dashboard.vue') },
        { path: 'users',     component: () => import('../pages/admin/Users.vue') },
        { path: 'classes',   component: () => import('../pages/admin/Classes.vue') },
        { path: 'schedule',  component: () => import('../pages/admin/Schedule.vue') },
        { path: 'news',      component: () => import('../pages/admin/News.vue') },
        { path: 'forum',     component: () => import('../pages/admin/Forum.vue') },
        { path: 'logs',      component: () => import('../pages/admin/Logs.vue') },
      ]
    },

    // ── 教师 ────────────────────────────────────────
    {
      path: '/teacher',
      component: () => import('../pages/teacher/TeacherLayout.vue'),
      children: [
        { path: '',          redirect: '/teacher/students' },
        { path: 'students',  component: () => import('../pages/teacher/Students.vue') },
        { path: 'messages',  component: () => import('../pages/teacher/Messages.vue') },
        { path: 'growth',    component: () => import('../pages/teacher/Growth.vue') },
        { path: 'schedule',  component: () => import('../pages/teacher/Schedule.vue') },
        { path: 'activities',component: () => import('../pages/teacher/Activities.vue') },
        { path: 'forum',     component: () => import('../pages/teacher/Forum.vue') },
      ]
    },

    // ── 家长 ────────────────────────────────────────
    {
      path: '/parent',
      component: () => import('../pages/parent/ParentLayout.vue'),
      children: [
        { path: '',          redirect: '/parent/timeline' },
        { path: 'timeline',  component: () => import('../pages/parent/Timeline.vue') },
        { path: 'school',    component: () => import('../pages/parent/School.vue') },
        { path: 'home',      component: () => import('../pages/parent/Home.vue') },
        { path: 'messages',  component: () => import('../pages/parent/Messages.vue') },
        { path: 'activities',component: () => import('../pages/parent/Activities.vue') },
        { path: 'forum',     component: () => import('../pages/parent/Forum.vue') },
      ]
    },
  ]
})

export default router
