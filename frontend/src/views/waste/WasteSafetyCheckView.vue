<template>
  <AppLayout>
    <div class="max-w-6xl mx-auto space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center">
          <div>
            <h2 class="text-xl font-bold text-slate-800">安全员检查</h2>
            <p class="text-slate-500 text-sm mt-1">待检查废液桶列表</p>
          </div>
          <el-button type="primary" @click="fetchList" :loading="loading">
            刷新
          </el-button>
        </div>

        <div class="p-6">
          <el-table
            :data="wasteList"
            v-loading="loading"
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
            <el-table-column prop="hasSpecialIngredients" label="特殊成分" width="100">
              <template #default="{ row }">
                <el-tag :type="row.hasSpecialIngredients ? 'warning' : 'info'" size="small">
                  {{ row.hasSpecialIngredients ? '有' : '无' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="experimentName" label="实验名称" min-width="150" />
            <el-table-column prop="labRoom" label="实验室" width="120" />
            <el-table-column prop="studentName" label="提交人" width="100" />
            <el-table-column prop="submittedAt" label="提交时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.submittedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="openCheckDialog(row)">
                  检查
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="!loading && wasteList.length === 0" description="暂无待检查的废液桶" />
        </div>
      </div>

      <el-dialog v-model="showCheckDialog" title="安全检查" width="600px">
        <div v-if="currentWaste" class="space-y-4">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="桶号">{{ currentWaste.bucketNo }}</el-descriptions-item>
            <el-descriptions-item label="当前类别">
              {{ getCategoryLabel(currentWaste.category) }}
            </el-descriptions-item>
            <el-descriptions-item label="重量">{{ currentWaste.weight }} kg</el-descriptions-item>
            <el-descriptions-item label="特殊成分">
              {{ currentWaste.hasSpecialIngredients ? '有' : '无' }}
            </el-descriptions-item>
            <el-descriptions-item label="实验名称">{{ currentWaste.experimentName }}</el-descriptions-item>
            <el-descriptions-item label="实验室">{{ currentWaste.labRoom }}</el-descriptions-item>
            <el-descriptions-item label="学生">
              {{ currentWaste.studentName }} ({{ currentWaste.studentId }})
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">
              {{ formatTime(currentWaste.submittedAt) }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider />

          <el-form :model="checkForm" label-width="120px">
            <el-form-item label="检查结果">
              <el-radio-group v-model="checkForm.passed">
                <el-radio :value="true">通过</el-radio>
                <el-radio :value="false">不通过</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item v-if="checkForm.passed" label="修正类别">
              <el-select
                v-model="checkForm.correctedCategory"
                placeholder="如类别正确则不填"
                style="width: 100%"
                clearable
              >
                <el-option
                  v-for="item in wasteCategoryOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <div class="text-xs text-slate-400 mt-1">
                  如果学生分类正确，请选择正确的类别
                </div>
            </el-form-item>

            <el-form-item v-if="checkForm.correctedCategory" label="修正说明">
              <el-input
                v-model="checkForm.correctionRemark"
                type="textarea"
                :rows="2"
                placeholder="请说明修正原因"
              />
            </el-form-item>

            <el-form-item label="检查备注">
              <el-input
                v-model="checkForm.remark"
                type="textarea"
                :rows="2"
                placeholder="请输入检查备注"
              />
            </el-form-item>

            <el-form-item label="安全员ID">
              <el-input v-model="checkForm.officerId" placeholder="请输入安全员ID" />
            </el-form-item>

            <el-form-item label="安全员姓名">
              <el-input v-model="checkForm.officerName" placeholder="请输入安全员姓名" />
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <el-button @click="showCheckDialog = false">取消</el-button>
          <el-button type="primary" @click="submitCheck" :loading="submitting">
            确认检查
          </el-button>
        </template>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/AppLayout.vue'
import { wasteBucketApi, wasteCategoryOptions, getCategoryLabel } from '@/api/wasteBucket'

const loading = ref(false)
const submitting = ref(false)
const wasteList = ref([])
const showCheckDialog = ref(false)
const currentWaste = ref(null)

const checkForm = reactive({
  bucketId: null,
  officerId: '',
  officerName: '',
  passed: true,
  correctedCategory: '',
  correctionRemark: '',
  remark: ''
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await wasteBucketApi.getPendingSafetyCheck()
    wasteList.value = res
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const openCheckDialog = (row) => {
  currentWaste.value = row
  checkForm.bucketId = row.id
  checkForm.officerId = 'SAFE001'
  checkForm.officerName = '李安全'
  checkForm.passed = true
  checkForm.correctedCategory = ''
  checkForm.correctionRemark = ''
  checkForm.remark = ''
  showCheckDialog.value = true
}

const submitCheck = async () => {
  if (!checkForm.officerId || !checkForm.officerName) {
    ElMessage.warning('请输入安全员信息')
    return
  }

  submitting.value = true
  try {
    const data = {
      bucketId: checkForm.bucketId,
      officerId: checkForm.officerId,
      officerName: checkForm.officerName,
      passed: checkForm.passed,
      correctedCategory: checkForm.correctedCategory || null,
      correctionRemark: checkForm.correctionRemark || null,
      remark: checkForm.remark || null
    }
    await wasteBucketApi.safetyCheck(data)
    ElMessage.success('检查完成')
    showCheckDialog.value = false
    fetchList()
  } catch (err) {
    console.error(err)
  } finally {
    submitting.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchList()
})
</script>
