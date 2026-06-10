import api from './index'

export const wasteBucketApi = {
  submitWaste(data) {
    return api.post('/waste-buckets/submit', data)
  },

  safetyCheck(data) {
    return api.post('/waste-buckets/safety-check', data)
  },

  storeWaste(data) {
    return api.post('/waste-buckets/store', data)
  },

  collectWaste(data) {
    return api.post('/waste-buckets/collect', data)
  },

  getById(id) {
    return api.get(`/waste-buckets/${id}`)
  },

  getByBucketNo(bucketNo) {
    return api.get(`/waste-buckets/bucket-no/${bucketNo}`)
  },

  getByQrCode(qrCode) {
    return api.get(`/waste-buckets/qr-code/${qrCode}`)
  },

  queryWaste(data) {
    return api.post('/waste-buckets/query', data)
  },

  getByStatus(status) {
    return api.get(`/waste-buckets/status/${status}`)
  },

  getByStudentId(studentId) {
    return api.get(`/waste-buckets/student/${studentId}`)
  },

  getStatistics() {
    return api.get('/waste-buckets/statistics')
  },

  getPendingSafetyCheck() {
    return api.get('/waste-buckets/pending/safety-check')
  },

  getPendingStorage() {
    return api.get('/waste-buckets/pending/storage')
  },

  getPendingCollection() {
    return api.get('/waste-buckets/pending/collection')
  }
}

export const wasteCategoryOptions = [
  { value: 'ACID', label: '酸性废液' },
  { value: 'ALKALI', label: '碱性废液' },
  { value: 'ORGANIC', label: '有机废液' },
  { value: 'HEAVY_METAL', label: '重金属废液' },
  { value: 'TOXIC', label: '有毒废液' },
  { value: 'OXIDIZING', label: '氧化性废液' },
  { value: 'REDUCING', label: '还原性废液' },
  { value: 'OTHER', label: '其他废液' }
]

export const wasteStatusOptions = [
  { value: 'SUBMITTED', label: '学生已提交', type: 'warning' },
  { value: 'SAFETY_CHECKED', label: '安全员已检查', type: 'primary' },
  { value: 'IN_STORAGE', label: '已入危废暂存间', type: 'info' },
  { value: 'COLLECTED', label: '已回收', type: 'success' },
  { value: 'CANCELLED', label: '已取消', type: 'danger' }
]

export function getStatusLabel(status) {
  const item = wasteStatusOptions.find(s => s.value === status)
  return item ? item.label : status
}

export function getStatusType(status) {
  const item = wasteStatusOptions.find(s => s.value === status)
  return item ? item.type : 'info'
}

export function getCategoryLabel(category) {
  const item = wasteCategoryOptions.find(c => c.value === category)
  return item ? item.label : category
}
