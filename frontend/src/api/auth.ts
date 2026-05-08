import request from '../utils/request'

export interface LoginParams {
  username: string
  password: string
}

export const login = (data: LoginParams) =>
  request.post('/api/auth/login', data)

export const logout = () =>
  request.post('/api/auth/logout')

export const getCurrentUser = () =>
  request.get('/api/auth/current')
