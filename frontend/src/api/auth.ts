import request from './request'
import type { User } from '@/types'

export interface LoginResult {
  token: string
  user: User
}

export function login(data: { username: string; password: string }) {
  return request.post<any, { code: number; message: string; data: LoginResult }>('/auth/login', data)
}

export function logout() {
  return request.post<any, { code: number; message: string }>('/auth/logout')
}
