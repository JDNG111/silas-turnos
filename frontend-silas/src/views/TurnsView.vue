<template>
  <div class="p-6 lg:p-8">
    <div class="flex items-center justify-between mb-8">
      <div>
        <h1 class="text-3xl font-bold gradient-text mb-2">Gestión de Turnos</h1>
        <p class="text-secondary-600">Visualiza y gestiona todos los turnos del sistema</p>
      </div>
      <Button variant="primary" @click="showNewTurnModal = true">
        <i class="fas fa-plus mr-2" /> Nuevo Turno
      </Button>
    </div>

    <!-- Filtros -->
    <div class="glass-card rounded-2xl p-4 mb-6">
      <div class="flex flex-wrap gap-4">
        <div class="flex-1 min-w-[200px]">
          <Input
            v-model="filters.search"
            placeholder="Buscar por ticket o cliente..."
            icon="fas fa-search"
          />
        </div>
        <div class="w-48">
          <select
            v-model="filters.status"
            class="w-full rounded-xl border-secondary-200 shadow-sm focus:border-primary-500 focus:ring-primary-500"
          >
            <option value="">Todos los estados</option>
            <option value="PENDING">Pendiente</option>
            <option value="CALLED">Llamado</option>
            <option value="IN_PROGRESS">En Atención</option>
            <option value="COMPLETED">Completado</option>
            <option value="CANCELLED">Cancelado</option>
          </select>
        </div>
        <div class="w-48">
          <select
            v-model="filters.service"
            class="w-full rounded-xl border-secondary-200 shadow-sm focus:border-primary-500 focus:ring-primary-500"
          >
            <option value="">Todos los servicios</option>
            <option v-for="service in services" :key="service.id" :value="service.id">
              {{ service.name }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Tabla de turnos -->
    <div class="glass-card rounded-2xl overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-secondary-50">
            <tr>
              <th class="px-6 py-4 text-left text-sm font-semibold text-secondary-600">Ticket</th>
              <th class="px-6 py-4 text-left text-sm font-semibold text-secondary-600">Cliente</th>
              <th class="px-6 py-4 text-left text-sm font-semibold text-secondary-600">Servicio</th>
              <th class="px-6 py-4 text-left text-sm font-semibold text-secondary-600">Prioridad</th>
              <th class="px-6 py-4 text-left text-sm font-semibold text-secondary-600">Estado</th>
              <th class="px-6 py-4 text-left text-sm font-semibold text-secondary-600">Hora</th>
              <th class="px-6 py-4 text-left text-sm font-semibold text-secondary-600">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-secondary-200">
            <tr v-for="turn in filteredTurns" :key="turn.id" class="hover:bg-secondary-50 transition-colors">
              <td class="px-6 py-4 font-medium">{{ turn.ticketNumber }}</td>
              <td class="px-6 py-4">{{ turn.customerName }}</td>
              <td class="px-6 py-4">{{ turn.serviceName }}</td>
              <td class="px-6 py-4">
                <PriorityBadge :priority="turn.priority" />
              </td>
              <td class="px-6 py-4">
                <span class="px-2 py-1 rounded-full text-xs font-medium" :class="statusClass(turn.status)">
                  {{ turn.statusDescription }}
                </span>
              </td>
              <td class="px-6 py-4">{{ turn.createdAtFormatted }}</td>
              <td class="px-6 py-4">
                <Button variant="ghost" size="sm" @click="viewTurnDetails(turn)">
                  <i class="fas fa-eye" />
                </Button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal de detalles -->
    <TurnModal
      :show="showDetailsModal"
      title="Detalles del Turno"
      :actions="modalActions"
      @close="showDetailsModal = false"
    >
      <div v-if="selectedTurn" class="space-y-4">
        <!-- Contenido del modal (igual que en DashboardView) -->
        <div class="flex items-center justify-between">
          <PriorityBadge :priority="selectedTurn.priority" />
          <span class="text-sm text-secondary-500">{{ selectedTurn.createdAtFormatted }}</span>
        </div>
        
        <div class="text-center py-4">
          <p class="text-4xl font-bold gradient-text">{{ selectedTurn.ticketNumber }}</p>
          <p class="text-secondary-600 mt-2">{{ selectedTurn.customerName }}</p>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="bg-secondary-50 p-3 rounded-xl">
            <p class="text-xs text-secondary-500">Servicio</p>
            <p class="font-medium">{{ selectedTurn.serviceName }}</p>
          </div>
          <div class="bg-secondary-50 p-3 rounded-xl">
            <p class="text-xs text-secondary-500">Número</p>
            <p class="font-medium">#{{ selectedTurn.waitingNumber }}</p>
          </div>
          <div class="bg-secondary-50 p-3 rounded-xl">
            <p class="text-xs text-secondary-500">Estado</p>
            <p class="font-medium">{{ selectedTurn.statusDescription }}</p>
          </div>
          <div class="bg-secondary-50 p-3 rounded-xl">
            <p class="text-xs text-secondary-500">Teléfono</p>
            <p class="font-medium">{{ selectedTurn.customerPhone || 'N/A' }}</p>
          </div>
        </div>
      </div>
    </TurnModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import axios from 'axios'
import { useToast } from 'vue-toastification'
import Button from '../components/UI/Button.vue'
import Input from '../components/UI/Input.vue'
import PriorityBadge from '../components/Turns/PriorityBadge.vue'
import TurnModal from '../components/Turns/TurnModal.vue'

const store = useStore()
const toast = useToast()

const services = ref([])
const turns = ref([])
const selectedTurn = ref(null)
const showDetailsModal = ref(false)

const filters = ref({
  search: '',
  status: '',
  service: ''
})

onMounted(async () => {
  await loadServices()
  await loadTurns()
})

const loadServices = async () => {
  try {
    const response = await axios.get('/api/services')
    services.value = response.data
  } catch (error) {
    toast.error('Error al cargar servicios')
  }
}

const loadTurns = async () => {
  try {
    const response = await axios.get('/api/turns/active')
    turns.value = response.data
  } catch (error) {
    toast.error('Error al cargar turnos')
  }
}

const filteredTurns = computed(() => {
  return turns.value.filter(turn => {
    const matchesSearch = filters.value.search === '' ||
      turn.ticketNumber.toLowerCase().includes(filters.value.search.toLowerCase()) ||
      turn.customerName.toLowerCase().includes(filters.value.search.toLowerCase())
    
    const matchesStatus = filters.value.status === '' || turn.status === filters.value.status
    const matchesService = filters.value.service === '' || turn.serviceId === parseInt(filters.value.service)
    
    return matchesSearch && matchesStatus && matchesService
  })
})

const statusClass = (status) => {
  const classes = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'CALLED': 'bg-blue-100 text-blue-800',
    'IN_PROGRESS': 'bg-purple-100 text-purple-800',
    'COMPLETED': 'bg-green-100 text-green-800',
    'CANCELLED': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-secondary-100 text-secondary-800'
}

const viewTurnDetails = (turn) => {
  selectedTurn.value = turn
  showDetailsModal.value = true
}

const modalActions = computed(() => {
  if (!selectedTurn.value) return []
  
  const actions = []
  
  switch (selectedTurn.value.status) {
    case 'PENDING':
      actions.push({
        label: 'Llamar',
        variant: 'primary',
        handler: () => callTurn(selectedTurn.value.id)
      })
      break
    case 'CALLED':
      actions.push({
        label: 'Iniciar',
        variant: 'primary',
        handler: () => startTurn(selectedTurn.value.id)
      })
      break
    case 'IN_PROGRESS':
      actions.push({
        label: 'Completar',
        variant: 'primary',
        handler: () => completeTurn(selectedTurn.value.id)
      })
      break
  }
  
  return actions
})

const callTurn = async (id) => {
  try {
    await store.dispatch('callTurn', { id, attentionPoint: 'Ventanilla 1' })
    toast.success('Turno llamado')
    showDetailsModal.value = false
    loadTurns()
  } catch (error) {
    toast.error('Error al llamar el turno')
  }
}

const startTurn = async (id) => {
  try {
    await store.dispatch('startTurn', id)
    toast.success('Turno iniciado')
    showDetailsModal.value = false
    loadTurns()
  } catch (error) {
    toast.error('Error al iniciar el turno')
  }
}

const completeTurn = async (id) => {
  try {
    await store.dispatch('completeTurn', id)
    toast.success('Turno completado')
    showDetailsModal.value = false
    loadTurns()
  } catch (error) {
    toast.error('Error al completar el turno')
  }
}
</script>