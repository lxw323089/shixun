import type { Department, Worker, Attendance, Salary, User, OperationLog } from '@/types'

export const departments: Department[] = [
  { id: 1, name: '生产部', description: '负责产品生产制造', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 2, name: '技术部', description: '负责技术研发和支持', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 3, name: '财务部', description: '负责财务核算和管理', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 4, name: '人事部', description: '负责人员招聘和管理', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 5, name: '质检部', description: '负责产品质量检验', createTime: '2024-01-15 10:00:00', updateTime: '2024-01-15 10:00:00' },
]

export const workers: Worker[] = [
  { id: 1, departmentId: 1, departmentName: '生产部', name: '张三', gender: '男', phone: '13800138001', idCard: '110101199001011234', entryDate: '2023-03-15', position: '生产组长', baseSalary: 6500, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 2, departmentId: 1, departmentName: '生产部', name: '李四', gender: '男', phone: '13800138002', idCard: '110101199102022345', entryDate: '2023-04-20', position: '操作工', baseSalary: 5000, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 3, departmentId: 2, departmentName: '技术部', name: '王五', gender: '男', phone: '13800138003', idCard: '110101198903033456', entryDate: '2022-06-10', position: '技术工程师', baseSalary: 8000, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 4, departmentId: 2, departmentName: '技术部', name: '赵六', gender: '女', phone: '13800138004', idCard: '110101199204044567', entryDate: '2023-08-01', position: '技术员', baseSalary: 6000, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 5, departmentId: 3, departmentName: '财务部', name: '钱七', gender: '女', phone: '13800138005', idCard: '110101198805055678', entryDate: '2021-09-15', position: '财务主管', baseSalary: 7500, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 6, departmentId: 3, departmentName: '财务部', name: '孙八', gender: '女', phone: '13800138006', idCard: '110101199306066789', entryDate: '2023-02-28', position: '会计', baseSalary: 5500, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 7, departmentId: 4, departmentName: '人事部', name: '周九', gender: '男', phone: '13800138007', idCard: '110101199007077890', entryDate: '2022-11-20', position: '人事专员', baseSalary: 5200, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 8, departmentId: 5, departmentName: '质检部', name: '吴十', gender: '男', phone: '13800138008', idCard: '110101199108088901', entryDate: '2023-05-10', position: '质检员', baseSalary: 4800, status: '在职', createTime: '2024-01-01 09:00:00', updateTime: '2024-01-01 09:00:00' },
  { id: 9, departmentId: 1, departmentName: '生产部', name: '郑十一', gender: '男', phone: '13800138009', idCard: '110101199409099012', entryDate: '2024-01-10', position: '操作工', baseSalary: 5000, status: '在职', createTime: '2024-01-10 09:00:00', updateTime: '2024-01-10 09:00:00' },
  { id: 10, departmentId: 2, departmentName: '技术部', name: '冯十二', gender: '女', phone: '13800138010', idCard: '110101199510100123', entryDate: '2024-02-01', position: '技术员', baseSalary: 5800, status: '离职', createTime: '2024-02-01 09:00:00', updateTime: '2024-06-15 09:00:00' },
]

export const attendances: Attendance[] = [
  { id: 1, workerId: 1, workerName: '张三', departmentName: '生产部', date: '2024-09-01', status: '正常', checkIn: '08:00:00', checkOut: '17:30:00', remark: '', createTime: '2024-09-01 17:30:00' },
  { id: 2, workerId: 2, workerName: '李四', departmentName: '生产部', date: '2024-09-01', status: '迟到', checkIn: '08:35:00', checkOut: '17:30:00', remark: '地铁延误', createTime: '2024-09-01 17:30:00' },
  { id: 3, workerId: 3, workerName: '王五', departmentName: '技术部', date: '2024-09-01', status: '正常', checkIn: '07:55:00', checkOut: '18:00:00', remark: '加班半小时', createTime: '2024-09-01 18:00:00' },
  { id: 4, workerId: 4, workerName: '赵六', departmentName: '技术部', date: '2024-09-01', status: '正常', checkIn: '08:05:00', checkOut: '17:35:00', remark: '', createTime: '2024-09-01 17:35:00' },
  { id: 5, workerId: 5, workerName: '钱七', departmentName: '财务部', date: '2024-09-01', status: '正常', checkIn: '08:00:00', checkOut: '17:30:00', remark: '', createTime: '2024-09-01 17:30:00' },
  { id: 6, workerId: 1, workerName: '张三', departmentName: '生产部', date: '2024-09-02', status: '正常', checkIn: '07:58:00', checkOut: '17:32:00', remark: '', createTime: '2024-09-02 17:32:00' },
  { id: 7, workerId: 2, workerName: '李四', departmentName: '生产部', date: '2024-09-02', status: '缺勤', checkIn: '', checkOut: '', remark: '事假', createTime: '2024-09-02 09:00:00' },
  { id: 8, workerId: 6, workerName: '孙八', departmentName: '财务部', date: '2024-09-01', status: '早退', checkIn: '08:00:00', checkOut: '16:45:00', remark: '家中有事', createTime: '2024-09-01 16:45:00' },
  { id: 9, workerId: 7, workerName: '周九', departmentName: '人事部', date: '2024-09-01', status: '正常', checkIn: '07:50:00', checkOut: '17:40:00', remark: '', createTime: '2024-09-01 17:40:00' },
  { id: 10, workerId: 8, workerName: '吴十', departmentName: '质检部', date: '2024-09-01', status: '正常', checkIn: '08:02:00', checkOut: '17:30:00', remark: '', createTime: '2024-09-01 17:30:00' },
]

