import request from './request'
import type { User } from '@/types'

export interface UserPageResult {
  list: User[]
  total: number
  page: number
  pageSize: number
}

export function getUsers(params: { page: number; pageSize: number }) {
  return request.get<any, { code: number; data: UserPageResult }>('/users', { params })
}

export function createUser(data: Partial<User>) {
  return request.post<any, { code: number; data: User }>('/users', data)
}

export function updateUser(id: number, data: Partial<User>) {
  return request.put<any, { code: number; data: User }>(`/users/${id}`, data)
}

export function resetPassword(id: number) {
  return request.put<any, { code: number; data: User }>(`/users/${id}/reset-password`)
}

export function deleteUser(id: number) {
  return request.delete<any, { code: number }>(`/users/${id}`)
}

export function updateProfile(nickname: string) {
  return request.put<any, { code: number; data: User }>('/users/profile', { nickname })
}

export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, { code: number; data: { avatar: string } }>('/users/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
