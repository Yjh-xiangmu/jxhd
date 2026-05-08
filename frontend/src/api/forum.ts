import request from '../utils/request'

export const getForumPosts = (params?: { pageNum?: number; pageSize?: number; keyword?: string }) =>
  request.get('/api/forum/posts', { params })

export const getPostDetail = (id: number | string) =>
  request.get(`/api/forum/posts/${id}`)

export const addPost = (data: { title: string; content: string }) =>
  request.post('/api/forum/posts', data)

export const deletePost = (id: number | string) =>
  request.delete(`/api/forum/posts/${id}`)

export const togglePin = (id: number | string) =>
  request.put(`/api/forum/posts/${id}/pin`, {})

export const addReply = (postId: number | string, content: string) =>
  request.post(`/api/forum/posts/${postId}/replies`, { content })

export const deleteReply = (id: number | string) =>
  request.delete(`/api/forum/replies/${id}`)
