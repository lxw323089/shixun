<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { User, Calendar, Wallet, Camera, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as userApi from '@/api/user'

const store = useAppStore()

onMounted(() => {
  // 重新从数据库加载个人信息相关数据，避免显示缓存旧数据
  store.loadWorkers()
  store.loadAttendances()
  store.loadSalaries()
})

const activeTab = ref('info')
const nicknameInput = ref('')
const nicknameEditing = ref(false)

const displayName = computed(() => store.currentUser?.nickname || store.currentUser?.username || '')
const avatarUrl = computed(() => {
  const a = store.currentUser?.avatar
  if (!a) return ''
  return a.startsWith('http') ? a : a
})

const myInfo = computed(() => {
  if (store.currentUser?.workerId) {
    return store.workers.find(w => w.id === store.currentUser?.workerId)
  }
  return null
})

const myAttendances = computed(() => {
  if (store.currentUser?.workerId) {
    return store.attendances.filter(a => a.workerId === store.currentUser?.workerId)
  }
  return []
})

const mySalaries = computed(() => {
  if (store.currentUser?.workerId) {
    return store.salaries.filter(s => s.workerId === store.currentUser?.workerId)
  }
  return []
})

const attendancePage = ref(1)
const attendancePageSize = ref(5)
const pagedAttendances = computed(() => {
  const start = (attendancePage.value - 1) * attendancePageSize.value
  return myAttendances.value.slice(start, start + attendancePageSize.value)
})

const salaryPage = ref(1)
const salaryPageSize = ref(5)
const pagedSalaries = computed(() => {
  const start = (salaryPage.value - 1) * salaryPageSize.value
  return mySalaries.value.slice(start, start + salaryPageSize.value)
})

function getStatusType(status: string) {
  const map: Record<string, string> = {
    '正常': 'success', '迟到': 'warning', '早退': 'warning', '缺勤': 'danger', '请假': 'info'
  }
  return map[status] || 'info'
}

// 头像上传
async function handleAvatarChange(file: any) {
  const rawFile = file.raw as File
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.error('请上传图片文件')
    return
  }
  if (rawFile.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 2MB')
    return
  }
  try {
    const res = await userApi.uploadAvatar(rawFile)
    if (res.code === 200) {
      store.updateCurrentUser({ avatar: res.data.avatar })
      ElMessage.success('头像更新成功')
    } else {
      ElMessage.error('头像上传失败')
    }
  } catch (e) {
    ElMessage.error('头像上传失败')
  }
}

// 昵称编辑
function startEditNickname() {
  nicknameInput.value = store.currentUser?.nickname || store.currentUser?.username || ''
  nicknameEditing.value = true
}

async function saveNickname() {
  const name = nicknameInput.value.trim()
  if (!name) {
    ElMessage.warning('名称不能为空')
    return
  }
  try {
    const res = await userApi.updateProfile({ nickname: name, username: name })
    if (res.code === 200) {
      store.updateCurrentUser({ nickname: name, username: name })
      nicknameEditing.value = false
      ElMessage.success('名称修改成功，下次登录请使用新名称')
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '修改失败')
  }
}
</script>

