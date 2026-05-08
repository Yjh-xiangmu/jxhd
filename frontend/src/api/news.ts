import request from '../utils/request'

// ── 校园资讯 ──────────────────────────────────────
export interface NewsForm {
  id?: number
  title: string
  type: string
  content?: string
  status?: number
}

export const getNewsList = (params: {
  pageNum?: number; pageSize?: number
  type?: string; keyword?: string; status?: number
}) => request.get('/api/news', { params })

export const addNews    = (data: NewsForm)          => request.post('/api/news', data)
export const updateNews = (id: number, data: NewsForm) => request.put(`/api/news/${id}`, data)
export const updateNewsStatus = (id: number, status: number) =>
  request.put(`/api/news/${id}/status`, { status })
export const deleteNews = (id: number) => request.delete(`/api/news/${id}`)

// ── 亲子活动 ──────────────────────────────────────
export interface ActivityForm {
  id?: number
  title: string
  description?: string
  location?: string
  activityTime?: string
  signupDeadline?: string
  maxCount?: number | null
  status?: number
}

export const getActivityList = (params?: { status?: number }) =>
  request.get('/api/activities', { params })

export const addActivity    = (data: ActivityForm)             => request.post('/api/activities', data)
export const updateActivity = (id: number, data: ActivityForm) => request.put(`/api/activities/${id}`, data)
export const updateActivityStatus = (id: number, status: number) =>
  request.put(`/api/activities/${id}/status`, { status })
export const deleteActivity = (id: number) => request.delete(`/api/activities/${id}`)
export const getActivitySignups = (id: number) => request.get(`/api/activities/${id}/signups`)

export const signupActivity = (id: number | string, studentId: number | string, remark?: string) =>
  request.post(`/api/activities/${id}/signup`, { studentId, remark: remark ?? '' })

export const cancelSignup = (signupId: number | string) =>
  request.delete(`/api/activities/signup/${signupId}`)

export const getMySignups = () => request.get('/api/activities/my-signups')
