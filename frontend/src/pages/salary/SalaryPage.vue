<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Edit, Wallet, View } from '@element-plus/icons-vue'
import type { Salary } from '@/types'

const store = useAppStore()

onMounted(() => {
  store.loadSalaries()
  store.loadWorkers()
  store.loadDepartments()
})

const searchForm = ref({
  month: '',
  departmentId: null as number | null,
  workerName: '',
  status: ''
})

const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const currentSalary = ref<Salary | null>(null)

const form = ref<Partial<Salary>>({
  workerId: undefined,
  month: '',
  baseSalary: 0,
  bonus: 0,
  deduction: 0,
  status: '待发放'
})

const rules: FormRules = {
  workerId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }],
  baseSalary: [{ required: true, message: '请输入基本工资', trigger: 'blur' }]
}

const filteredList = computed(() => {
  return store.salaries.filter(s => {
    if (searchForm.value.month && s.month !== searchForm.value.month) return false
    if (searchForm.value.departmentId) {
      const worker = store.workers.find(w => w.id === s.workerId)
      if (!worker || worker.departmentId !== searchForm.value.departmentId) return false
    }
    if (searchForm.value.workerName && !s.workerName.includes(searchForm.value.workerName)) return false
    if (searchForm.value.status && s.status !== searchForm.value.status) return false
    return true
  })
})

const currentPage = ref(1)
const pageSize = ref(10)

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

const totalStats = computed(() => {
  const list = filteredList.value
  const baseTotal = list.reduce((sum, s) => sum + s.baseSalary, 0)
  const bonusTotal = list.reduce((sum, s) => sum + s.bonus, 0)
  const deductionTotal = list.reduce((sum, s) => sum + s.deduction, 0)
  const netTotal = list.reduce((sum, s) => sum + s.total, 0)
  return { baseTotal, bonusTotal, deductionTotal, netTotal, count: list.length }
})

function handleSearch() {
  currentPage.value = 1
}

function handleReset() {
  searchForm.value = { month: '', departmentId: null, workerName: '', status: '' }
  currentPage.value = 1
}

function handleAdd() {
  dialogTitle.value = '核算工资'
  form.value = {
    workerId: undefined,
    month: '',
    baseSalary: 0,
    bonus: 0,
    deduction: 0,
    status: '待发放'
  }
  dialogVisible.value = true
}

function handleEdit(row: Salary) {
  dialogTitle.value = '编辑工资'
  form.value = { ...row }
  dialogVisible.value = true
}

function handleView(row: Salary) {
  currentSalary.value = row
  detailVisible.value = true
}

function handlePay(row: Salary) {
  ElMessageBox.confirm(`确定要发放${row.workerName}的${row.month}工资吗？`, '提示', {
    confirmButtonText: '确定发放',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    store.paySalary(row.id)
    ElMessage.success('发放成功')
  }).catch(() => {})
}

function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (valid) {
      const worker = store.workers.find(w => w.id === form.value.workerId)
      const total = (form.value.baseSalary || 0) + (form.value.bonus || 0) - (form.value.deduction || 0)
      const data = {
        ...form.value,
        workerName: worker?.name || '',
        departmentName: worker?.departmentName || '',
        total
      }
      if (form.value.id) {
        store.updateSalary(form.value.id, data)
        ElMessage.success('修改成功')
      } else {
        store.addSalary(data as Omit<Salary, 'id' | 'createTime'>)
        ElMessage.success('核算成功')
      }
      dialogVisible.value = false
    }
  })
}
</script>

