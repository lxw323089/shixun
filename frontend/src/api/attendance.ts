import request from './request'
import type { Attendance } from '@/types'

export interface AttendancePageResult {
  list: Attendance[]
  total: number
  page: number
  pageSize: number
}

export function getAttendances(params: { page: number; pageSize: number; workerName?: string; date?: string; status?: string }) {
  return request.get<any, { code: number; data: AttendancePageResult }>('/attendances', { params })
}

export function createAttendance(data: Partial<Attendance>) {
  return request.post<any, { code: number; data: Attendance }>('/attendances', data)
}

export function updateAttendance(id: number, data: Partial<Attendance>) {
  return request.put<any, { code: number; data: Attendance }>(`/attendances/${id}`, data)
}

export function deleteAttendance(id: number) {
  return request.delete<any, { code: number }>(`/attendances/${id}`)
}
