<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans text-slate-800">
    <header class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex justify-between items-center">
        <div class="flex items-center gap-3">
          <div class="h-8 w-8 bg-blue-600 rounded-lg flex items-center justify-center text-white font-bold text-lg">
            S
          </div>
          <h1 class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-indigo-600">
            Sky-Lark
          </h1>
        </div>
        <nav class="flex items-center gap-1">
          <router-link
            to="/"
            class="nav-link"
            active-class="nav-link-active"
          >
            首页
          </router-link>

          <el-dropdown trigger="click">
            <span class="nav-link cursor-pointer flex items-center gap-1">
              废液管理
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <router-link to="/waste/submit" class="dropdown-link">
                    废液提交
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/waste/safety-check" class="dropdown-link">
                    安全员检查
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/waste/storage" class="dropdown-link">
                    暂存间入库
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/waste/collection" class="dropdown-link">
                    第三方回收
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <router-link to="/waste/trace" class="dropdown-link">
                    追踪查询
                  </router-link>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <router-link
            to="/about"
            class="nav-link"
            active-class="nav-link-active"
          >
            关于我们
          </router-link>
        </nav>
      </div>
    </header>
    <main class="flex-grow container mx-auto p-4 sm:p-6 lg:p-8 animate-fade-in">
      <slot></slot>
    </main>
    <footer class="bg-white border-t border-slate-100 py-6 text-center text-slate-400 text-sm">
      <p>&copy; 2026 Sky-Lark 项目框架，为开发者打造 <span class="text-red-400">❤</span></p>
    </footer>
  </div>
</template>

<script setup>
import { ArrowDown } from '@element-plus/icons-vue'
</script>

<style scoped>
.nav-link {
  position: relative;
  padding-bottom: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  color: #64748b;
  transition: color 0.2s ease;
}

.nav-link::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 3px;
  background: #93c5fd;
  border-radius: 999px;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.2s ease, background 0.2s ease;
}

.nav-link:hover {
  color: #2563eb;
}

.nav-link:hover::after {
  transform: scaleX(1);
}

.nav-link-active {
  color: #2563eb;
  font-weight: 600;
}

.nav-link-active::after {
  background: #2563eb;
  transform: scaleX(1);
}

.dropdown-link {
  color: #475569;
  text-decoration: none;
  display: block;
  width: 100%;
}

.dropdown-link:hover {
  color: #2563eb;
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
