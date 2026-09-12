<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import type { User } from '@/types'

const store = useAppStore()

onMounted(() => {
  store.loadUsers()
  store.loadWorkers()
})

const searchForm = ref({
  username: '',
  role: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()

const form = ref<Partial<User>>({
  username: '',
  role: 'worker',
  workerId: null,
  status: '启用'
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const filteredList = computed(() => {
  return store.users.filter(u => {
    if (searchForm.value.username && !u.username.includes(searchForm.value.username)) return false
    if (searchForm.value.role && u.role !== searchForm.value.role) return false
    if (searchForm.value.status && u.status !== searchForm.value.status) return false
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
  searchForm.value = { username: '', role: '', status: '' }
  currentPage.value = 1
}

function handleAdd() {
  dialogTitle.value = '新增用户'
  form.value = {
    username: '',
    role: 'worker',
    workerId: null,
    status: '启用'
  }
  dialogVisible.value = true
}

function handleEdit(row: User) {
  dialogTitle.value = '编辑用户'
  form.value = { ...row }
  dialogVisible.value = true
}

function handleDelete(row: User) {
  if (row.username === 'admin') {
    ElMessage.warning('管理员账号不能删除')
    return
  }
  ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await store.deleteUser(row.id)
      ElMessage.success('删除成功')
    } catch {
      // 错误消息已由请求拦截器显示
    }
  }).catch(() => {})
}

function handleSubmit() {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      const worker = form.value.workerId ? store.workers.find(w => w.id === form.value.workerId) : null
      const data = {
        ...form.value,
        workerName: worker?.name
      }
      try {
        if (form.value.id) {
          await store.updateUser(form.value.id, data)
          ElMessage.success('修改成功')
        } else {
          await store.addUser(data as Omit<User, 'id' | 'createTime'>)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
      } catch {
        // 错误消息已由请求拦截器显示
      }
    }
  })
}

function getRoleText(role: string) {
  return role === 'admin' ? '管理员' : '员工'
}

function getRoleType(role: string) {
  return role === 'admin' ? 'danger' : 'primary'
}

function getWorkerName(workerId: number | null | undefined) {
  if (!workerId) return '-'
  const w = store.workers.find(w => w.id === workerId)
  return w?.name || '-'
}
</script>

<template>
  <div class="user-page">
    <div class="page-header">
      <h2 class="page-title">系统用户</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
    </div>

    <div class="card-content">
      <div class="search-bar">
        <el-input
          v-model="searchForm.username"
          placeholder="用户名"
          :prefix-icon="Search"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="searchForm.role"
          placeholder="角色"
          clearable
          style="width: 120px"
        >
          <el-option label="管理员" value="admin" />
          <el-option label="员工" value="worker" />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
          style="width: 120px"
        >
          <el-option label="启用" value="启用" />
          <el-option label="禁用" value="禁用" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="pagedList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" size="small">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联员工" width="120">
          <template #default="{ row }">{{ getWorkerName(row.workerId) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="admin">管理员</el-radio>
            <el-radio value="worker">员工</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联员工" v-if="form.role === 'worker'">
          <el-select v-model="form.workerId" placeholder="请选择员工" style="width: 100%" clearable filterable>
            <el-option
              v-for="worker in store.workers.filter(w => w.status === '在职')"
              :key="worker.id"
              :label="`${worker.name} - ${worker.departmentName}`"
              :value="worker.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="启用">启用</el-radio>
            <el-radio value="禁用">禁用</el-radio>
          </el-radio-group>
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
.user-page {
  padding: 0;
}
</style>
