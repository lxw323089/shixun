<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import type { Attendance } from '@/types'

const store = useAppStore()

onMounted(() => {
  store.loadAttendances()
  store.loadWorkers()
  store.loadDepartments()
})

const searchForm = ref({
  dateRange: [] as string[],
  departmentId: null as number | null,
  workerName: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()

const form = ref<Partial<Attendance>>({
  workerId: undefined,
  date: '',
  status: '正常',
  checkIn: '',
  checkOut: '',
  remark: ''
})

const rules: FormRules = {
  workerId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择考勤状态', trigger: 'change' }]
}

const filteredList = computed(() => {
  return store.attendances.filter(a => {
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      if (a.date < searchForm.value.dateRange[0] || a.date > searchForm.value.dateRange[1]) {
        return false
      }
    }
    if (searchForm.value.departmentId) {
      const worker = store.workers.find(w => w.id === a.workerId)
      if (!worker || worker.departmentId !== searchForm.value.departmentId) return false
    }
    if (searchForm.value.workerName && !a.workerName.includes(searchForm.value.workerName)) {
      return false
    }
    if (searchForm.value.status && a.status !== searchForm.value.status) {
      return false
    }
    return true
  })
})

const currentPage = ref(1)
const pageSize = ref(10)

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

const stats = computed(() => {
  const list = filteredList.value
  const total = list.length
  const normal = list.filter(a => a.status === '正常').length
  const late = list.filter(a => a.status === '迟到').length
  const early = list.filter(a => a.status === '早退').length
  const absent = list.filter(a => a.status === '缺勤').length
  const rate = total > 0 ? ((normal / total) * 100).toFixed(1) + '%' : '-'
  return { total, normal, late, early, absent, rate }
})

function handleSearch() {
  currentPage.value = 1
}

function handleReset() {
  searchForm.value = { dateRange: [], departmentId: null, workerName: '', status: '' }
  currentPage.value = 1
}

function handleAdd() {
  dialogTitle.value = '录入考勤'
  form.value = {
    workerId: undefined,
    date: '',
    status: '正常',
    checkIn: '',
    checkOut: '',
    remark: ''
  }
  dialogVisible.value = true
}

function handleEdit(row: Attendance) {
  dialogTitle.value = '编辑考勤'
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleDelete(row: Attendance) {
  try {
    await ElMessageBox.confirm(`确定要删除${row.workerName}的考勤记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await store.deleteAttendance(row.id)
    ElMessage.success('删除成功')
  } catch (e) {}
}

async function handleSubmit() {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      const worker = store.workers.find(w => w.id === form.value.workerId)
      const data = {
        ...form.value,
        workerName: worker?.name || '',
        departmentName: worker?.departmentName || ''
      }
      if (form.value.id) {
        await store.updateAttendance(form.value.id, data)
        ElMessage.success('修改成功')
      } else {
        await store.addAttendance(data as Omit<Attendance, 'id' | 'createTime'>)
        ElMessage.success('录入成功')
      }
      dialogVisible.value = false
    }
  } catch (e) {
    console.log('表单验证未通过')
  }
}

function getStatusType(status: string) {
  const map: Record<string, string> = {
    '正常': 'success',
    '迟到': 'warning',
    '早退': 'warning',
    '缺勤': 'danger',
    '请假': 'info'
  }
  return map[status] || 'info'
}
</script>

<template>
  <div class="attendance-page">
    <div class="page-header">
      <h2 class="page-title">考勤管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">录入考勤</el-button>
    </div>

    <div class="stats-row">
      <div class="stat-item">
        <p class="stat-label">总记录数</p>
        <p class="stat-value">{{ stats.total }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">正常</p>
        <p class="stat-value text-success">{{ stats.normal }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">迟到</p>
        <p class="stat-value text-warning">{{ stats.late }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">早退</p>
        <p class="stat-value text-warning">{{ stats.early }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">缺勤</p>
        <p class="stat-value text-danger">{{ stats.absent }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">出勤率</p>
        <p class="stat-value text-primary">{{ stats.rate }}</p>
      </div>
    </div>

    <div class="card-content">
      <div class="search-bar">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 280px"
        />
        <el-select
          v-model="searchForm.departmentId"
          placeholder="选择部门"
          clearable
          style="width: 140px"
        >
          <el-option
            v-for="dept in store.departments"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
        <el-input
          v-model="searchForm.workerName"
          placeholder="员工姓名"
          :prefix-icon="Search"
          clearable
          style="width: 140px"
        />
        <el-select
          v-model="searchForm.status"
          placeholder="考勤状态"
          clearable
          style="width: 120px"
        >
          <el-option label="正常" value="正常" />
          <el-option label="迟到" value="迟到" />
          <el-option label="早退" value="早退" />
          <el-option label="缺勤" value="缺勤" />
          <el-option label="请假" value="请假" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="pagedList" style="width: 100%" border>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="workerName" label="姓名" width="100" />
        <el-table-column prop="departmentName" label="部门" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkIn" label="上班打卡" width="110" />
        <el-table-column prop="checkOut" label="下班打卡" width="110" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredList.length"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="员工" prop="workerId">
          <el-select v-model="form.workerId" placeholder="请选择员工" style="width: 100%" filterable>
            <el-option
              v-for="worker in store.workers.filter(w => w.status === '在职')"
              :key="worker.id"
              :label="`${worker.name} - ${worker.departmentName}`"
              :value="worker.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="考勤状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="正常" value="正常" />
            <el-option label="迟到" value="迟到" />
            <el-option label="早退" value="早退" />
            <el-option label="缺勤" value="缺勤" />
            <el-option label="请假" value="请假" />
          </el-select>
        </el-form-item>
        <el-form-item label="上班打卡" prop="checkIn">
          <el-time-picker
            v-model="form.checkIn"
            placeholder="选择时间"
            value-format="HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="下班打卡" prop="checkOut">
          <el-time-picker
            v-model="form.checkOut"
            placeholder="选择时间"
            value-format="HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.attendance-page {
  padding: 0;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-item {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.text-success {
  color: #10b981;
}

.text-warning {
  color: #f59e0b;
}

.text-danger {
  color: #ef4444;
}

.text-primary {
  color: #3b82f6;
}
</style>
