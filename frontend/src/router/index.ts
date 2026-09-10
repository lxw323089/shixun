import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAppStore } from '@/stores/app'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/login/LoginPage.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/pages/dashboard/DashboardPage.vue'),
        meta: { title: '仪表盘', icon: 'DataAnalysis' }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/pages/department/DepartmentPage.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding', roles: ['admin'] }
      },
      {
        path: 'worker',
        name: 'Worker',
        component: () => import('@/pages/worker/WorkerPage.vue'),
        meta: { title: '员工管理', icon: 'User', roles: ['admin'] }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/pages/attendance/AttendancePage.vue'),
        meta: { title: '考勤管理', icon: 'Calendar', roles: ['admin'] }
      },
      {
        path: 'salary',
        name: 'Salary',
        component: () => import('@/pages/salary/SalaryPage.vue'),
        meta: { title: '工资管理', icon: 'Money', roles: ['admin'] }
      },
      {
        path: 'system',
        redirect: '/system/user',
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('@/pages/system/user/UserPage.vue'),
            meta: { title: '系统用户', icon: 'UserFilled', roles: ['admin'] }
          },
          {
            path: 'log',
            name: 'OperationLog',
            component: () => import('@/pages/system/log/LogPage.vue'),
            meta: { title: '操作日志', icon: 'Document', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/pages/profile/ProfilePage.vue'),
        meta: { title: '个人中心', icon: 'Avatar', roles: ['admin', 'worker'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, _from, next) => {
  const store = useAppStore()
  document.title = to.meta.title ? `${to.meta.title} - 员工管理系统` : '员工管理系统'

  if (to.meta.public) {
    if (to.path === '/login' && store.currentUser) {
      next('/dashboard')
    } else {
      next()
    }
  } else if (!store.currentUser) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    const role = store.currentUser.role
    const requiredRoles = to.meta.roles as string[] | undefined
    if (requiredRoles && !requiredRoles.includes(role)) {
      next('/dashboard')
    } else {
      // 首次进入受保护页面时加载数据
      if (store.departments.length === 0) {
        try {
          await store.initData()
        } catch (e) {
          console.error('初始化数据失败', e)
        }
      }
      next()
    }
  }
})

export default router
