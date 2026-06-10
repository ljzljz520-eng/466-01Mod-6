<template>
  <AppLayout>
    <div class="max-w-6xl mx-auto space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center">
          <div>
            <h2 class="text-xl font-bold text-slate-800">危废暂存间入库</h2>
            <p class="text-slate-500 text-sm mt-1">待入库废液桶列表</p>
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
                <el-tag size="small">
                  {{ getCategoryLabel(row.category) }}
                </el-tag>
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
            <el-table-column prop="safetyOfficerName" label="安全员" width="100" />
            <el-table-column prop="safetyCheckedAt" label="检查时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.safetyCheckedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="success" size="small" @click="handleStore(row)">
                  入库
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="!loading && wasteList.length === 0" description="暂无待入库的废液桶" />
        </div>
      </div>

      <el-dialog v-model="showStoreDialog" title="入库确认" width="400px">
        <div v-if="currentWaste" class="space-y-4">
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="桶号">{{ currentWaste.bucketNo }}</el-descriptions-item>
            <el-descriptions-item label="类别">
              {{ getCategoryLabel(currentWaste.category) }}
            </el-descriptions-item>
            <el-descriptions-item label="重量">{{ currentWaste.weight }} kg</el-descriptions-item>
            <el-descriptions-item label="安全员">
              {{ currentWaste.safetyOfficerName }}
            </el-descriptions-item>
          </el-descriptions>

          <el-form :model="storeForm" label-width="100px" class="mt-4">
            <el-form-item label="操作员ID">
              <el-input v-model="storeForm.operatorId" placeholder="请输入操作员ID" />
            </el-form-item>
            <el-form-item label="操作员姓名">
              <el-input v-model="storeForm.operatorName" placeholder="请输入操作员姓名" />
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <el-button @click="showStoreDialog = false">取消</el-button>
          <el-button type="success" @click="submitStore" :loading="submitting">
            确认入库
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
import { wasteBucketApi, getCategoryLabel } from '@/api/wasteBucket'

const loading = ref(false)
const submitting = ref(false)
const wasteList = ref([])
const showStoreDialog = ref(false)
const currentWaste = ref(null)

const storeForm = reactive({
  bucketId: null,
  operatorId: '',
  operatorName: ''
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await wasteBucketApi.getPendingStorage()
    wasteList.value = res
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const handleStore = (row) => {
  currentWaste.value = row
  storeForm.bucketId = row.id
  storeForm.operatorId = 'STOR001'
  storeForm.operatorName = '王保管'
  showStoreDialog.value = true
}

const submitStore = async () => {
  if (!storeForm.operatorId || !storeForm.operatorName) {
    ElMessage.warning('请输入操作员信息')
    return
  }

  submitting.value = true
  try {
    await wasteBucketApi.storeWaste({
      bucketId: storeForm.bucketId,
      operatorId: storeForm.operatorId,
      operatorName: storeForm.operatorName
    })
    ElMessage.success('入库成功')
    showStoreDialog.value = false
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
