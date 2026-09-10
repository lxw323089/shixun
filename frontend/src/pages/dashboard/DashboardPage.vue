<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref, nextTick } from 'vue'
import { useAppStore } from '@/stores/app'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  OfficeBuilding,
  User,
  Calendar,
  Wallet,
  Document
} from '@element-plus/icons-vue'

const store = useAppStore()
const router = useRouter()

// 图表容器 ref
const statusChartRef = ref<HTMLElement>()
const attendanceChartRef = ref<HTMLElement>()
const deptChartRef = ref<HTMLElement>()

let statusChart: echarts.ECharts | null = null
let attendanceChart: echarts.ECharts | null = null
let deptChart: echarts.ECharts | null = null

onMounted(async () => {
  await store.initData()
  await nextTick()
  initCharts()
})

onBeforeUnmount(() => {
  statusChart?.dispose()
  attendanceChart?.dispose()
  deptChart?.dispose()
})

function initCharts() {
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value)
    statusChart.setOption(getStatusOption())
  }
  if (attendanceChartRef.value) {
    attendanceChart = echarts.init(attendanceChartRef.value)
    attendanceChart.setOption(getAttendanceOption())
  }
  if (deptChartRef.value) {
    deptChart = echarts.init(deptChartRef.value)
    deptChart.setOption(getDeptOption())
  }
}

// 员工状态分布
const getStatusOption = () => {
  const active = store.workers.filter(w => w.status === '在职').length
  const inactive = store.workers.filter(w => w.status === '离职').length
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, icon: 'circle' },
    series: [{
      name: '员工状态',
      type: 'pie',
      radius: ['45%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: [
        { value: active, name: '在职', itemStyle: { color: '#10b981' } },
        { value: inactive, name: '离职', itemStyle: { color: '#94a3b8' } }
      ]
    }]
  }
}

// 考勤状态分布
const getAttendanceOption = () => {
  const statusMap: Record<string, number> = {}
  store.attendances.forEach(a => {
    statusMap[a.status] = (statusMap[a.status] || 0) + 1
  })
  const colors: Record<string, string> = {
    '正常': '#10b981',
    '迟到': '#f59e0b',
    '早退': '#f97316',
    '缺勤': '#ef4444',
    '请假': '#8b5cf6'
  }
  const data = Object.entries(statusMap).map(([name, value]) => ({
    name, value, itemStyle: { color: colors[name] || '#3b82f6' }
  }))
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, icon: 'circle' },
    series: [{
      name: '考勤状态',
      type: 'pie',
      radius: ['45%', '70%'],
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: data.length ? data : [{ value: 1, name: '暂无数据', itemStyle: { color: '#cbd5e1' } }]
    }]
  }
}

// 部门人数分布
const getDeptOption = () => {
  const deptMap: Record<string, number> = {}
  store.departments.forEach(d => { deptMap[d.name] = 0 })
  store.workers.forEach(w => {
    if (w.departmentName) deptMap[w.departmentName] = (deptMap[w.departmentName] || 0) + 1
  })
  const names = Object.keys(deptMap)
  const values = Object.values(deptMap)
  const colorList = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#14b8a6']
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: names,
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#64748b' },
      splitLine: { lineStyle: { color: '#f1f5f9' } }
    },
    series: [{
      name: '人数',
      type: 'bar',
      barWidth: '40%',
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: (params: any) => colorList[params.dataIndex % colorList.length]
      },
      data: values
    }]
  }
}

const stats = computed(() => [
  { title: '部门数量', value: store.departments.length, icon: OfficeBuilding, bg: 'linear-gradient(135deg, #60a5fa, #3b82f6)', path: '/department' },
  { title: '在职员工', value: store.workers.filter(w => w.status === '在职').length, icon: User, bg: 'linear-gradient(135deg, #f87171, #ef4444)', path: '/worker' },
  { title: '本月考勤率', value: '95.6%', icon: Calendar, bg: 'linear-gradient(135deg, #fbbf24, #f59e0b)', path: '/attendance' },
  { title: '本月工资总额', value: `¥${store.salaries.filter(s => s.month === '2024-09').reduce((sum, s) => sum + s.total, 0).toLocaleString()}`, icon: Wallet, bg: 'linear-gradient(135deg, #34d399, #10b981)', path: '/salary' }
])

