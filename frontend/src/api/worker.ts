import request from './request'
import type { Worker } from '@/types'

export interface WorkerPageResult {
  list: Worker[]
  total: number
  page: number
  pageSize: number
}

export function getWorkers(params: { page: number; pageSize: number; name?: string; departmentId?: number; status?: string }) {
  return request.get<any, { code: number; data: WorkerPageResult }>('/workers', { params })
}

export function getAllWorkers() {
  return request.get<any, { code: number; data: Worker[] }>('/workers/all')
}

export function createWorker(data: Partial<Worker>) {
  return request.post<any, { code: number; data: Worker }>('/workers', data)
}

export function updateWorker(id: number, data: Partial<Worker>) {
  return request.put<any, { code: number; data: Worker }>(`/workers/${id}`, data)
}

export function deleteWorker(id: number) {
  return request.delete<any, { code: number }>(`/workers/${id}`)
}