<template>
  <div class="salary-page">
    <div class="page-header">
      <h2 class="page-title">工资管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">核算工资</el-button>
    </div>

    <div class="stats-row">
      <div class="stat-item">
        <p class="stat-label">人数</p>
        <p class="stat-value">{{ totalStats.count }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">基本工资总额</p>
        <p class="stat-value">¥{{ totalStats.baseTotal.toLocaleString() }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">奖金总额</p>
        <p class="stat-value text-success">¥{{ totalStats.bonusTotal.toLocaleString() }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">扣款总额</p>
        <p class="stat-value text-danger">¥{{ totalStats.deductionTotal.toLocaleString() }}</p>
      </div>
      <div class="stat-item">
        <p class="stat-label">实发工资总额</p>
        <p class="stat-value text-primary">¥{{ totalStats.netTotal.toLocaleString() }}</p>
      </div>
    </div>

    <div class="card-content">
      <div class="search-bar">
        <el-date-picker
          v-model="searchForm.month"
          type="month"
          placeholder="选择月份"
          value-format="YYYY-MM"
          style="width: 160px"
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
          placeholder="发放状态"
          clearable
          style="width: 120px"
        >
          <el-option label="待发放" value="待发放" />
          <el-option label="已发放" value="已发放" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="pagedList" style="width: 100%" border>
        <el-table-column prop="month" label="月份" width="110" />
        <el-table-column prop="workerName" label="姓名" width="100" />
        <el-table-column prop="departmentName" label="部门" width="100" />
        <el-table-column prop="baseSalary" label="基本工资" width="100">
          <template #default="{ row }">¥{{ row.baseSalary }}</template>
        </el-table-column>
        <el-table-column prop="bonus" label="奖金" width="90">
          <template #default="{ row }">¥{{ row.bonus }}</template>
        </el-table-column>
        <el-table-column prop="deduction" label="扣款" width="90">
          <template #default="{ row }">¥{{ row.deduction }}</template>
        </el-table-column>
        <el-table-column prop="total" label="实发工资" width="110">
          <template #default="{ row }">
            <span class="total-salary">¥{{ row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '已发放' ? 'success' : 'warning'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)" v-if="row.status === '待发放'">编辑</el-button>
            <el-button type="success" link :icon="Wallet" @click="handlePay(row)" v-if="row.status === '待发放'">发放</el-button>
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
        <el-form-item label="月份" prop="month">
          <el-date-picker
            v-model="form.month"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="基本工资" prop="baseSalary">
          <el-input-number v-model="form.baseSalary" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="奖金" prop="bonus">
          <el-input-number v-model="form.bonus" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="扣款" prop="deduction">
          <el-input-number v-model="form.deduction" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实发工资">
          <span class="preview-total">
            ¥{{ (form.baseSalary || 0) + (form.bonus || 0) - (form.deduction || 0) }}
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="工资条详情" width="400px">
      <div class="salary-detail" v-if="currentSalary">
        <div class="detail-header">
          <p class="detail-month">{{ currentSalary.month }}</p>
          <el-tag :type="currentSalary.status === '已发放' ? 'success' : 'warning'">
            {{ currentSalary.status }}
          </el-tag>
        </div>
        <div class="detail-info">
          <p><span>姓名：</span>{{ currentSalary.workerName }}</p>
          <p><span>部门：</span>{{ currentSalary.departmentName }}</p>
        </div>
        <div class="detail-items">
          <div class="detail-item">
            <span>基本工资</span>
            <span>¥{{ currentSalary.baseSalary }}</span>
          </div>
          <div class="detail-item bonus">
            <span>奖金</span>
            <span>+ ¥{{ currentSalary.bonus }}</span>
          </div>
          <div class="detail-item deduction">
            <span>扣款</span>
            <span>- ¥{{ currentSalary.deduction }}</span>
          </div>
          <div class="detail-item total">
            <span>实发工资</span>
            <span>¥{{ currentSalary.total }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.salary-page {
  padding: 0;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
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
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.text-success {
  color: #10b981;
}

.text-danger {
  color: #ef4444;
}

.text-primary {
  color: #3b82f6;
}

.total-salary {
  font-weight: 600;
  color: #1e40af;
}

.preview-total {
  font-size: 18px;
  font-weight: 700;
  color: #1e40af;
}

.salary-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-month {
  font-size: 18px;
  font-weight: 600;
}

.detail-info {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 6px;
  color: #64748b;
  font-size: 14px;
}

.detail-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item.bonus span:last-child {
  color: #10b981;
}

.detail-item.deduction span:last-child {
  color: #ef4444;
}

.detail-item.total {
  font-size: 16px;
  font-weight: 700;
  padding-top: 12px;
  border-top: 2px solid #e2e8f0;
  border-bottom: none;
}

.detail-item.total span:last-child {
  color: #1e40af;
}
</style>