const recentWorkers = computed(() => store.workers.slice(0, 5))
const recentLogs = computed(() => store.operationLogs.slice(0, 5))

function goToPage(path: string) {
  if (store.currentUser?.role === 'admin') router.push(path)
}
</script>

<template>
  <div class="dashboard">
    <div class="page-header">
      <h2 class="page-title">仪表盘</h2>
      <span class="welcome">欢迎回来，{{ store.currentUser?.username }}</span>
    </div>

    <!-- 彩色统计卡片 -->
    <div class="stats-grid">
      <div v-for="item in stats" :key="item.title" class="stat-card" :class="{ clickable: store.currentUser?.role === 'admin' }" :style="{ background: item.bg }" @click="goToPage(item.path)">
        <div class="stat-icon-wrap"><el-icon :size="32"><component :is="item.icon" /></el-icon></div>
        <div class="stat-info">
          <p class="stat-value">{{ item.value }}</p>
          <p class="stat-title">{{ item.title }}</p>
        </div>
      </div>
    </div>

    <!-- 概率分布图 -->
    <div class="chart-row">
      <div class="chart-card card-content">
        <div class="card-header"><h3 class="card-title">员工状态分布</h3></div>
        <div ref="statusChartRef" class="chart-box"></div>
      </div>
      <div class="chart-card card-content">
        <div class="card-header"><h3 class="card-title">考勤状态分布</h3></div>
        <div ref="attendanceChartRef" class="chart-box"></div>
      </div>
      <div class="chart-card card-content">
        <div class="card-header"><h3 class="card-title">部门人数分布</h3></div>
        <div ref="deptChartRef" class="chart-box"></div>
      </div>
    </div>

    <!-- 最近数据 -->
    <div class="dashboard-row">
      <div class="dashboard-card card-content">
        <div class="card-header">
          <h3 class="card-title">最近入职员工</h3>
          <el-button type="primary" link @click="router.push('/worker')" v-if="store.currentUser?.role === 'admin'">查看全部</el-button>
        </div>
        <el-table :data="recentWorkers" style="width: 100%">
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="departmentName" label="部门" width="120" />
          <el-table-column prop="position" label="职位" width="120" />
          <el-table-column prop="entryDate" label="入职日期" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === '在职' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="dashboard-card card-content">
        <div class="card-header">
          <h3 class="card-title">最近操作日志</h3>
          <el-button type="primary" link @click="router.push('/system/log')" v-if="store.currentUser?.role === 'admin'">查看全部</el-button>
        </div>
        <div class="log-list">
          <div v-for="log in recentLogs" :key="log.id" class="log-item">
            <div class="log-icon"><el-icon><Document /></el-icon></div>
            <div class="log-content">
              <p class="log-text">{{ log.module }} - {{ log.content }}</p>
              <p class="log-meta"><span>{{ log.username }}</span><span>{{ log.createTime }}</span></p>
            </div>
          </div>
          <el-empty v-if="recentLogs.length === 0" description="暂无操作日志" :image-size="80" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard { padding: 0; }
.welcome { font-size: 14px; color: #64748b; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}
.stat-card {
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}
.stat-card.clickable { cursor: pointer; }
.stat-card.clickable:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); }
.stat-icon-wrap {
  width: 56px; height: 56px; border-radius: 12px;
  background: rgba(255, 255, 255, 0.25);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-value { font-size: 30px; font-weight: 700; color: #fff; line-height: 1.2; }
.stat-title { font-size: 14px; color: rgba(255, 255, 255, 0.9); margin-top: 4px; }

/* 图表行 */
.chart-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.chart-card { min-height: 320px; }
.chart-box { width: 100%; height: 250px; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.card-title { font-size: 16px; font-weight: 600; color: #1e293b; }

.dashboard-row {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
}
.dashboard-card { min-height: 360px; }

.log-list { display: flex; flex-direction: column; gap: 16px; }
.log-item { display: flex; gap: 12px; padding-bottom: 16px; border-bottom: 1px solid #f1f5f9; }
.log-item:last-child { border-bottom: none; padding-bottom: 0; }
.log-icon {
  width: 36px; height: 36px; border-radius: 8px;
  background: #eff6ff; color: #3b82f6;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.log-text { font-size: 14px; color: #334155; margin-bottom: 4px; }
.log-meta { font-size: 12px; color: #94a3b8; display: flex; gap: 12px; }
</style>
