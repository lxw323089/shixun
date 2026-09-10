import request from './request'
import type { Salary } from '@/types'

export interface SalaryPageResult {
  list: Salary[]
  total: number
  page: number
  pageSize: number
}

export function getSalaries(params: { page: number; pageSize: number; workerName?: string; month?: string; status?: string }) {
  return request.get<any, { code: number; data: SalaryPageResult }>('/salaries', { params })
}

export function createSalary(data: Partial<Salary>) {
  return request.post<any, { code: number; data: Salary }>('/salaries', data)
}

export function updateSalary(id: number, data: Partial<Salary>) {
  return request.put<any, { code: number; data: Salary }>(`/salaries/${id}`, data)
}

export function paySalary(id: number) {
  return request.put<any, { code: number; data: Salary }>(`/salaries/${id}/pay`)
}

export function deleteSalary(id: number) {
  return request.delete<any, { code: number }>(`/salaries/${id}`)
}
