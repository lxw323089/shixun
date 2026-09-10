<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Edit, Delete, View } from '@element-plus/icons-vue'
import type { Worker } from '@/types'

const store = useAppStore()

onMounted(() => {
  store.loadWorkers()
  store.loadDepartments()
})

const searchForm = ref({
  keyword: '',
  departmentId: null as number | null,
  status: ''
})

const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const currentWorker = ref<Worker | null>(null)

const form = ref<Partial<Worker>>({
  departmentId: undefined,
  name: '',
  gender: '男',
  phone: '',
  idCard: '',
  entryDate: '',
  position: '',
  baseSalary: 0,
  status: '在职'
})

const rules: FormRules = {
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }],
  baseSalary: [{ required: true, message: '请输入基本工资', trigger: 'blur' }]
}

const filteredList = computed(() => {
  return store.workers.filter(w => {
    if (searchForm.value.keyword && !w.name.includes(searchForm.value.keyword) && !w.phone.includes(searchForm.value.keyword)) {
      return false
    }
    if (searchForm.value.departmentId && w.departmentId !== searchForm.value.departmentId) {
      return false
    }
    if (searchForm.value.status && w.status !== searchForm.value.status) {
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

function handleSearch() {
  currentPage.value = 1
}

function handleReset() {
  searchForm.value = { keyword: '', departmentId: null, status: '' }
  currentPage.value = 1
}

function handleAdd() {
  dialogTitle.value = '新增员工'
  form.value = {
    departmentId: undefined,
    name: '',
    gender: '男',
    phone: '',
    idCard: '',
    entryDate: '',
    position: '',
    baseSalary: 0,
    status: '在职'
  }
  dialogVisible.value = true
}

function handleEdit(row: Worker) {
  dialogTitle.value = '编辑员工'
  form.value = { ...row }
  dialogVisible.value = true
}

function handleView(row: Worker) {
  currentWorker.value = row
  detailVisible.value = true
}

async function handleDelete(row: Worker) {
  try {
    await ElMessageBox.confirm(`确定要删除员工"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await store.deleteWorker(row.id)
    ElMessage.success('删除成功')
  } catch (e) {}
}

async function handleSubmit() {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      const dept = store.departments.find(d => d.id === form.value.departmentId)
      const data = {
        ...form.value,
        departmentName: dept?.name || ''
      }
      if (form.value.id) {
        await store.updateWorker(form.value.id, data)
        ElMessage.success('修改成功')
      } else {
        await store.addWorker(data as Omit<Worker, 'id' | 'createTime' | 'updateTime'>)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
    }
  } catch (e) {
    console.log('表单验证未通过')
  }
}
</script>

<template>
  <div class="worker-page">
    <div class="page-header">
      <h2 class="page-title">员工管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增员工</el-button>
    </div>

    <div class="card-content">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="姓名/手机号"
          :prefix-icon="Search"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="searchForm.departmentId"
          placeholder="选择部门"
          clearable
          style="width: 160px"
        >
          <el-option
            v-for="dept in store.departments"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="选择状态"
          clearable
          style="width: 140px"
        >
          <el-option label="在职" value="在职" />
          <el-option label="离职" value="离职" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="pagedList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="departmentName" label="部门" width="100" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="entryDate" label="入职日期" width="120" />
        <el-table-column prop="baseSalary" label="基本工资" width="110">
          <template #default="{ row }">¥{{ row.baseSalary }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '在职' ? 'success' : 'info'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="departmentId">
              <el-select v-model="form.departmentId" placeholder="请选择部门" style="width: 100%">
                <el-option
                  v-for="dept in store.departments"
                  :key="dept.id"
                  :label="dept.name"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="form.entryDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基本工资" prop="baseSalary">
              <el-input-number v-model="form.baseSalary" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="在职" value="在职" />
                <el-option label="离职" value="离职" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="员工详情" width="500px">
      <el-descriptions :column="2" border v-if="currentWorker">
        <el-descriptions-item label="姓名">{{ currentWorker.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentWorker.gender }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentWorker.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ currentWorker.position }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentWorker.phone }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentWorker.idCard }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ currentWorker.entryDate }}</el-descriptions-item>
        <el-descriptions-item label="基本工资">¥{{ currentWorker.baseSalary }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentWorker.status === '在职' ? 'success' : 'info'" size="small">
            {{ currentWorker.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentWorker.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style scoped>
.worker-page {
  padding: 0;
}
</style>
