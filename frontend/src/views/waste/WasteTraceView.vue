<template>
  <AppLayout>
    <div class="max-w-7xl mx-auto space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100">
          <h2 class="text-xl font-bold text-slate-800">废液追踪查询</h2>
          <p class="text-slate-500 text-sm mt-1">
            支持多条件查询，可追溯每桶废液的完整流转记录和责任人
          </p>
        </div>

        <div class="p-6">
          <el-form :model="queryForm" inline class="mb-4">
            <el-form-item label="桶号">
              <el-input v-model="queryForm.bucketNo" placeholder="请输入桶号" clearable />
            </el-form-item>
            <el-form-item label="类别">
              <el-select
                v-model="queryForm.category"
                placeholder="全部类别"
                clearable
                style="width: 140px"
              >
                <el-option
                  v-for="item in wasteCategoryOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select
                v-model="queryForm.status"
                placeholder="全部状态"
                clearable
                style="width: 140px"
              >
                <el-option
                  v-for="item in wasteStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="学生">
              <el-input v-model="queryForm.studentName" placeholder="姓名或学号" clearable />
            </el-form-item>
            <el-form-item label="实验">
              <el-input v-model="queryForm.experimentName" placeholder="实验名称" clearable />
            </el-form-item>
            <el-form-item label="实验室">
              <el-input v-model="queryForm.labRoom" placeholder="房间号" clearable />
            </el-form-item>
            <el-form-item label="类别修正">
              <el-select
                v-model="queryForm.categoryCorrected"
                placeholder="全部"
                clearable
                style="width: 120px"
              >
                <el-option label="已修正" :value="true" />
                <el-option label="未修正" :value="false" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center">
          <div>
            <h3 class="font-bold text-slate-800">查询结果</h3>
            <p class="text-slate-500 text-sm">共 {{ total }} 条记录</p>
          </div>
        </div>

        <div class="overflow-x-auto">
          <el-table
            :data="tableData"
            v-loading="loading"
            style="width: 100%"
            :header-cell-style="{ background: '#f8fafc', color: '#64748b' }"
          >
            <el-table-column prop="bucketNo" label="桶号" width="140" fixed="left">
              <template #default="{ row }">
                <span class="font-medium text-blue-600">{{ row.bucketNo }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="类别" width="110">
              <template #default="{ row }">
                <el-tag size="small">
                  {{ getCategoryLabel(row.category) }}
                </el-tag>
                <el-tag
                  v-if="row.categoryCorrectionRemark"
                  type="warning"
                  size="small"
                  class="ml-1"
                >
                  已修正
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="重量(kg)" width="90" />
            <el-table-column prop="hasSpecialIngredients" label="特殊成分" width="90">
              <template #default="{ row }">
                <el-tag :type="row.hasSpecialIngredients ? 'warning' : 'info'" size="small">
                  {{ row.hasSpecialIngredients ? '有' : '无' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="experimentName" label="实验名称" min-width="140" />
            <el-table-column prop="labRoom" label="实验室" width="110" />
            <el-table-column prop="studentName" label="学生" width="90">
              <template #default="{ row }">
                <el-tooltip :content="`学号：${row.studentId}`" placement="top">
                  <span>{{ row.studentName }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submittedAt" label="提交时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.submittedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="viewDetail(row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="p-4 border-t border-slate-100 flex justify-end">
          <el-pagination
            v-model:current-page="queryForm.pageNum"
            v-model:page-size="queryForm.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleQuery"
            @current-change="handleQuery"
          />
        </div>
      </div>

      <el-dialog v-model="showDetail" title="废液桶详情" width="700px">
        <div v-if="currentWaste" class="space-y-4">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="桶号" :span="2">
              <span class="font-bold text-lg">{{ currentWaste.bucketNo }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="二维码" :span="2">
              {{ currentWaste.qrCode }}
            </el-descriptions-item>
            <el-descriptions-item label="当前类别">
              <el-tag type="primary">{{ getCategoryLabel(currentWaste.category) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="重量">
              {{ currentWaste.weight }} kg
            </el-descriptions-item>
            <el-descriptions-item label="特殊成分">
              <el-tag :type="currentWaste.hasSpecialIngredients ? 'warning' : 'info'">
                {{ currentWaste.hasSpecialIngredients ? '有' : '无' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentWaste.status)">
                {{ getStatusLabel(currentWaste.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">实验信息</el-divider>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="实验名称">
              {{ currentWaste.experimentName }}
            </el-descriptions-item>
            <el-descriptions-item label="实验时间">
              {{ formatTime(currentWaste.experimentTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="实验室房间">
              {{ currentWaste.labRoom }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">学生信息（责任人）</el-divider>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="学号">
              {{ currentWaste.studentId }}
            </el-descriptions-item>
            <el-descriptions-item label="姓名">
              {{ currentWaste.studentName }}
            </el-descriptions-item>
            <el-descriptions-item label="联系方式" :span="2">
              {{ currentWaste.studentContact || '-' }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">流转记录</el-divider>
          <el-timeline>
            <el-timeline-item
              v-if="currentWaste.submittedAt"
              :timestamp="formatTime(currentWaste.submittedAt)"
              placement="top"
              type="warning"
            >
              <div class="font-medium">学生提交</div>
              <div class="text-sm text-slate-500">
                {{ currentWaste.studentName }} 提交了废液桶
              </div>
            </el-timeline-item>

            <el-timeline-item
              v-if="currentWaste.safetyCheckedAt"
              :timestamp="formatTime(currentWaste.safetyCheckedAt)"
              placement="top"
              type="primary"
            >
              <div class="font-medium">安全员检查</div>
              <div class="text-sm text-slate-500">
                {{ currentWaste.safetyOfficerName }} 检查完成
              </div>
              <div v-if="currentWaste.safetyRemark" class="text-sm text-slate-500">
                备注：{{ currentWaste.safetyRemark }}
              </div>
              <div v-if="currentWaste.categoryCorrectionRemark" class="text-sm text-orange-600 mt-1">
                ⚠ 类别修正：{{ currentWaste.categoryCorrectionRemark }}
              </div>
            </el-timeline-item>

            <el-timeline-item
              v-if="currentWaste.storedAt"
              :timestamp="formatTime(currentWaste.storedAt)"
              placement="top"
              type="info"
            >
              <div class="font-medium">入库危废暂存间</div>
              <div class="text-sm text-slate-500">
                操作员：{{ currentWaste.storageOperatorName }}
              </div>
            </el-timeline-item>

            <el-timeline-item
              v-if="currentWaste.collectedAt"
              :timestamp="formatTime(currentWaste.collectedAt)"
              placement="top"
              type="success"
            >
              <div class="font-medium">第三方回收</div>
              <div class="text-sm text-slate-500">
                回收人员：{{ currentWaste.recyclerName }}
              </div>
            </el-timeline-item>
          </el-timeline>

          <div v-if="currentWaste.specialIngredientsRemark" class="mt-4">
            <el-alert
              title="特殊成分说明"
              :description="currentWaste.specialIngredientsRemark"
              type="warning"
              :closable="false"
              show-icon
            />
          </div>
        </div>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/AppLayout.vue'
import {
  wasteBucketApi,
  wasteCategoryOptions,
  wasteStatusOptions,
  getCategoryLabel,
  getStatusLabel,
  getStatusType
} from '@/api/wasteBucket'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const showDetail = ref(false)
const currentWaste = ref(null)

const queryForm = reactive({
  bucketNo: '',
  category: '',
  status: '',
  studentId: '',
  studentName: '',
  experimentName: '',
  labRoom: '',
  categoryCorrected: null,
  pageNum: 1,
  pageSize: 20
})

const handleQuery = async () => {
  loading.value = true
  try {
    const data = {
      bucketNo: queryForm.bucketNo || null,
      category: queryForm.category || null,
      status: queryForm.status || null,
      studentId: queryForm.studentName || null,
      studentName: queryForm.studentName || null,
      experimentName: queryForm.experimentName || null,
      labRoom: queryForm.labRoom || null,
      categoryCorrected: queryForm.categoryCorrected,
      pageNum: queryForm.pageNum,
      pageSize: queryForm.pageSize
    }
    const res = await wasteBucketApi.queryWaste(data)
    tableData.value = res.content
    total.value = res.totalElements
  } catch (err) {
    console.error(err)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.bucketNo = ''
  queryForm.category = ''
  queryForm.status = ''
  queryForm.studentId = ''
  queryForm.studentName = ''
  queryForm.experimentName = ''
  queryForm.labRoom = ''
  queryForm.categoryCorrected = null
  queryForm.pageNum = 1
  queryForm.pageSize = 20
  handleQuery()
}

const viewDetail = async (row) => {
  try {
    const res = await wasteBucketApi.getById(row.id)
    currentWaste.value = res
    showDetail.value = true
  } catch (err) {
    console.error(err)
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.ml-1 {
  margin-left: 4px;
}
</style>
