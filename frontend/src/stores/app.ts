import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, Department, Worker, Attendance, Salary, OperationLog } from '@/types'
import * as authApi from '@/api/auth'
import * as departmentApi from '@/api/department'
import * as workerApi from '@/api/worker'
import * as attendanceApi from '@/api/attendance'
import * as salaryApi from '@/api/salary'
import * as userApi from '@/api/user'
import * as logApi from '@/api/log'

export const useAppStore = defineStore('app', () => {
  const savedUser = localStorage.getItem('currentUser')
  const currentUser = ref<User | null>(savedUser ? JSON.parse(savedUser) : null)
  const token = ref<string | null>(localStorage.getItem('token'))

  const departments = ref<Department[]>([])
  const workers = ref<Worker[]>([])
  const attendances = ref<Attendance[]>([])
  const salaries = ref<Salary[]>([])
  const users = ref<User[]>([])
  const operationLogs = ref<OperationLog[]>([])
  const sidebarCollapsed = ref(false)

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 登录
  async function login(username: string, password: string): Promise<boolean> {
    try {
      const res = await authApi.login({ username, password })
      if (res.code === 200) {
        token.value = res.data.token
        currentUser.value = res.data.user
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('currentUser', JSON.stringify(res.data.user))
        return true
      }
      return false
    } catch (e) {
      return false
    }
  }

  function logout() {
    currentUser.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('currentUser')
  }

  function updateCurrentUser(user: Partial<User>) {
    if (currentUser.value) {
      currentUser.value = { ...currentUser.value, ...user }
      localStorage.setItem('currentUser', JSON.stringify(currentUser.value))
    }
  }

  // 部门
  async function loadDepartments() {
    const res = await departmentApi.getDepartments()
    departments.value = res.data
  }

  async function addDepartment(dept: Partial<Department>) {
    await departmentApi.createDepartment(dept)
    await loadDepartments()
  }

  async function updateDepartment(id: number, data: Partial<Department>) {
    await departmentApi.updateDepartment(id, data)
    await loadDepartments()
  }

  async function deleteDepartment(id: number) {
    await departmentApi.deleteDepartment(id)
    await loadDepartments()
  }

  // 员工
  async function loadWorkers() {
    const res = await workerApi.getAllWorkers()
    workers.value = res.data
  }

  async function addWorker(worker: Partial<Worker>) {
    await workerApi.createWorker(worker)
    await loadWorkers()
  }

  async function updateWorker(id: number, data: Partial<Worker>) {
    await workerApi.updateWorker(id, data)
    await loadWorkers()
  }

  async function deleteWorker(id: number) {
    await workerApi.deleteWorker(id)
    await loadWorkers()
  }

  // 考勤
  async function loadAttendances() {
    const res = await attendanceApi.getAttendances({ page: 1, pageSize: 1000 })
    attendances.value = res.data.list
  }

  async function addAttendance(att: Partial<Attendance>) {
    await attendanceApi.createAttendance(att)
    await loadAttendances()
  }

  async function updateAttendance(id: number, data: Partial<Attendance>) {
    await attendanceApi.updateAttendance(id, data)
    await loadAttendances()
  }

  async function deleteAttendance(id: number) {
    await attendanceApi.deleteAttendance(id)
    await loadAttendances()
  }

  // 工资
  async function loadSalaries() {
    const res = await salaryApi.getSalaries({ page: 1, pageSize: 1000 })
    salaries.value = res.data.list
  }

  async function addSalary(sal: Partial<Salary>) {
    await salaryApi.createSalary(sal)
    await loadSalaries()
  }

  async function updateSalary(id: number, data: Partial<Salary>) {
    await salaryApi.updateSalary(id, data)
    await loadSalaries()
  }

  async function paySalary(id: number) {
    await salaryApi.paySalary(id)
    await loadSalaries()
  }

  async function deleteSalary(id: number) {
    await salaryApi.deleteSalary(id)
    await loadSalaries()
  }

  // 用户
  async function loadUsers() {
    const res = await userApi.getUsers({ page: 1, pageSize: 1000 })
    users.value = res.data.list
  }

  async function addUser(user: Partial<User>) {
    await userApi.createUser(user)
    await loadUsers()
  }

  async function updateUser(id: number, data: Partial<User>) {
    await userApi.updateUser(id, data)
    await loadUsers()
  }

  async function deleteUser(id: number) {
    await userApi.deleteUser(id)
    await loadUsers()
  }

  // 日志
  async function loadLogs() {
    const res = await logApi.getLogs({ page: 1, pageSize: 1000 })
    operationLogs.value = res.data.list
  }

  // 初始化所有数据
  async function initData() {
    await Promise.all([
      loadDepartments(),
      loadWorkers(),
      loadAttendances(),
      loadSalaries(),
      loadUsers(),
      loadLogs(),
    ])
  }

  return {
    currentUser,
    token,
    departments,
    workers,
    attendances,
    salaries,
    users,
    operationLogs,
    sidebarCollapsed,
    toggleSidebar,
    login,
    logout,
    updateCurrentUser,
    initData,
    loadDepartments,
    loadWorkers,
    loadAttendances,
    loadSalaries,
    loadUsers,
    loadLogs,
    addDepartment,
    updateDepartment,
    deleteDepartment,
    addWorker,
    updateWorker,
    deleteWorker,
    addAttendance,
    updateAttendance,
    deleteAttendance,
    addSalary,
    updateSalary,
    paySalary,
    deleteSalary,
    addUser,
    updateUser,
    deleteUser,
  }
})
