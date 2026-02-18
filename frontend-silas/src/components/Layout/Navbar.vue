<template>
  <nav class="glass-card fixed top-0 left-0 right-0 z-40 px-6 py-4">
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-4">
        <button
          class="lg:hidden text-secondary-600 hover:text-primary-600 transition-colors"
          @click="$emit('toggle-sidebar')"
        >
          <i class="fas fa-bars text-2xl" />
        </button>
        <router-link to="/" class="flex items-center space-x-2">
          <div class="w-10 h-10 gradient-bg rounded-xl flex items-center justify-center">
            <i class="fas fa-ticket-alt text-white text-xl" />
          </div>
          <span class="text-xl font-bold gradient-text hidden sm:block">SILAS</span>
        </router-link>
      </div>

      <div class="flex items-center space-x-4">
        <button class="p-2 rounded-lg hover:bg-secondary-100 transition-colors relative">
          <i class="fas fa-bell text-secondary-600" />
          <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full" />
        </button>
        
        <div class="flex items-center space-x-3">
          <div class="hidden md:block text-right">
            <p class="text-sm font-medium">{{ user?.name || 'Operador' }}</p>
            <p class="text-xs text-secondary-500">{{ user?.role || 'Administrador' }}</p>
          </div>
          <div class="w-10 h-10 rounded-xl gradient-bg flex items-center justify-center text-white font-bold">
            {{ user?.initials || 'AD' }}
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'

const user = ref({
  name: 'Admin SILAS',
  role: 'Administrador',
  initials: 'AD'
})
</script>