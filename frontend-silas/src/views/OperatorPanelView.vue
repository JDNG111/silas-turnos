<template>
  <div class="p-6 lg:p-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold gradient-text mb-2">Panel del Operador</h1>
      <p class="text-secondary-600">Gestiona los turnos en tiempo real</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Columna izquierda - Pendientes -->
      <div class="glass-card rounded-2xl p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold">Pendientes</h2>
          <span class="bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full text-xs font-medium">
            {{ pendingTurns.length }}
          </span>
        </div>
        <div class="space-y-3 max-h-[600px] overflow-y-auto pr-2">
          <TurnCard
            v-for="turn in pendingTurns"
            :key="turn.id"
            :turn="turn"
            :show-actions="true"
            @call="handleCall"
            @cancel="handleCancel"
          />
          <div v-if="pendingTurns.length === 0" class="text-center py-8 text-secondary-500">
            <i class="fas fa-check-circle text-4xl mb-2 opacity-50" />
            <p>No hay turnos pendientes</p>
          </div>
        </div>
      </div>

      <!-- Columna central - Llamados y En Progreso -->
      <div class="space-y-6">
        <!-- Llamados -->
        <div class="glass-card rounded-2xl p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold">Llamados</h2>
            <span class="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs font-medium">
              {{ calledTurns.length }}
            </span>
          </div>
          <div class="space-y-3 max-h-[280px] overflow-y-auto pr-2">
            <TurnCard
              v-for="turn in calledTurns"
              :key="turn.id"
              :turn="turn"
              :show-actions="true"
              @start="handleStart"
              @cancel="handleCancel"
            />
            <div v-if="calledTurns.length === 0" class="text-center py-4 text-secondary-500">
              <i class="fas fa-bell-slash text-3xl mb-2 opacity-50" />
              <p class="text-sm">No hay turnos llamados</p>
            </div>
          </div>
        </div>

        <!-- En Progreso -->
        <div class="glass-card rounded-2xl p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold">En Progreso</h2>
            <span class="bg-purple-100 text-purple-800 px-2 py-1 rounded-full text-xs font-medium">
              {{ inProgressTurns.length }}
            </span>
          </div>
          <div class="space-y-3 max-h-[280px] overflow-y-auto pr-2">
            <TurnCard
              v-for="turn in inProgressTurns"
              :key="turn.id"
              :turn="turn"
              :show-actions="true"
              @complete="handleComplete"
            />
            <div v-if="inProgressTurns.length === 0" class="text-center py-4 text-secondary-500">
              <i class="fas fa-pause-circle text-3xl mb-2 opacity-50" />
              <p class="text-sm">No hay turnos en progreso</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Columna derecha - Controles -->
      <div class="glass-card rounded-2xl p-6">
        <h2 class="text-lg font-semibold mb-4">Controles</h2>
        
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-secondary-700 mb-2">
              Punto de Atención
            </label>
            <Input
              v-model="attentionPoint"
              placeholder="Ej: Ventanilla 1"
              icon="fas fa-map-marker-alt"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-secondary-700 mb-2">
              Buscar Turno
            </label>
            <div class="flex gap-2">
              <Input
                v-model="searchTicket"
                placeholder="Número de ticket"
                icon="fas fa-search"
              />
              <Button variant="primary" @click="searchTurn" :disabled="!searchTicket">
                Buscar
              </Button>
            </div>
          </div>

          <div v-if="searchedTurn" class="mt-4">
            <p class="text-sm font-medium mb-2">Turno encontrado:</p>
            <TurnCard :turn="searchedTurn" :show-actions="true" />
          </div>

          <hr class="border-secondary-200">

          <Button variant="secondary" fullWidth @click="refreshTurns">
            <i class="fas fa-sync-alt mr-2" /> Actualizar
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useToast } from 'vue-toastification'
import TurnCard from '../components/Turns/TurnCard.vue'
import Button from '../components/UI/Button.vue'
import Input from '../components/UI/Input.vue'
import axios from 'axios'

const store = useStore()
const toast = useToast()

const attentionPoint = ref('Ventanilla 1')
const searchTicket = ref('')
const searchedTurn = ref(null)

const pendingTurns = computed(() => store.getters.pendingTurns)
const calledTurns = computed(() => store.getters.calledTurns)
const inProgressTurns = computed(() => store.getters.inProgressTurns)

onMounted(() => {
  store.dispatch('fetchActiveTurns')
  
  // Auto-refresh cada 10 segundos
  setInterval(() => {
    store.dispatch('fetchActiveTurns')
  }, 10000)
})

const handleCall = async (turn) => {
  try {
    await store.dispatch('callTurn', {
      id: turn.id,
      attentionPoint: attentionPoint.value
    })
    toast.success(`Turno ${turn.ticketNumber} llamado`)
  } catch (error) {
    toast.error('Error al llamar el turno')
  }
}

const handleStart = async (turn) => {
  try {
    await store.dispatch('startTurn', turn.id)
    toast.success(`Turno ${turn.ticketNumber} iniciado`)
  } catch (error) {
    toast.error('Error al iniciar el turno')
  }
}

const handleComplete = async (turn) => {
  try {
    await store.dispatch('completeTurn', turn.id)
    toast.success(`Turno ${turn.ticketNumber} completado`)
  } catch (error) {
    toast.error('Error al completar el turno')
  }
}

const handleCancel = async (turn) => {
  if (!confirm(`¿Cancelar turno ${turn.ticketNumber}?`)) return
  
  try {
    await store.dispatch('cancelTurn', {
      id: turn.id,
      reason: 'Cancelado por operador'
    })
    toast.success(`Turno ${turn.ticketNumber} cancelado`)
  } catch (error) {
    toast.error('Error al cancelar el turno')
  }
}

const searchTurn = async () => {
  if (!searchTicket.value) return
  
  try {
    const response = await axios.get(`/api/turns/ticket/${searchTicket.value}`)
    searchedTurn.value = response.data
  } catch (error) {
    toast.error('Turno no encontrado')
    searchedTurn.value = null
  }
}

const refreshTurns = () => {
  store.dispatch('fetchActiveTurns')
}
</script>