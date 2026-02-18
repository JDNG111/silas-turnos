<template>
  <aside
    ref="sidebarRef"
    class="fixed left-0 top-0 h-full w-64 glass-card z-50 transform transition-transform duration-300 ease-in-out lg:translate-x-0"
    :class="isOpen ? 'translate-x-0' : '-translate-x-full'"
  >
    <div class="flex flex-col h-full">
      <div class="p-6">
        <div class="flex items-center justify-between">
          <router-link to="/" class="flex items-center space-x-2">
            <div class="w-10 h-10 gradient-bg rounded-xl flex items-center justify-center">
              <i class="fas fa-ticket-alt text-white text-xl"></i>
            </div>
            <span class="text-xl font-bold gradient-text">SILAS</span>
          </router-link>
          <button
            class="lg:hidden text-gray-600 hover:text-primary-600"
            @click="$emit('close')"
          >
            <i class="fas fa-times text-xl"></i>
          </button>
        </div>
      </div>

      <nav class="flex-1 px-4 space-y-1">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="flex items-center px-4 py-3 text-gray-700 rounded-xl hover:bg-purple-50 hover:text-purple-600 transition-all duration-200 group"
          :class="{ 'bg-purple-50 text-purple-600': isActive(item.path) }"
        >
          <i :class="[item.icon, 'w-6 text-lg']"></i>
          <span class="ml-3">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="p-4 border-t border-gray-200">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 rounded-xl gradient-bg flex items-center justify-center text-white font-bold">
            AD
          </div>
          <div>
            <p class="text-sm font-medium">Admin SILAS</p>
            <p class="text-xs text-gray-500">Administrador</p>
          </div>
        </div>
      </div>
    </div>
  </aside>

  <!-- Overlay for mobile -->
  <div
    v-if="isOpen"
    class="fixed inset-0 bg-gray-900 bg-opacity-50 z-40 lg:hidden"
    @click="$emit('close')"
  ></div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

// Definir emits explícitamente
const emit = defineEmits(['close'])

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()

const menuItems = [
  { path: '/', name: 'Dashboard', icon: 'fas fa-chart-pie' },
  { path: '/turns', name: 'Turnos', icon: 'fas fa-list' },
  { path: '/new-turn', name: 'Nuevo Turno', icon: 'fas fa-plus-circle' },
  { path: '/operator', name: 'Panel Operador', icon: 'fas fa-users-cog' },
  { path: '/display', name: 'Pantalla', icon: 'fas fa-tv' }
]

const isActive = (path) => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}
</script>