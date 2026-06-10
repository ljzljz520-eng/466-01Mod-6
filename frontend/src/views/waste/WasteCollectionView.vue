<template>
  <AppLayout>
    <div class="max-w-6xl mx-auto space-y-6">
      <div class="grid gap-6 grid-cols-1 md:grid-cols-2">
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <div class="p-6 border-b border-slate-100">
            <h2 class="text-xl font-bold text-slate-800">扫码回收</h2>
            <p class="text-slate-500 text-sm mt-1">扫描废液桶二维码确认回收</p>
          </div>

          <div class="p-6 space-y-4">
            <div class="flex items-center gap-3 p-4 bg-blue-50 rounded-lg border border-blue-100">
              <el-icon :size="24" color="#3b82f6">
                <Camera />
              </el-icon>
              <div>
                <p class="text-sm font-medium text-blue-900">扫码回收模式</p>
                <p class="text-xs text-blue-600">输入二维码编号进行回收确认</p>
              </div>
            </div>

            <el-form :model="scanForm" label-width="100px">
              <el-form-item label="回收人员ID">
                <el-input v-model="scanForm.recyclerId" placeholder="请输入回收人员ID" />
              </el-form-item>
              <el-form-item label="回收人员姓名">
                <el-input v-model="scanForm.recyclerName" placeholder="请输入回收人员姓名" />
              </el-form-item>
              <el-form-item label="二维码">
                <el-input
                  v-model="scanForm.qrCode"
                  placeholder="请扫描或输入二维码"
                  @keyup.enter="handleScan"
                >
                  <template #append>
                    <el-button @click="handleScan" :loading="scanning">查询</el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-form>

            <div class="pt-2">
              <p class="text-sm text-slate-500 mb-2">快速选择：</p>
              <div class="flex flex-wrap gap-2">
                <el-tag
                  v-for="item in pendingList.slice(0, 5)"
                  :key="item.id"
                  class="cursor-pointer hover:opacity-80"
                  @click="quickSelect(item)"
                >
                  {{ item.bucketNo }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <div class="p-6 border-b border-slate-100">
            <h2 class="text-xl font-bold text-slate-800">废液桶详情</h2>
            <p class="text-slate-500 text-sm mt-1">扫码后显示废液桶信息</p>
          </div>

          <div class="p-6">
            <div v-if="scannedWaste" class="space-y-4">
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item label="桶号">
                  <span class="font-bold">{{ scannedWaste.bucketNo }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="类别">
                  <el-tag type="primary">{{ getCategoryLabel(scannedWaste.category) }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="重量">{{ scannedWaste.weight }} kg</el-descriptions-item>
                <el-descriptions-item label="特殊成分">
                  {{ scannedWaste.hasSpecialIngredients ? '有' : '无' }}
                </el-descriptions-item>
                <el-descriptions-item label="实验名称">
                  {{ scannedWaste.experimentName }}
                </el-descriptions-item>
                <el-descriptions-item label="实验室">
                  {{ scannedWaste.labRoom }}
                </el-descriptions-item>
                <el-descriptions-item label="学生">
                  {{ scannedWaste.studentName }}
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="getStatusType(scannedWaste.status)">
                    {{ getStatusLabel(scannedWaste.status) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="入库时间">
                  {{ formatTime(scannedWaste.storedAt) }}
                </el-descriptions-item>
              </el-descriptions>

              <div v-if="scannedWaste.status === 'IN_STORAGE'" class="pt-2">
                <el-button
                  type="success"
                  size="large"
                  style="width: 100%"
                  @click="confirmCollect"
                  :loading="collecting"
                >
                  确认回收
                </el-button>
              </div>

              <div v-else class="text-center py-4">
                <el-tag type="info" size="large">
                  当前状态：{{ getStatusLabel(scannedWaste.status) }}
                </el-tag>
                <p class="text-slate-400 text-sm mt-2">仅在暂存间状态可回收</p>
              </div>
            </div>

            <el-empty v-else description="请扫描二维码查看详情" />
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center">
          <div>
            <h2 class="text-xl font-bold text-slate-800">待回收列表</h2>
            <p class="text-slate-500 text-sm mt-1">危废暂存间内的废液桶</p>
          </div>
          <el-button @click="fetchPendingList" :loading="loadingList">刷新</el-button>
        </div>

        <div class="p-6">
          <el-table
            :data="pendingList"
            v-loading="loadingList"
            style="width: 100%"
            :header-cell-style="{ background: '#f8fafc', color: '#64748b' }"
          >
            <el-table-column prop="bucketNo" label="桶号" width="140" />
            <el-table-column prop="category" label="类别" width="120">
              <template #default="{ row }">
                {{ getCategoryLabel(row.category) }}
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="重量(kg)" width="100" />
            <el-table-column prop="labRoom" label="实验室" width="120" />
            <el-table-column prop="storageOperatorName" label="入库员" width="100" />
            <el-table-column prop="storedAt" label="入库时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.storedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="success" size="small" @click="quickSelect(row)">
                  扫码回收
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
import AppLayout from '@/components/AppLayout.vue'
import {
  wasteBucketApi,
  getCategoryLabel,
  getStatusLabel,
  getStatusType
} from '@/api/wasteBucket'

const scanning = ref(false)
const collecting = ref(false)
const loadingList = ref(false)
const scannedWaste = ref(null)
const pendingList = ref([])

const scanForm = reactive({
  qrCode: '',
  recyclerId: 'REC001',
  recyclerName: '孙回收'
})

const fetchPendingList = async () => {
  loadingList.value = true
  try {
    const res = await wasteBucketApi.getPendingCollection()
    pendingList.value = res
  } catch (err) {
    console.error(err)
  } finally {
    loadingList.value = false
  }
}

const handleScan = async () => {
  if (!scanForm.qrCode) {
    ElMessage.warning('请输入二维码')
    return
  }

  scanning.value = true
  try {
    const res = await wasteBucketApi.getByQrCode(scanForm.qrCode)
    scannedWaste.value = res
    ElMessage.success('查询成功')
  } catch (err) {
    scannedWaste.value = null
    console.error(err)
  } finally {
    scanning.value = false
  }
}

const quickSelect = (item) => {
  scanForm.qrCode = item.qrCode
  scannedWaste.value = item
}

const confirmCollect = async () => {
  if (!scanForm.recyclerId || !scanForm.recyclerName) {
    ElMessage.warning('请输入回收人员信息')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认回收废液桶 ${scannedWaste.value.bucketNo}？`,
      '回收确认',
      {
        confirmButtonText: '确认回收',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }

  collecting.value = true
  try {
    await wasteBucketApi.collectWaste({
      qrCode: scanForm.qrCode,
      recyclerId: scanForm.recyclerId,
      recyclerName: scanForm.recyclerName
    })
    ElMessage.success('回收成功')
    scannedWaste.value = null
    scanForm.qrCode = ''
    fetchPendingList()
  } catch (err) {
    console.error(err)
  } finally {
    collecting.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchPendingList()
})
</script>
