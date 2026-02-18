<template>
  <div class="glass-card rounded-2xl p-6">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-lg font-semibold">{{ title }}</h3>
      <select
        v-model="selectedPeriod"
        class="text-sm border rounded-lg px-3 py-1 bg-white"
        @change="updateChart"
      >
        <option value="today">Hoy</option>
        <option value="week">Esta semana</option>
        <option value="month">Este mes</option>
      </select>
    </div>
    <div ref="chartContainer" class="h-64">
      <canvas ref="chartCanvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Chart, registerables } from 'chart.js'
import gsap from 'gsap'

Chart.register(...registerables)

const props = defineProps({
  title: {
    type: String,
    default: 'Turnos por hora'
  },
  data: {
    type: Object,
    required: true
  }
})

const chartCanvas = ref(null)
const chartContainer = ref(null)
const selectedPeriod = ref('today')
let chart = null

onMounted(() => {
  createChart()
  
  // Animación de entrada
  gsap.from(chartContainer.value, {
    opacity: 0,
    y: 20,
    duration: 0.6,
    delay: 0.2
  })
})

const createChart = () => {
  if (chart) chart.destroy()
  
  chart = new Chart(chartCanvas.value, {
    type: 'line',
    data: {
      labels: props.data.labels || [],
      datasets: [{
        label: 'Turnos',
        data: props.data.values || [],
        borderColor: '#4f46e5',
        backgroundColor: 'rgba(79, 70, 229, 0.1)',
        tension: 0.4,
        fill: true,
        pointBackgroundColor: '#4f46e5',
        pointBorderColor: 'white',
        pointBorderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          backgroundColor: 'white',
          titleColor: '#1e293b',
          bodyColor: '#475569',
          borderColor: '#e2e8f0',
          borderWidth: 1,
          padding: 12,
          boxPadding: 6
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: '#e2e8f0'
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      }
    }
  })
}

const updateChart = () => {
  gsap.to(chartContainer.value, {
    opacity: 0,
    y: -10,
    duration: 0.3,
    onComplete: () => {
      createChart()
      gsap.to(chartContainer.value, {
        opacity: 1,
        y: 0,
        duration: 0.3
      })
    }
  })
}

watch(() => props.data, () => {
  createChart()
}, { deep: true })
</script>