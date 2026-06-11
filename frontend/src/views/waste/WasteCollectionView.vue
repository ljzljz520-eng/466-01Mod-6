<template>
  <AppLayout>
    <div class="max-w-6xl mx-auto space-y-6">
      <div class="grid gap-6 grid-cols-1 md:grid-cols-2">
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <div class="p-6 border-b border-slate-100">
            <h2 class="text-xl font-bold text-slate-800">扫码回收</h2>
            <p class="text-slate-500 text-sm mt-1">使用摄像头扫描废液桶二维码</p>
          </div>

          <div class="p-6 space-y-4">
            <div class="flex gap-2">
              <el-radio-group v-model="scanMode" size="small">
                <el-radio-button value="camera">
                  <el-icon><Camera /></el-icon>
                  <span class="ml-1">摄像头扫码</span>
                </el-radio-button>
                <el-radio-button value="input">
                  <el-icon><Edit /></el-icon>
                  <span class="ml-1">手动输入</span>
                </el-radio-button>
                <el-radio-button value="upload">
                  <el-icon><Picture /></el-icon>
                  <span class="ml-1">上传图片</span>
                </el-radio-button>
              </el-radio-group>
            </div>

            <div v-if="scanMode === 'camera'" class="space-y-4">
              <div class="relative aspect-video bg-slate-900 rounded-lg overflow-hidden">
                <video
                  ref="videoRef"
                  class="w-full h-full object-cover"
                  autoplay
                  playsinline
                  muted
                ></video>
                <div class="absolute inset-0 pointer-events-none">
                  <div class="absolute inset-0 flex items-center justify-center">
                    <div class="w-48 h-48 border-2 border-green-400 rounded-lg relative">
                      <div class="absolute -top-1 -left-1 w-6 h-6 border-t-4 border-l-4 border-green-400 rounded-tl"></div>
                      <div class="absolute -top-1 -right-1 w-6 h-6 border-t-4 border-r-4 border-green-400 rounded-tr"></div>
                      <div class="absolute -bottom-1 -left-1 w-6 h-6 border-b-4 border-l-4 border-green-400 rounded-bl"></div>
                      <div class="absolute -bottom-1 -right-1 w-6 h-6 border-b-4 border-r-4 border-green-400 rounded-br"></div>
                      <div class="scan-line"></div>
                    </div>
                  </div>
                </div>
                <canvas ref="scanCanvas" class="hidden"></canvas>
              </div>

              <div class="flex gap-2">
                <el-button
                  v-if="!cameraActive"
                  type="primary"
                  style="flex: 1"
                  @click="startCamera"
                  :loading="cameraLoading"
                >
                  <el-icon><VideoCamera /></el-icon>
                  <span class="ml-1">开启摄像头</span>
                </el-button>
                <el-button
                  v-else
                  type="danger"
                  style="flex: 1"
                  @click="stopCamera"
                >
                  <el-icon><VideoPause /></el-icon>
                  <span class="ml-1">关闭摄像头</span>
                </el-button>
              </div>

              <div v-if="cameraActive" class="text-center">
                <el-tag type="success" effect="dark" size="small">
                  <span class="inline-block h-2 w-2 rounded-full bg-green-300 animate-pulse mr-1"></span>
                  扫描中，请将二维码对准框内
                </el-tag>
              </div>
            </div>

            <div v-else-if="scanMode === 'input'" class="space-y-4">
              <div class="flex items-center gap-3 p-4 bg-blue-50 rounded-lg border border-blue-100">
                <el-icon :size="24" color="#3b82f6">
                  <Edit />
                </el-icon>
                <div>
                  <p class="text-sm font-medium text-blue-900">手动输入模式</p>
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

            <div v-else class="space-y-4">
              <div class="flex items-center gap-3 p-4 bg-purple-50 rounded-lg border border-purple-100">
                <el-icon :size="24" color="#9333ea">
                  <Picture />
                </el-icon>
                <div>
                  <p class="text-sm font-medium text-purple-900">上传图片识别</p>
                  <p class="text-xs text-purple-600">上传包含二维码的图片进行识别</p>
                </div>
              </div>

              <el-form :model="scanForm" label-width="100px">
                <el-form-item label="回收人员ID">
                  <el-input v-model="scanForm.recyclerId" placeholder="请输入回收人员ID" />
                </el-form-item>
                <el-form-item label="回收人员姓名">
                  <el-input v-model="scanForm.recyclerName" placeholder="请输入回收人员姓名" />
                </el-form-item>
              </el-form>

              <el-upload
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                accept="image/*"
                :on-change="handleImageUpload"
                drag
              >
                <el-icon class="el-icon--upload" :size="40">
                  <UploadFilled />
                </el-icon>
                <div class="el-upload__text mt-2">
                  点击或拖拽图片到此处识别二维码
                </div>
              </el-upload>

              <div v-if="uploadPreview" class="mt-4">
                <p class="text-sm text-slate-500 mb-2">上传预览：</p>
                <img
                  :src="uploadPreview"
                  class="max-w-full max-h-48 rounded-lg border border-slate-200 mx-auto"
                />
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
import { ref, reactive, onMounted, onBeforeUnmount, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Camera,
  Edit,
  Picture,
  VideoCamera,
  VideoPause,
  UploadFilled
} from '@element-plus/icons-vue'
import jsQR from 'jsqr'
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
const scanMode = ref('camera')

