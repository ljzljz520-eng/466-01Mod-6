import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import AboutView from '@/views/AboutView.vue'
import WasteSubmitView from '@/views/waste/WasteSubmitView.vue'
import WasteSafetyCheckView from '@/views/waste/WasteSafetyCheckView.vue'
import WasteStorageView from '@/views/waste/WasteStorageView.vue'
import WasteCollectionView from '@/views/waste/WasteCollectionView.vue'
import WasteTraceView from '@/views/waste/WasteTraceView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/waste/submit',
    name: 'waste-submit',
    component: WasteSubmitView
  },
  {
    path: '/waste/safety-check',
    name: 'waste-safety-check',
    component: WasteSafetyCheckView
  },
  {
    path: '/waste/storage',
    name: 'waste-storage',
    component: WasteStorageView
  },
  {
    path: '/waste/collection',
    name: 'waste-collection',
    component: WasteCollectionView
  },
  {
    path: '/waste/trace',
    name: 'waste-trace',
    component: WasteTraceView
  },
  {
    path: '/about',
    name: 'about',
    component: AboutView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
