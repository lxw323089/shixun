import request from './request'
import type { Department } from '@/types'

export function getDepartments() {
  return request.get<any, { code: number; data: Department[] }>('/departments')
}

export function createDepartment(data: Partial<Department>) {
  return request.post<any, { code: number; data: Department }>('/departments', data)
}

export function updateDepartment(id: number, data: Partial<Department>) {
  return request.put<any, { code: number; data: Department }>(`/departments/${id}`, data)
}

export function deleteDepartment(id: number) {
  return request.delete<any, { code: number }>(`/departments/${id}`)
}