const cameraActive = ref(false)
const cameraLoading = ref(false)
const videoRef = ref(null)
const scanCanvas = ref(null)
const streamRef = ref(null)
const animationRef = ref(null)
const lastScanTime = ref(0)

const uploadPreview = ref(null)

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

const handleScanSuccess = async (qrCode) => {
  if (!qrCode) return
  scanForm.qrCode = qrCode
  scanning.value = true
  try {
    const res = await wasteBucketApi.getByQrCode(qrCode)
    scannedWaste.value = res
    ElMessage.success('查询成功')
  } catch (err) {
    scannedWaste.value = null
    console.error(err)
  } finally {
    scanning.value = false
  }
}

const handleScan = async () => {
  if (!scanForm.qrCode) {
    ElMessage.warning('请输入二维码')
    return
  }
  await handleScanSuccess(scanForm.qrCode)
}

const quickSelect = (item) => {
  scanForm.qrCode = item.qrCode
  scannedWaste.value = item
}

const startCamera = async () => {
  cameraLoading.value = true
  try {
    const constraints = {
      video: {
        facingMode: { ideal: 'environment' },
        width: { ideal: 1280 },
        height: { ideal: 720 }
      }
    }
    streamRef.value = await navigator.mediaDevices.getUserMedia(constraints)
    if (videoRef.value) {
      videoRef.value.srcObject = streamRef.value
      await videoRef.value.play()
    }
    cameraActive.value = true
    scanLoop()
  } catch (err) {
    console.error('无法访问摄像头:', err)
    ElMessage.error('无法访问摄像头，请检查权限或使用手动输入模式')
    scanMode.value = 'input'
  } finally {
    cameraLoading.value = false
  }
}

const stopCamera = () => {
  if (animationRef.value) {
    cancelAnimationFrame(animationRef.value)
    animationRef.value = null
  }
  if (streamRef.value) {
    streamRef.value.getTracks().forEach(track => track.stop())
    streamRef.value = null
  }
  if (videoRef.value) {
    videoRef.value.srcObject = null
  }
  cameraActive.value = false
}

const scanLoop = () => {
  if (!cameraActive.value) return

  if (videoRef.value && scanCanvas.value) {
    const video = videoRef.value
    const canvas = scanCanvas.value
    const ctx = canvas.getContext('2d')

    if (video.readyState === video.HAVE_ENOUGH_DATA) {
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height)

      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
      const code = jsQR(imageData.data, imageData.width, imageData.height, {
        inversionAttempts: 'dontInvert'
      })

      if (code && Date.now() - lastScanTime.value > 2000) {
        lastScanTime.value = Date.now()
        ElMessage.success('扫描成功')
        stopCamera()
        handleScanSuccess(code.data)
        return
      }
    }
  }

  animationRef.value = requestAnimationFrame(scanLoop)
}

const handleImageUpload = (uploadFile) => {
  const file = uploadFile.raw
  if (!file) return

  const reader = new FileReader()
  reader.onload = (e) => {
    const img = new Image()
    img.onload = () => {
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      canvas.width = img.width
      canvas.height = img.height
      ctx.drawImage(img, 0, 0)

      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
      const code = jsQR(imageData.data, imageData.width, imageData.height)

      if (code) {
        uploadPreview.value = e.target.result
        ElMessage.success('二维码识别成功')
        handleScanSuccess(code.data)
      } else {
        ElMessage.error('未识别到二维码，请尝试更清晰的图片')
      }
    }
    img.src = e.target.result
    uploadPreview.value = e.target.result
  }
  reader.readAsDataURL(file)
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
    uploadPreview.value = null
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

watch(scanMode, (val) => {
  if (val !== 'camera' && cameraActive.value) {
    stopCamera()
  }
})

onMounted(() => {
  fetchPendingList()
})

onBeforeUnmount(() => {
  stopCamera()
})
</script>

<style scoped>
.ml-1 {
  margin-left: 4px;
}
.mt-2 {
  margin-top: 8px;
}

.scan-line {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #4ade80, transparent);
  animation: scanMove 2s linear infinite;
}

@keyframes scanMove {
  0% { top: 0; }
  100% { top: 100%; }
}

.hidden {
  display: none;
}

.max-h-48 {
  max-height: 12rem;
}

.aspect-video {
  aspect-ratio: 16 / 9;
}
</style>
