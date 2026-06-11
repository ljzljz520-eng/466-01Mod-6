<template>
  <AppLayout>
    <div class="max-w-3xl mx-auto">
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100">
          <h2 class="text-xl font-bold text-slate-800">实验废液提交</h2>
          <p class="text-slate-500 text-sm mt-1">请填写废液桶信息，提交后等待安全员检查</p>
        </div>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="p-6"
        >
          <el-divider content-position="left">废液桶信息</el-divider>

          <el-form-item label="桶号" prop="bucketNo">
            <el-input v-model="formData.bucketNo" placeholder="请输入废液桶编号" />
          </el-form-item>

          <el-form-item label="废液类别" prop="category">
            <el-select v-model="formData.category" placeholder="请选择废液类别" style="width: 100%">
              <el-option
                v-for="item in wasteCategoryOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="重量(kg)" prop="weight">
            <el-input-number
              v-model="formData.weight"
              :min="0.01"
              :precision="2"
              :step="0.1"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="含特殊成分" prop="hasSpecialIngredients">
            <el-radio-group v-model="formData.hasSpecialIngredients">
              <el-radio :value="true">是</el-radio>
              <el-radio :value="false">否</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item
            v-if="formData.hasSpecialIngredients"
            label="特殊成分说明"
            prop="specialIngredientsRemark"
          >
            <el-input
              v-model="formData.specialIngredientsRemark"
              type="textarea"
              :rows="3"
              placeholder="请说明特殊成分"
            />
          </el-form-item>

          <el-divider content-position="left">实验信息</el-divider>

          <el-form-item label="实验名称" prop="experimentName">
            <el-input v-model="formData.experimentName" placeholder="请输入实验名称" />
          </el-form-item>

          <el-form-item label="实验时间" prop="experimentTime">
            <el-date-picker
              v-model="formData.experimentTime"
              type="datetime"
              placeholder="选择实验时间"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="实验室房间" prop="labRoom">
            <el-input v-model="formData.labRoom" placeholder="请输入实验室房间号" />
          </el-form-item>

          <el-divider content-position="left">学生信息</el-divider>

          <el-form-item label="学号" prop="studentId">
            <el-input v-model="formData.studentId" placeholder="请输入学号" />
          </el-form-item>

          <el-form-item label="姓名" prop="studentName">
            <el-input v-model="formData.studentName" placeholder="请输入姓名" />
          </el-form-item>

          <el-form-item label="联系方式" prop="studentContact">
            <el-input v-model="formData.studentContact" placeholder="请输入手机号" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              提交废液
            </el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-dialog v-model="showSuccess" title="提交成功" width="520px">
        <div class="text-center py-2">
          <el-icon :size="40" color="#67c23a" class="mb-2">
            <CircleCheck />
          </el-icon>
          <p class="text-slate-700 font-medium text-lg">废液桶信息已提交</p>

          <div id="qrcode-print-area">
            <div class="label-card mt-4 mx-auto" id="label-card">
              <div class="label-header">
                <h3 class="label-title">实验废液桶标签</h3>
              </div>

              <div class="label-qrcode mt-3">
                <canvas ref="qrcodeCanvas"></canvas>
              </div>

              <div class="label-info mt-3">
                <div class="info-row">
                  <span class="info-label">桶号：</span>
                  <span class="info-value font-bold">{{ successData.bucketNo }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">类别：</span>
                  <span class="info-value">{{ getCategoryLabel(successData.category) }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">重量：</span>
                  <span class="info-value">{{ successData.weight }} kg</span>
                </div>
                <div class="info-row">
                  <span class="info-label">特殊成分：</span>
                  <span class="info-value">{{ successData.hasSpecialIngredients ? '有' : '无' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">实验：</span>
                  <span class="info-value">{{ successData.experimentName }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">实验室：</span>
                  <span class="info-value">{{ successData.labRoom }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">学生：</span>
                  <span class="info-value">{{ successData.studentName }} ({{ successData.studentId }})</span>
                </div>
                <div class="info-row">
                  <span class="info-label">提交：</span>
                  <span class="info-value text-xs">{{ formatTime(successData.submittedAt) }}</span>
                </div>
              </div>

              <div class="label-footer mt-3">
                <p class="qr-string">QR: {{ successData.qrCode }}</p>
              </div>
            </div>
          </div>

          <p class="text-slate-400 text-xs mt-3">
            请打印或下载标签贴在废液桶上，等待安全员检查
          </p>
        </div>
        <template #footer>
          <div class="flex justify-between w-full">
            <div>
              <el-button @click="handleDownload" type="success" plain>
                <el-icon><Download /></el-icon>
                <span class="ml-1">下载标签</span>
              </el-button>
              <el-button @click="handlePrint" type="primary" plain>
                <el-icon><Printer /></el-icon>
                <span class="ml-1">打印标签</span>
              </el-button>
            </div>
            <el-button type="primary" @click="handleContinue">继续提交</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, Download, Printer } from '@element-plus/icons-vue'
import QRCode from 'qrcode'
import AppLayout from '@/components/AppLayout.vue'
import { wasteBucketApi, wasteCategoryOptions, getCategoryLabel } from '@/api/wasteBucket'

const formRef = ref(null)
const submitting = ref(false)
const showSuccess = ref(false)
const successData = ref({})
const qrcodeCanvas = ref(null)

const formData = reactive({
  bucketNo: '',
  category: '',
  weight: 1,
  hasSpecialIngredients: false,
  specialIngredientsRemark: '',
  experimentName: '',
  experimentTime: new Date(),
  labRoom: '',
  studentId: '',
  studentName: '',
  studentContact: ''
})

const rules = {
  bucketNo: [{ required: true, message: '请输入桶号', trigger: 'blur' }],
  category: [{ required: true, message: '请选择废液类别', trigger: 'change' }],
  weight: [{ required: true, message: '请输入重量', trigger: 'blur' }],
  hasSpecialIngredients: [{ required: true, message: '请选择是否含特殊成分', trigger: 'change' }],
  experimentName: [{ required: true, message: '请输入实验名称', trigger: 'blur' }],
  experimentTime: [{ required: true, message: '请选择实验时间', trigger: 'change' }],
  labRoom: [{ required: true, message: '请输入实验室房间号', trigger: 'blur' }],
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const generateQRCode = async () => {
  if (!successData.value.qrCode || !qrcodeCanvas.value) return
  await nextTick()
  try {
    await QRCode.toCanvas(qrcodeCanvas.value, successData.value.qrCode, {
      width: 180,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#ffffff'
      }
    })
  } catch (err) {
    console.error('二维码生成失败:', err)
  }
}

watch(showSuccess, (val) => {
  if (val) {
    nextTick(() => {
      generateQRCode()
    })
  }
})

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  submitting.value = true
  try {
    const data = {
      ...formData,
      experimentTime: formData.experimentTime
        ? new Date(formData.experimentTime).toISOString()
        : null
    }
    const res = await wasteBucketApi.submitWaste(data)
    successData.value = res
    showSuccess.value = true
    ElMessage.success('提交成功')
  } catch (err) {
    console.error(err)
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  formData.experimentTime = new Date()
  formData.weight = 1
  formData.hasSpecialIngredients = false
}

const handleContinue = () => {
  showSuccess.value = false
  handleReset()
}

const handleDownload = async () => {
  if (!qrcodeCanvas.value) return
  try {
    const labelCard = document.getElementById('label-card')
    const canvas = document.createElement('canvas')
    const scale = 3
    canvas.width = labelCard.offsetWidth * scale
    canvas.height = labelCard.offsetHeight * scale
    const ctx = canvas.getContext('2d')
    ctx.scale(scale, scale)
    ctx.fillStyle = '#ffffff'
    ctx.fillRect(0, 0, labelCard.offsetWidth, labelCard.offsetHeight)
    ctx.strokeStyle = '#e5e7eb'
    ctx.lineWidth = 2
    ctx.strokeRect(1, 1, labelCard.offsetWidth - 2, labelCard.offsetHeight - 2)

    ctx.fillStyle = '#2563eb'
    ctx.font = 'bold 16px sans-serif'
    ctx.textAlign = 'center'
    ctx.fillText('实验废液桶标签', labelCard.offsetWidth / 2, 28)

    const qrCanvas = qrcodeCanvas.value
    const qrSize = 180
    const qrX = (labelCard.offsetWidth - qrSize) / 2
    ctx.drawImage(qrCanvas, qrX, 45, qrSize, qrSize)

    ctx.textAlign = 'left'
    ctx.fillStyle = '#1e293b'
    ctx.font = '12px sans-serif'
    const infoStartY = 245
    const lineHeight = 18
    const labelX = 25
    const valueX = 90
    const infos = [
      { label: '桶号：', value: successData.value.bucketNo, bold: true },
      { label: '类别：', value: getCategoryLabel(successData.value.category) },
      { label: '重量：', value: successData.value.weight + ' kg' },
      { label: '特殊成分：', value: successData.value.hasSpecialIngredients ? '有' : '无' },
      { label: '实验：', value: successData.value.experimentName },
      { label: '实验室：', value: successData.value.labRoom },
      { label: '学生：', value: `${successData.value.studentName} (${successData.value.studentId})` },
      { label: '提交：', value: formatTime(successData.value.submittedAt) }
    ]
    infos.forEach((info, idx) => {
      const y = infoStartY + idx * lineHeight
      ctx.fillStyle = '#64748b'
      ctx.font = '12px sans-serif'
      ctx.fillText(info.label, labelX, y)
      ctx.fillStyle = '#1e293b'
      ctx.font = info.bold ? 'bold 12px sans-serif' : '12px sans-serif'
      ctx.fillText(info.value, valueX, y)
    })

    ctx.fillStyle = '#94a3b8'
    ctx.font = '10px monospace'
    ctx.textAlign = 'center'
    ctx.fillText('QR: ' + successData.value.qrCode, labelCard.offsetWidth / 2, labelCard.offsetHeight - 18)

    const link = document.createElement('a')
    link.download = `废液桶标签_${successData.value.bucketNo}.png`
    link.href = canvas.toDataURL('image/png')
    link.click()
    ElMessage.success('标签已下载')
  } catch (err) {
    console.error('下载失败:', err)
    ElMessage.error('下载失败')
  }
}

const handlePrint = () => {
  const printContent = document.getElementById('qrcode-print-area')
  if (!printContent) return

  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>废液桶标签 - ${successData.value.bucketNo}</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
          display: flex;
          justify-content: center;
          align-items: center;
          min-height: 100vh;
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
          padding: 20px;
        }
        .label-card {
          width: 340px;
          border: 2px solid #333;
          border-radius: 8px;
          padding: 20px;
          text-align: center;
        }
        .label-title {
          font-size: 18px;
          font-weight: bold;
          color: #2563eb;
        }
        .label-qrcode canvas {
          display: block;
          margin: 0 auto;
        }
        .label-info {
          text-align: left;
          font-size: 12px;
        }
        .info-row {
          margin-bottom: 6px;
          display: flex;
        }
        .info-label {
          color: #666;
          min-width: 70px;
        }
        .info-value {
          color: #111;
          flex: 1;
        }
        .label-footer {
          margin-top: 12px;
          border-top: 1px dashed #ccc;
          padding-top: 10px;
        }
        .qr-string {
          font-size: 10px;
          color: #999;
          font-family: monospace;
          word-break: break-all;
        }
        @media print {
          body { padding: 0; }
        }
      </style>
    </head>
    <body>
      ${printContent.innerHTML}
    </body>
    </html>
  `)
  printWindow.document.close()

  setTimeout(() => {
    const canvas = printWindow.document.querySelector('canvas')
    if (canvas && qrcodeCanvas.value) {
      const ctx = canvas.getContext('2d')
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      ctx.drawImage(qrcodeCanvas.value, 0, 0, canvas.width, canvas.height)
    }
    printWindow.focus()
    printWindow.print()
  }, 300)
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.mb-2 {
  margin-bottom: 8px;
}
.mt-2 {
  margin-top: 8px;
}
.mt-3 {
  margin-top: 12px;
}
.mt-4 {
  margin-top: 16px;
}
.ml-1 {
  margin-left: 4px;
}

.label-card {
  width: 340px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  background: #ffffff;
}

.label-header {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 12px;
}

.label-title {
  font-size: 18px;
  font-weight: bold;
  color: #2563eb;
  margin: 0;
}

.label-qrcode {
  padding: 4px;
}

.label-info {
  text-align: left;
  font-size: 12px;
  padding: 0 8px;
}

.info-row {
  margin-bottom: 6px;
  display: flex;
}

.info-label {
  color: #64748b;
  min-width: 70px;
}

.info-value {
  color: #1e293b;
  flex: 1;
  word-break: break-all;
}

.label-footer {
  border-top: 1px dashed #e5e7eb;
  padding-top: 10px;
}

.qr-string {
  font-size: 10px;
  color: #94a3b8;
  font-family: monospace;
  word-break: break-all;
  margin: 0;
}

.w-full {
  width: 100%;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}
</style>
