import request from './request'
import type { OperationLog } from '@/types'

export interface LogPageResult {
  list: OperationLog[]
  total: number
  page: number
  pageSize: number
}

export function getLogs(params: { page: number; pageSize: number }) {
  return request.get<any, { code: number; data: LogPageResult }>('/logs', { params })
}
