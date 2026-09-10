export interface Department {
  id: number
  name: string
  description: string
  createTime: string
  updateTime: string
}

export interface Worker {
  id: number
  departmentId: number
  departmentName: string
  name: string
  gender: string
  phone: string
  idCard: string
  entryDate: string
  position: string
  baseSalary: number
  status: string
  createTime: string
  updateTime: string
}

export interface Attendance {
  id: number
  workerId: number
  workerName: string
  departmentName: string
  date: string
  status: string
  checkIn: string
  checkOut: string
  remark: string
  createTime: string
}

export interface Salary {
  id: number
  workerId: number
  workerName: string
  departmentName: string
  month: string
  baseSalary: number
  bonus: number
  deduction: number
  total: number
  status: string
  createTime: string
}

export interface User {
  id: number
  username: string
  role: string
  nickname?: string
  avatar?: string
  workerId: number | null
  workerName?: string
  status: string
  createTime: string
}

export interface OperationLog {
  id: number
  userId: number
  username: string
  module: string
  operation: string
  content: string
  createTime: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface PageQuery {
  page: number
  pageSize: number
}
