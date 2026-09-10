<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import type { Department } from '@/types'

const store = useAppStore()

onMounted(() => {
  store.loadDepartments()
})

const searchKeyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()

const form = ref<Partial<Department>>({
  name: '',
  description: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const filteredList = computed(() => {
  if (!searchKeyword.value) return store.departments
  return store.departments.filter(d =>
    d.name.includes(searchKeyword.value) ||
    d.description.includes(searchKeyword.value)
  )
})

const currentPage = ref(1)
const pageSize = ref(10)

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

function handleAdd() {
  dialogTitle.value = '新增部门'
  form.value = { name: '', description: '' }
  dialogVisible.value = true
}

function handleEdit(row: Department) {
  dialogTitle.value = '编辑部门'
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleDelete(row: Department) {
  try {
    await ElMessageBox.confirm(`确定要删除部门"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await store.deleteDepartment(row.id)
    ElMessage.success('删除成功')
  } catch (e) {}
}

async function handleSubmit() {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      if (form.value.id) {
        await store.updateDepartment(form.value.id, form.value)
        ElMessage.success('修改成功')
      } else {
        await store.addDepartment(form.value as Omit<Department, 'id' | 'createTime' | 'updateTime'>)
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
  <div class="department-page">
    <div class="page-header">
      <h2 class="page-title">部门管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增部门</el-button>
    </div>

    <div class="card-content">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索部门名称或描述"
          :prefix-icon="Search"
          clearable
          style="width: 300px"
        />
      </div>

      <el-table :data="pagedList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="部门名称" width="150" />
        <el-table-column prop="description" label="部门描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
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
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入部门描述" />
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
.department-page {
  padding: 0;
}
</style>
