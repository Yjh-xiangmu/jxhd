import request from '../utils/request'

export const uploadImage = (file: File) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/api/growth/upload-image', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export const getGrowthRecords = (params: { pageNum?: number; pageSize?: number; classId?: number }) =>
  request.get('/api/growth', { params })

export const getMyChildrenRecords = () =>
  request.get('/api/growth/my-children')

export const getMyChildRecords = (studentId: number | string) =>
  request.get(`/api/growth/my-child/${studentId}`)

export const addGrowthRecord = (data: { studentId: number | string; content: string; images: string[] }) =>
  request.post('/api/growth', data)

export const deleteGrowthRecord = (id: number | string) =>
  request.delete(`/api/growth/${id}`)
