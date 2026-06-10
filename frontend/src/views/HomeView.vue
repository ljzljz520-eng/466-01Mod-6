<template>
  <AppLayout>
    <div class="max-w-6xl mx-auto space-y-10">

      <!-- Hero Section -->
      <div class="text-center py-12 space-y-4">
        <h2 class="text-4xl sm:text-5xl font-extrabold text-slate-900 tracking-tight">
          实验废液桶交接追踪系统
        </h2>
        <p class="text-xl text-slate-500 max-w-2xl mx-auto">
          全流程追踪废液管理：学生提交 → 安全员检查 → 危废暂存 → 第三方回收
        </p>
      </div>

      <!-- Quick Access Cards -->
      <div class="grid gap-4 grid-cols-2 md:grid-cols-5">
        <router-link to="/waste/submit" class="quick-card">
          <div class="p-4 bg-white rounded-xl shadow-sm border border-slate-200 h-full hover:shadow-md transition-all hover:-translate-y-0.5">
            <div class="flex items-center gap-3">
              <div class="p-2 bg-blue-50 rounded-lg text-blue-600">
                <el-icon :size="24"><Upload /></el-icon>
              </div>
              <div>
                <h3 class="font-bold text-slate-800">废液提交</h3>
                <p class="text-xs text-slate-500">学生提交废液</p>
              </div>
            </div>
          </div>
        </router-link>

        <router-link to="/waste/safety-check" class="quick-card">
          <div class="p-4 bg-white rounded-xl shadow-sm border border-slate-200 h-full hover:shadow-md transition-all hover:-translate-y-0.5">
            <div class="flex items-center gap-3">
              <div class="p-2 bg-green-50 rounded-lg text-green-600">
                <el-icon :size="24"><CircleCheck /></el-icon>
              </div>
              <div>
                <h3 class="font-bold text-slate-800">安全检查</h3>
                <p class="text-xs text-slate-500">安全员审核</p>
              </div>
            </div>
          </div>
        </router-link>

        <router-link to="/waste/storage" class="quick-card">
          <div class="p-4 bg-white rounded-xl shadow-sm border border-slate-200 h-full hover:shadow-md transition-all hover:-translate-y-0.5">
            <div class="flex items-center gap-3">
              <div class="p-2 bg-amber-50 rounded-lg text-amber-600">
                <el-icon :size="24"><Box /></el-icon>
              </div>
              <div>
                <h3 class="font-bold text-slate-800">暂存入库</h3>
                <p class="text-xs text-slate-500">危废暂存间</p>
              </div>
            </div>
          </div>
        </router-link>

        <router-link to="/waste/collection" class="quick-card">
          <div class="p-4 bg-white rounded-xl shadow-sm border border-slate-200 h-full hover:shadow-md transition-all hover:-translate-y-0.5">
            <div class="flex items-center gap-3">
              <div class="p-2 bg-purple-50 rounded-lg text-purple-600">
                <el-icon :size="24"><Camera /></el-icon>
              </div>
              <div>
                <h3 class="font-bold text-slate-800">扫码回收</h3>
                <p class="text-xs text-slate-500">第三方回收</p>
              </div>
            </div>
          </div>
        </router-link>

        <router-link to="/waste/trace" class="quick-card">
          <div class="p-4 bg-white rounded-xl shadow-sm border border-slate-200 h-full hover:shadow-md transition-all hover:-translate-y-0.5">
            <div class="flex items-center gap-3">
              <div class="p-2 bg-indigo-50 rounded-lg text-indigo-600">
                <el-icon :size="24"><Search /></el-icon>
              </div>
              <div>
                <h3 class="font-bold text-slate-800">追踪查询</h3>
                <p class="text-xs text-slate-500">全程追溯</p>
              </div>
            </div>
          </div>
        </router-link>
      </div>

      <!-- Statistics Cards -->
      <div class="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-4">
        <el-card shadow="hover" class="border-none ring-1 ring-slate-200">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-bold text-slate-700">废液桶总数</span>
              <el-icon :size="18" class="text-blue-500"><Box /></el-icon>
            </div>
          </template>
          <div class="text-3xl font-bold text-slate-800">
            {{ statistics.total || 0 }}
          </div>
        </el-card>

        <el-card shadow="hover" class="border-none ring-1 ring-slate-200">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-bold text-slate-700">待检查</span>
              <el-tag type="warning" size="small">待处理</el-tag>
            </div>
          </template>
          <div class="text-3xl font-bold text-amber-600">
            {{ statistics.byStatus?.SUBMITTED || 0 }}
          </div>
        </el-card>

        <el-card shadow="hover" class="border-none ring-1 ring-slate-200">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-bold text-slate-700">暂存中</span>
              <el-tag type="info" size="small">在库</el-tag>
            </div>
          </template>
          <div class="text-3xl font-bold text-blue-600">
            {{ statistics.byStatus?.IN_STORAGE || 0 }}
          </div>
        </el-card>

        <el-card shadow="hover" class="border-none ring-1 ring-slate-200">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-bold text-slate-700">已回收</span>
              <el-tag type="success" size="small">完成</el-tag>
            </div>
          </template>
          <div class="text-3xl font-bold text-green-600">
            {{ statistics.byStatus?.COLLECTED || 0 }}
          </div>
        </el-card>
      </div>

      <!-- System Status -->
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center">
          <h3 class="text-lg font-bold text-slate-800">系统状态</h3>
          <el-button type="primary" size="small" @click="checkHealth" :loading="loadingHealth">
            检查状态
          </el-button>
        </div>
        <div class="p-6">
          <div class="grid gap-4 grid-cols-1 md:grid-cols-3">
            <div class="flex items-center gap-3 p-4 bg-blue-50 rounded-lg">
              <div class="h-3 w-3 rounded-full" :class="isBackendUp ? 'bg-green-500' : 'bg-red-500'"></div>
              <div>
                <p class="font-medium text-slate-700">后端服务</p>
                <p class="text-sm text-slate-500">{{ backendStatus || '检查中...' }}</p>
              </div>
            </div>
            <div class="flex items-center gap-3 p-4 bg-green-50 rounded-lg">
              <div class="h-3 w-3 rounded-full bg-green-500"></div>
              <div>
                <p class="font-medium text-slate-700">前端服务</p>
                <p class="text-sm text-slate-500">运行正常</p>
              </div>
            </div>
            <div class="flex items-center gap-3 p-4 bg-orange-50 rounded-lg">
              <div class="h-3 w-3 rounded-full bg-orange-500"></div>
              <div>
                <p class="font-medium text-slate-700">数据库</p>
                <p class="text-sm text-slate-500">MySQL 8.0</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Flow Diagram -->
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100">
          <h3 class="text-lg font-bold text-slate-800">废液流转流程</h3>
        </div>
        <div class="p-6">
          <div class="flex items-center justify-between flex-wrap gap-4">
            <div class="flex-1 min-w-32 text-center">
              <div class="w-16 h-16 mx-auto bg-blue-100 rounded-full flex items-center justify-center mb-2">
                <el-icon :size="28" class="text-blue-600"><Upload /></el-icon>
              </div>
              <p class="font-medium text-slate-700">学生提交</p>
              <p class="text-xs text-slate-500">填写废液信息</p>
            </div>
            <el-icon :size="24" class="text-slate-300 mx-2"><ArrowRight /></el-icon>
            <div class="flex-1 min-w-32 text-center">
              <div class="w-16 h-16 mx-auto bg-green-100 rounded-full flex items-center justify-center mb-2">
                <el-icon :size="28" class="text-green-600"><CircleCheck /></el-icon>
              </div>
              <p class="font-medium text-slate-700">安全员检查</p>
              <p class="text-xs text-slate-500">审核并修正类别</p>
            </div>
            <el-icon :size="24" class="text-slate-300 mx-2"><ArrowRight /></el-icon>
            <div class="flex-1 min-w-32 text-center">
              <div class="w-16 h-16 mx-auto bg-amber-100 rounded-full flex items-center justify-center mb-2">
                <el-icon :size="28" class="text-amber-600"><Box /></el-icon>
              </div>
              <p class="font-medium text-slate-700">危废暂存</p>
              <p class="text-xs text-slate-500">入库暂存间</p>
            </div>
            <el-icon :size="24" class="text-slate-300 mx-2"><ArrowRight /></el-icon>
            <div class="flex-1 min-w-32 text-center">
              <div class="w-16 h-16 mx-auto bg-purple-100 rounded-full flex items-center justify-center mb-2">
                <el-icon :size="28" class="text-purple-600"><Van /></el-icon>
              </div>
              <p class="font-medium text-slate-700">第三方回收</p>
              <p class="text-xs text-slate-500">扫码确认</p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Upload,
  CircleCheck,
  Box,
  Camera,
  Search,
  ArrowRight,
  Van
} from '@element-plus/icons-vue'
import AppLayout from '@/components/AppLayout.vue'
import api from '@/api'
import { wasteBucketApi } from '@/api/wasteBucket'

const loadingHealth = ref(false)
const backendStatus = ref('')
const isBackendUp = ref(false)
const statistics = ref({})
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const checkHealth = async () => {
  loadingHealth.value = true
  try {
    const res = await api.get('/health')
    backendStatus.value = res.message
    isBackendUp.value = res.status === 'UP'
  } catch (err) {
    backendStatus.value = '后端已断开'
    isBackendUp.value = false
  } finally {
    loadingHealth.value = false
  }
}

const fetchStatistics = async () => {
  try {
    const res = await wasteBucketApi.getStatistics()
    statistics.value = res
  } catch (err) {
    console.error('获取统计数据失败', err)
  }
}

onMounted(async () => {
  await checkHealth()
  if (isBackendUp.value) {
    fetchStatistics()
  }
})
</script>

<style scoped>
.quick-card {
  text-decoration: none;
  color: inherit;
}
</style>
