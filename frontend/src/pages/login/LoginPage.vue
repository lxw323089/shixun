<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const store = useAppStore()

const loginForm = ref({
  username: '',
  password: ''
})

const formRef = ref<FormInstance>()
const loading = ref(false)

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      loading.value = true
      const success = await store.login(loginForm.value.username, loginForm.value.password)
      loading.value = false
      if (success) {
        ElMessage.success('登录成功')
        const redirect = (route.query.redirect as string) || '/dashboard'
        router.replace(redirect)
      } else {
        ElMessage.error('用户名或密码错误')
      }
    }
  } catch (e) {
    console.log('表单验证未通过')
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-left">
      <div class="brand-info">
        <div class="brand-logo">
          <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="32" cy="32" r="30" fill="rgba(255,255,255,0.15)"/>
            <path d="M32 16C27.5817 16 24 19.5817 24 24C24 28.4183 27.5817 32 32 32C36.4183 32 40 28.4183 40 24C40 19.5817 36.4183 16 32 16Z" fill="#fff"/>
            <path d="M20 44C20 37.3726 25.3726 32 32 32C38.6274 32 44 37.3726 44 44V48H20V44Z" fill="#fff"/>
            <path d="M18 26L14 30L18 34" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M46 26L50 30L46 34" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="brand-title">员工管理系统</h1>
        <p class="brand-desc">Worker Management System</p>
        <div class="brand-divider"></div>
        <p class="brand-slogan">高效 · 智能 · 便捷</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-box">
        <h2 class="login-title">用户登录</h2>
        <p class="login-subtitle">请输入您的账号信息</p>
        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form>
        <div class="login-tips">
          <p>管理员账号：admin / admin123</p>
          <p>员工账号：zhangsan / 123456</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1e3a5f 0%, #1e40af 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.brand-info {
  color: #fff;
  max-width: 400px;
  text-align: center;
}

.brand-logo {
  width: 100px;
  height: 100px;
  margin: 0 auto 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-logo svg {
  width: 100%;
  height: 100%;
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 4px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.brand-desc {
  font-size: 20px;
  opacity: 0.85;
  margin-bottom: 28px;
  letter-spacing: 2px;
  font-weight: 300;
}

.brand-divider {
  width: 60px;
  height: 3px;
  background: rgba(255, 255, 255, 0.5);
  margin: 0 auto 28px;
  border-radius: 2px;
}

.brand-slogan {
  font-size: 18px;
  opacity: 0.9;
  letter-spacing: 6px;
  font-weight: 300;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8fafc;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 32px;
}

.login-form {
  margin-bottom: 24px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.login-tips {
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.8;
}
</style>
