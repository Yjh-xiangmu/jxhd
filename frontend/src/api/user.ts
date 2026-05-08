import request from '../utils/request'

export interface UserQuery {
  pageNum?: number
  pageSize?: number
  role?: string
  keyword?: string
}

export interface UserForm {
  id?: number
  username?: string
  password?: string
  role: string
  realName?: string
  phone?: string
  status?: number
}

// 分页查询
export const getUserList = (params: UserQuery) =>
  request.get('/api/users', { params })

// 新增用户
export const addUser = (data: UserForm) =>
  request.post('/api/users', data)

// 修改用户
export const updateUser = (id: number, data: UserForm) =>
  request.put(`/api/users/${id}`, data)

// 启用/禁用
export const updateUserStatus = (id: number, status: number) =>
  request.put(`/api/users/${id}/status`, { status })

// 重置密码
export const resetPassword = (id: number) =>
  request.put(`/api/users/${id}/reset-password`)

// 删除用户
export const deleteUser = (id: number) =>
  request.delete(`/api/users/${id}`)
