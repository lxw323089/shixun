import request from './request'
import type { Worker, OperationLog } from '@/types'

export function getDashboardStats() {
  return request.get<any, { code: number; data: { departmentCount: number; workerCount: number; attendanceRate: string; totalSalary: number } }>('/dashboard/stats')
}

export function getRecentWorkers() {
  return request.get<any, { code: number; data: Worker[] }>('/dashboard/recent-workers')
}

export function getRecentLogs() {
  return request.get<any, { code: number; data: OperationLog[] }>('/dashboard/recent-logs')
}