export const salaries: Salary[] = [
  { id: 1, workerId: 1, workerName: '张三', departmentName: '生产部', month: '2024-08', baseSalary: 6500, bonus: 800, deduction: 200, total: 7100, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 2, workerId: 2, workerName: '李四', departmentName: '生产部', month: '2024-08', baseSalary: 5000, bonus: 300, deduction: 150, total: 5150, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 3, workerId: 3, workerName: '王五', departmentName: '技术部', month: '2024-08', baseSalary: 8000, bonus: 1200, deduction: 300, total: 8900, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 4, workerId: 4, workerName: '赵六', departmentName: '技术部', month: '2024-08', baseSalary: 6000, bonus: 500, deduction: 100, total: 6400, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 5, workerId: 5, workerName: '钱七', departmentName: '财务部', month: '2024-08', baseSalary: 7500, bonus: 1000, deduction: 250, total: 8250, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 6, workerId: 6, workerName: '孙八', departmentName: '财务部', month: '2024-08', baseSalary: 5500, bonus: 400, deduction: 180, total: 5720, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 7, workerId: 7, workerName: '周九', departmentName: '人事部', month: '2024-08', baseSalary: 5200, bonus: 350, deduction: 120, total: 5430, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 8, workerId: 8, workerName: '吴十', departmentName: '质检部', month: '2024-08', baseSalary: 4800, bonus: 200, deduction: 80, total: 4920, status: '已发放', createTime: '2024-08-31 10:00:00' },
  { id: 9, workerId: 1, workerName: '张三', departmentName: '生产部', month: '2024-09', baseSalary: 6500, bonus: 0, deduction: 0, total: 6500, status: '待发放', createTime: '2024-09-05 10:00:00' },
  { id: 10, workerId: 2, workerName: '李四', departmentName: '生产部', month: '2024-09', baseSalary: 5000, bonus: 0, deduction: 0, total: 5000, status: '待发放', createTime: '2024-09-05 10:00:00' },
]

export const users: User[] = [
  { id: 1, username: 'admin', role: 'admin', workerId: null, status: '启用', createTime: '2024-01-01 09:00:00' },
  { id: 2, username: 'zhangsan', role: 'worker', workerId: 1, workerName: '张三', status: '启用', createTime: '2024-01-01 09:00:00' },
  { id: 3, username: 'lisi', role: 'worker', workerId: 2, workerName: '李四', status: '启用', createTime: '2024-01-01 09:00:00' },
  { id: 4, username: 'wangwu', role: 'worker', workerId: 3, workerName: '王五', status: '启用', createTime: '2024-01-01 09:00:00' },
  { id: 5, username: 'zhaoliu', role: 'worker', workerId: 4, workerName: '赵六', status: '启用', createTime: '2024-01-01 09:00:00' },
  { id: 6, username: 'qianqi', role: 'worker', workerId: 5, workerName: '钱七', status: '启用', createTime: '2024-01-01 09:00:00' },
]

export const operationLogs: OperationLog[] = [
  { id: 1, userId: 1, username: 'admin', module: '部门管理', operation: '新增', content: '新增部门：质检部', createTime: '2024-09-01 09:30:00' },
  { id: 2, userId: 1, username: 'admin', module: '员工管理', operation: '新增', content: '新增员工：郑十一', createTime: '2024-09-01 10:15:00' },
  { id: 3, userId: 1, username: 'admin', module: '员工管理', operation: '修改', content: '修改员工信息：张三', createTime: '2024-09-01 11:00:00' },
  { id: 4, userId: 1, username: 'admin', module: '考勤管理', operation: '录入', content: '录入9月1日考勤数据', createTime: '2024-09-01 18:00:00' },
  { id: 5, userId: 1, username: 'admin', module: '工资管理', operation: '核算', content: '核算8月工资', createTime: '2024-08-31 14:00:00' },
  { id: 6, userId: 1, username: 'admin', module: '工资管理', operation: '发放', content: '发放8月工资', createTime: '2024-08-31 16:00:00' },
  { id: 7, userId: 1, username: 'admin', module: '系统用户', operation: '新增', content: '新增用户：zhangsan', createTime: '2024-01-01 09:00:00' },
  { id: 8, userId: 1, username: 'admin', module: '系统用户', operation: '权限分配', content: '分配管理员权限给admin', createTime: '2024-01-01 09:05:00' },
  { id: 9, userId: 1, username: 'admin', module: '员工管理', operation: '删除', content: '删除员工：冯十二', createTime: '2024-06-15 09:30:00' },
  { id: 10, userId: 1, username: 'admin', module: '部门管理', operation: '修改', content: '修改部门描述：技术部', createTime: '2024-07-10 10:20:00' },
]
