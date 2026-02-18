<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-900 to-purple-900 text-white p-8">
    <div class="text-center mb-12">
      <h1 class="text-5xl font-bold mb-2">Sistema de Turnos</h1>
      <p class="text-blue-200 text-xl">{{ currentDateTime }}</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Turnos Llamados -->
      <div class="lg:col-span-2">
        <h2 class="text-3xl font-semibold mb-6 flex items-center">
          <i class="fas fa-bell text-yellow-400 mr-3"></i>
          Turnos Llamados
        </h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div
            v-for="turn in calledTurns"
            :key="turn.id"
            class="bg-gradient-to-br from-green-500 to-green-700 rounded-2xl p-8 text-center shadow-2xl transform hover:scale-105 transition-transform"
          >
            <div class="text-7xl font-bold mb-4">{{ turn.ticketNumber }}</div>
            <div class="text-3xl mb-2">{{ turn.serviceName }}</div>
            <div class="text-xl text-green-200">{{ turn.attentionPoint || 'Ventanilla 1' }}</div>
          </div>
        </div>

        <div v-if="calledTurns.length === 0" class="text-center py-12 bg-white/10 rounded-2xl">
          <i class="fas fa-bell-slash text-6xl mb-4 opacity-50"></i>
          <p class="text-2xl">No hay turnos llamados</p>
        </div>
      </div>

      <!-- Próximos Turnos -->
      <div class="bg-white/10 rounded-2xl p-6 backdrop-blur-lg">
        <h2 class="text-3xl font-semibold mb-6 flex items-center">
          <i class="fas fa-list text-blue-400 mr-3"></i>
          Próximos Turnos
        </h2>

        <div class="space-y-3">
          <div
            v-for="(turn, index) in pendingTurns.slice(0, 10)"
            :key="turn.id"
            class="bg-white/20 rounded-xl p-4 flex items-center justify-between hover:bg-white/30 transition-colors"
          >
            <div class="flex items-center">
              <span class="text-2xl font-bold text-yellow-400 mr-4">#{{ index + 1 }}</span>
              <div>
                <div class="font-medium text-xl">{{ turn.ticketNumber }}</div>
                <div class="text-sm text-blue-200">{{ turn.serviceName }}</div>
              </div>
            </div>
            <span class="px-3 py-1 rounded-full text-sm font-medium" :class="priorityClass(turn.priority)">
              {{ turn.priority }}
            </span>
          </div>
        </div>

        <div v-if="pendingTurns.length === 0" class="text-center py-8 text-white/50">
          <p class="text-xl">No hay turnos en espera</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const currentDateTime = ref('')

const calledTurns = computed(() => store.state.activeTurns.filter(t => t.status === 'CALLED'))
const pendingTurns = computed(() => store.state.activeTurns.filter(t => t.status === 'PENDING'))

const priorityClass = (priority) => {
  const classes = {
    'VIP': 'bg-purple-500 text-white',
    'HIGH': 'bg-orange-500 text-white',
    'NORMAL': 'bg-blue-500 text-white',
    'LOW': 'bg-green-500 text-white'
  }
  return classes[priority] || 'bg-gray-500 text-white'
}

let interval

onMounted(() => {
  store.dispatch('fetchActiveTurns')
  
  updateDateTime()
  interval = setInterval(updateDateTime, 1000)
  
  // Auto-refresh cada 5 segundos
  setInterval(() => {
    store.dispatch('fetchActiveTurns')
  }, 5000)
})

onUnmounted(() => {
  clearInterval(interval)
})

const updateDateTime = () => {
  const now = new Date()
  currentDateTime.value = now.toLocaleDateString('es-CO', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}
</script>