<template>
  <div class="profile-page">
    <div class="page-header">
      <h2 class="page-title">个人中心</h2>
    </div>

    <div class="profile-layout">
      <div class="profile-sidebar card-content">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="() => false"
            :on-change="handleAvatarChange"
            accept="image/*"
          >
            <div class="avatar-wrapper">
              <el-avatar v-if="avatarUrl" :size="80" :src="avatarUrl" />
              <el-avatar v-else :size="80" style="background-color: #3b82f6; font-size: 32px;">
                {{ store.currentUser?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <div class="avatar-mask">
                <el-icon><Camera /></el-icon>
              </div>
            </div>
          </el-upload>

          <div v-if="!nicknameEditing" class="name-row" @dblclick="startEditNickname">
            <h3 class="username">{{ displayName }}</h3>
            <el-icon class="edit-icon" @click.stop="startEditNickname"><Edit /></el-icon>
          </div>
          <div v-else class="name-edit-row">
            <el-input v-model="nicknameInput" size="small" style="width: 130px" />
            <el-button size="small" type="primary" @click="saveNickname">保存</el-button>
            <el-button size="small" @click="nicknameEditing = false">取消</el-button>
          </div>

          <p class="role">
            <el-tag :type="store.currentUser?.role === 'admin' ? 'danger' : 'primary'" size="small">
              {{ store.currentUser?.role === 'admin' ? '管理员' : '员工' }}
            </el-tag>
          </p>
        </div>
        <el-menu :default-active="activeTab" class="profile-menu" @select="(k: string) => activeTab = k">
          <el-menu-item index="info">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="attendance" v-if="store.currentUser?.role === 'worker'">
            <el-icon><Calendar /></el-icon>
            <span>我的考勤</span>
          </el-menu-item>
          <el-menu-item index="salary" v-if="store.currentUser?.role === 'worker'">
            <el-icon><Wallet /></el-icon>
            <span>我的工资</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="profile-content card-content">
        <div v-show="activeTab === 'info'" class="tab-content">
          <h3 class="tab-title">个人信息</h3>
          <div v-if="myInfo" class="info-section">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="姓名">{{ myInfo.name }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ myInfo.gender }}</el-descriptions-item>
              <el-descriptions-item label="部门">{{ myInfo.departmentName }}</el-descriptions-item>
              <el-descriptions-item label="职位">{{ myInfo.position }}</el-descriptions-item>
              <el-descriptions-item label="手机号">{{ myInfo.phone }}</el-descriptions-item>
              <el-descriptions-item label="身份证号">{{ myInfo.idCard }}</el-descriptions-item>
              <el-descriptions-item label="入职日期">{{ myInfo.entryDate }}</el-descriptions-item>
              <el-descriptions-item label="基本工资">¥{{ myInfo.baseSalary }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="myInfo.status === '在职' ? 'success' : 'info'" size="small">
                  {{ myInfo.status }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          <div v-else class="empty-info">
            <el-empty description="暂无个人信息" />
          </div>
        </div>

        <div v-show="activeTab === 'attendance'" class="tab-content">
          <h3 class="tab-title">我的考勤</h3>
          <div class="stats-row">
            <div class="stat-item">
              <p class="stat-label">总记录</p>
              <p class="stat-value">{{ myAttendances.length }}</p>
            </div>
            <div class="stat-item">
              <p class="stat-label">正常</p>
              <p class="stat-value text-success">{{ myAttendances.filter(a => a.status === '正常').length }}</p>
            </div>
            <div class="stat-item">
              <p class="stat-label">迟到/早退</p>
              <p class="stat-value text-warning">{{ myAttendances.filter(a => a.status === '迟到' || a.status === '早退').length }}</p>
            </div>
            <div class="stat-item">
              <p class="stat-label">缺勤</p>
              <p class="stat-value text-danger">{{ myAttendances.filter(a => a.status === '缺勤').length }}</p>
            </div>
          </div>
          <el-table :data="pagedAttendances" style="width: 100%" border>
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="checkIn" label="上班打卡" width="120" />
            <el-table-column prop="checkOut" label="下班打卡" width="120" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <div class="pagination-container">
            <el-pagination v-model:current-page="attendancePage" v-model:page-size="attendancePageSize" :page-sizes="[5, 10, 20]" :total="myAttendances.length" layout="total, sizes, prev, pager, next" />
          </div>
        </div>

        <div v-show="activeTab === 'salary'" class="tab-content">
          <h3 class="tab-title">我的工资</h3>
          <el-table :data="pagedSalaries" style="width: 100%" border>
            <el-table-column prop="month" label="月份" width="120" />
            <el-table-column prop="baseSalary" label="基本工资" width="120">
              <template #default="{ row }">¥{{ row.baseSalary }}</template>
            </el-table-column>
            <el-table-column prop="bonus" label="奖金" width="100">
              <template #default="{ row }">¥{{ row.bonus }}</template>
            </el-table-column>
            <el-table-column prop="deduction" label="扣款" width="100">
              <template #default="{ row }">¥{{ row.deduction }}</template>
            </el-table-column>
            <el-table-column prop="total" label="实发工资" width="120">
              <template #default="{ row }">
                <span style="font-weight: 600; color: #3b82f6">¥{{ row.total }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '已发放' ? 'success' : 'warning'" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination v-model:current-page="salaryPage" v-model:page-size="salaryPageSize" :page-sizes="[5, 10, 20]" :total="mySalaries.length" layout="total, sizes, prev, pager, next" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page { padding: 0; }

.profile-layout {
  display: flex;
  gap: 20px;
}

.profile-sidebar {
  width: 240px;
  flex-shrink: 0;
  padding: 0;
  overflow: hidden;
}

.avatar-section {
  padding: 24px;
  text-align: center;
  border-bottom: 1px solid #f1f5f9;
}

.avatar-uploader {
  display: inline-block;
  cursor: pointer;
}

.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto;
}

.avatar-wrapper :deep(.el-avatar) {
  width: 80px !important;
  height: 80px !important;
  line-height: 80px !important;
}

.avatar-mask {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  font-size: 22px;
}

.avatar-wrapper:hover .avatar-mask {
  opacity: 1;
}

.name-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin: 12px 0 8px;
  cursor: pointer;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.edit-icon {
  font-size: 14px;
  color: #94a3b8;
}

.edit-icon:hover {
  color: #3b82f6;
}

.name-edit-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin: 12px 0 8px;
}

.role { margin: 0; }

.profile-menu { border-right: none; }

.profile-content {
  flex: 1;
  min-height: 500px;
}

.tab-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.stat-item {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.text-success { color: #10b981; }
.text-warning { color: #f59e0b; }
.text-danger { color: #ef4444; }

.empty-info { padding: 40px 0; }
</style>
