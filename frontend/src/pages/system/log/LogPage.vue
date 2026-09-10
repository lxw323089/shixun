<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { Search, View } from '@element-plus/icons-vue'
import type { OperationLog } from '@/types'

const store = useAppStore()

onMounted(() => {
  store.loadLogs()
})

const searchForm = ref({
  dateRange: [] as string[],
  username: '',
  module: '',
  operation: ''
})

const detailVisible = ref(false)
const currentLog = ref<OperationLog | null>(null)

const moduleOptions = ['部门管理', '员工管理', '考勤管理', '工资管理', '系统用户', '操作日志']
const operationOptions = ['新增', '修改', '删除', '查询', '录入', '核算', '发放', '权限分配']

const filteredList = computed(() => {
  return store.operationLogs.filter(log => {
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      const logDate = log.createTime.split(' ')[0]
      if (logDate < searchForm.value.dateRange[0] || logDate > searchForm.value.dateRange[1]) {
        return false
      }
    }
    if (searchForm.value.username && !log.username.includes(searchForm.value.username)) return false
    if (searchForm.value.module && log.module !== searchForm.value.module) return false
    if (searchForm.value.operation && log.operation !== searchForm.value.operation) return false
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
  searchForm.value = { dateRange: [], username: '', module: '', operation: '' }
  currentPage.value = 1
}

function handleView(row: OperationLog) {
  currentLog.value = row
  detailVisible.value = true
}
</script>

<template>
  <div class="log-page">
    <div class="page-header">
      <h2 class="page-title">操作日志</h2>
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
        <el-input
          v-model="searchForm.username"
          placeholder="操作人"
          :prefix-icon="Search"
          clearable
          style="width: 140px"
        />
        <el-select
          v-model="searchForm.module"
          placeholder="操作模块"
          clearable
          style="width: 130px"
        >
          <el-option v-for="m in moduleOptions" :key="m" :label="m" :value="m" />
        </el-select>
        <el-select
          v-model="searchForm.operation"
          placeholder="操作类型"
          clearable
          style="width: 120px"
        >
          <el-option v-for="o in operationOptions" :key="o" :label="o" :value="o" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="pagedList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="operation" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.operation }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="操作内容" min-width="200" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="日志详情" width="500px">
      <el-descriptions :column="2" border v-if="currentLog">
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ currentLog.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag size="small">{{ currentLog.operation }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作内容" :span="2">
          {{ currentLog.content }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">
          {{ currentLog.createTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style scoped>
.log-page {
  padding: 0;
}
</style>
