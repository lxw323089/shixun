<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import {
  DataAnalysis,
  OfficeBuilding,
  User,
  Calendar,
  Wallet,
  Setting,
  Document,
  CaretBottom,
  SwitchButton,
  Avatar
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useAppStore()

const menuItems = computed(() => {
  const role = store.currentUser?.role
  const items: any[] = [
    { index: '/dashboard', title: '仪表盘', icon: DataAnalysis },
  ]
  if (role === 'admin') {
    items.push(
      { index: '/department', title: '部门管理', icon: OfficeBuilding },
      { index: '/worker', title: '员工管理', icon: User },
      { index: '/attendance', title: '考勤管理', icon: Calendar },
      { index: '/salary', title: '工资管理', icon: Wallet },
      {
        index: '/system',
        title: '系统管理',
        icon: Setting,
        children: [
          { index: '/system/user', title: '系统用户', icon: User },
          { index: '/system/log', title: '操作日志', icon: Document }
        ]
      }
    )
  }
  items.push({ index: '/profile', title: '个人中心', icon: User })
  return items
})

function handleSelect(index: string) {
  router.push(index)
}

function handleLogout() {
  store.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="main-container">
    <!-- 左侧白色侧边栏 -->
    <el-aside :width="store.sidebarCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!store.sidebarCollapsed" class="logo-text">员工管理系统</span>
        <span v-else class="logo-text">EMS</span>
      </div>

      <!-- 用户信息区 -->
      <div v-if="!store.sidebarCollapsed" class="user-card">
        <div class="avatar-wrapper" @click="router.push('/profile')">
          <el-avatar v-if="store.currentUser?.avatar" :size="56" :src="store.currentUser.avatar" class="user-avatar" />
          <el-avatar v-else :size="56" class="user-avatar">
            <el-icon :size="28"><User /></el-icon>
          </el-avatar>
        </div>
        <div class="user-name">{{ store.currentUser?.nickname || store.currentUser?.username }}</div>
        <div class="user-btns">
          <el-button size="small" text @click="router.push('/profile')">个人设置</el-button>
        </div>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="route.path"
        :collapse="store.sidebarCollapsed"
        class="sidebar-menu"
        background-color="#ffffff"
        text-color="#64748b"
        active-text-color="#3b82f6"
        router
        @select="handleSelect"
      >
        <template v-for="item in menuItems" :key="item.index">
          <el-sub-menu v-if="item.children" :index="item.index">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.index" :index="child.index">
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部头部 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="store.toggleSidebar">
            <SwitchButton />
          </el-icon>
          <span class="page-label">{{ route.meta.title || '员工管理系统' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="hover">
            <span class="user-info">
              <el-avatar v-if="store.currentUser?.avatar" :size="30" :src="store.currentUser.avatar" class="header-avatar" />
              <el-avatar v-else :size="30" class="header-avatar">
                {{ (store.currentUser?.nickname || store.currentUser?.username || '')?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="welcome-text">{{ store.currentUser?.nickname || store.currentUser?.username }}，欢迎您</span>
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">
                  <el-icon><Avatar /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.main-container {
  height: 100vh;
}

/* ===== 侧边栏 ===== */
.sidebar {
  background-color: #ffffff;
  transition: width 0.3s;
  overflow: hidden;
  border-right: 1px solid #e5e7eb;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.03);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #f1f5f9;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #3b82f6;
  letter-spacing: 1px;
}

/* 用户卡片 */
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.avatar-wrapper {
  margin-bottom: 10px;
}

.user-avatar {
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.user-btns {
  display: flex;
  gap: 4px;
}

/* 菜单 */
.sidebar-menu {
  border-right: none;
  padding: 8px;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  border-radius: 8px;
  margin-bottom: 4px;
  height: 44px;
  line-height: 44px;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background-color: #f1f5f9 !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #eff6ff !important;
  color: #3b82f6 !important;
  font-weight: 600;
}

/* ===== 头部 ===== */
.header {
  background-color: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #94a3b8;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #3b82f6;
}

.page-label {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-info:hover {
  background-color: #f8fafc;
}

.header-avatar {
  background-color: #3b82f6;
}

.welcome-text {
  font-size: 14px;
  color: #475569;
}

/* ===== 主内容区 ===== */
.main-content {
  background-color: #f5f7fa;
  padding: 20px 24px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
