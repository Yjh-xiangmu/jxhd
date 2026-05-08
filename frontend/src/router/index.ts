import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', component: () => import('../pages/auth/Login.vue') },
        { path: '/admin', component: () => import('../pages/admin/AdminLayout.vue') },
        { path: '/teacher', component: () => import('../pages/teacher/TeacherLayout.vue') },
        { path: '/parent', component: () => import('../pages/parent/ParentLayout.vue') }
    ]
})

export default router