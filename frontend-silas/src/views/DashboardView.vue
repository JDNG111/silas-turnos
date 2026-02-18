<template>
  <div class="p-6 lg:p-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold gradient-text mb-2">Dashboard</h1>
      <p class="text-secondary-600">Bienvenido de vuelta, aquí está el resumen de hoy</p>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <StatCard
        title="Turnos Hoy"
        :value="dashboard?.totalTurnsToday || 0"
        icon="fas fa-ticket-alt"
        :trend="12"
      />
      <StatCard
        title="Pendientes"
        :value="dashboard?.pendingTurns || 0"
        icon="fas fa-clock"
        :trend="-5"
      />
      <StatCard
        title="En Progreso"
        :value="dashboard?.inProgressTurns || 0"
        icon="fas fa-spinner"
        :trend="8"
      />
      <StatCard
        title="Completados"
        :value="dashboard?.completedTurns || 0"
        icon="fas fa-check-circle"
        :trend="15"
      />
    </div>

    <!-- Charts -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
      <TurnsChart
        title="Turnos por Hora"
        :data="chartData"
      />
      <TurnsChart
        title="Turnos por Servicio"
        :data="serviceChartData"
      />
    </div>

    <!-- Recent Turns -->
    <RecentTurns
      :turns="dashboard?.recentTurns || []"
      @select="handleTurnSelect"
    />

    <!-- Turn Modal -->
    <TurnModal
      :show="showModal"
      title="Detalles del Turno"
      :actions="modalActions"
      @close="showModal = false"
    >
      <div v-if="selectedTurn" class="space-y-4">
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
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useToast } from 'vue-toastification'
import StatCard from '../components/Dashboard/StatCard.vue'
import TurnsChart from '../components/Dashboard/TurnsChart.vue'
import RecentTurns from '../components/Dashboard/RecentTurns.vue'
import TurnModal from '../components/Turns/TurnModal.vue'
import PriorityBadge from '../components/Turns/PriorityBadge.vue'
import gsap from 'gsap'

const toast = useToast()
const dashboard = ref(null)
const showModal = ref(false)
const selectedTurn = ref(null)

const chartData = computed(() => {
  if (!dashboard.value) return { labels: [], values: [] }
  
  const hours = dashboard.value.turnsByHour || []
  return {
    labels: hours.map(h => h.hour),
    values: hours.map(h => h.count)
  }
})

const serviceChartData = computed(() => {
  if (!dashboard.value) return { labels: [], values: [] }
  
  const services = dashboard.value.turnsByService || []
  return {
    labels: services.map(s => s.service),
    values: services.map(s => s.count)
  }
})

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
  
  if (selectedTurn.value.status !== 'COMPLETED' && selectedTurn.value.status !== 'CANCELLED') {
    actions.push({
      label: 'Cancelar',
      variant: 'danger',
      handler: () => cancelTurn(selectedTurn.value.id)
    })
  }
  
  return actions
})

onMounted(() => {
  loadDashboard()
  
  // Refresh cada 30 segundos
  setInterval(loadDashboard, 30000)
})

const loadDashboard = async () => {
  try {
    const response = await axios.get('/api/dashboard')
    dashboard.value = response.data
    
    // Animación cuando llegan nuevos datos
    gsap.from('.glass-card', {
      opacity: 0,
      y: 20,
      duration: 0.5,
      stagger: 0.1
    })
  } catch (error) {
    toast.error('Error al cargar el dashboard')
  }
}

const handleTurnSelect = (turn) => {
  selectedTurn.value = turn
  showModal.value = true
}

const callTurn = async (id) => {
  try {
    await axios.put(`/api/turns/${id}/call?attentionPoint=Ventanilla%201`)
    toast.success('Turno llamado')
    showModal.value = false
    loadDashboard()
  } catch (error) {
    toast.error('Error al llamar el turno')
  }
}

const startTurn = async (id) => {
  try {
    await axios.put(`/api/turns/${id}/start`)
    toast.success('Turno iniciado')
    showModal.value = false
    loadDashboard()
  } catch (error) {
    toast.error('Error al iniciar el turno')
  }
}

const completeTurn = async (id) => {
  try {
    await axios.put(`/api/turns/${id}/complete`)
    toast.success('Turno completado')
    showModal.value = false
    loadDashboard()
  } catch (error) {
    toast.error('Error al completar el turno')
  }
}

const cancelTurn = async (id) => {
  if (!confirm('¿Estás seguro de cancelar este turno?')) return
  
  try {
    await axios.put(`/api/turns/${id}/cancel`, { reason: 'Cancelado por usuario' })
    toast.success('Turno cancelado')
    showModal.value = false
    loadDashboard()
  } catch (error) {
    toast.error('Error al cancelar el turno')
  }
}
</script>