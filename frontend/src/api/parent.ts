import request from '../utils/request'

export const getMyChildren = () =>
  request.get('/api/parent/my-children')

export const getHomeRecords = (studentId: number | string) =>
  request.get(`/api/parent/home-records/${studentId}`)

export const addHomeRecord = (data: { studentId: number | string; content: string; recordDate?: string }) =>
  request.post('/api/parent/home-records', data)

export const deleteHomeRecord = (id: number | string) =>
  request.delete(`/api/parent/home-records/${id}`)
