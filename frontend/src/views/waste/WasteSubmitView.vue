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

      <el-dialog v-model="showSuccess" title="提交成功" width="400px">
        <div class="text-center py-4">
          <el-icon :size="48" color="#67c23a" class="mb-3">
            <CircleCheck />
          </el-icon>
          <p class="text-slate-700 font-medium">废液桶信息已提交</p>
          <p class="text-slate-500 text-sm mt-2">
            桶号：{{ successData.bucketNo }}
          </p>
          <p class="text-slate-500 text-sm">
            二维码：{{ successData.qrCode }}
          </p>
          <p class="text-slate-400 text-xs mt-3">
            请等待安全员检查后送入危废暂存间
          </p>
        </div>
        <template #footer>
          <el-button type="primary" @click="handleContinue">继续提交</el-button>
        </template>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'
import AppLayout from '@/components/AppLayout.vue'
import { wasteBucketApi, wasteCategoryOptions } from '@/api/wasteBucket'

const formRef = ref(null)
const submitting = ref(false)
const showSuccess = ref(false)
const successData = ref({})

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
</script>

<style scoped>
.mb-3 {
  margin-bottom: 12px;
}
</style>
