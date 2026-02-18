<template>
  <div class="glass-card rounded-2xl p-6">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-lg font-semibold gradient-text">{{ title }}</h3>
      <div class="flex gap-2">
        <button
          v-for="period in periods"
          :key="period.value"
          @click="selectedPeriod = period.value"
          class="px-3 py-1 text-sm rounded-lg transition-colors"
          :class="selectedPeriod === period.value ? 'gradient-bg text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
        >
          {{ period.label }}
        </button>
      </div>
    </div>
    
    <div class="relative h-80">
      <canvas ref="chartCanvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'

Chart.register(...registerables, ChartDataLabels)

const props = defineProps({
  title: String,
  type: {
    type: String,
    default: 'line' // line, bar, doughnut, polarArea
  },
  data: {
    type: Object,
    required: true
  }
})

const chartCanvas = ref(null)
const selectedPeriod = ref('day')
let chart = null

const periods = [
  { value: 'day', label: 'Hoy' },
  { value: 'week', label: 'Semana' },
  { value: 'month', label: 'Mes' },
  { value: 'year', label: 'Año' }
]

onMounted(() => {
  createChart()
})

watch(() => props.data, () => {
  if (chart) {
    chart.destroy()
    createChart()
  }
}, { deep: true })

const createChart = () => {
  if (!chartCanvas.value) return
  
  const ctx = chartCanvas.value.getContext('2d')
  
  // Gradientes personalizados
  const gradient = ctx.createLinearGradient(0, 0, 0, 400)
  gradient.addColorStop(0, 'rgba(102, 126, 234, 0.8)')
  gradient.addColorStop(1, 'rgba(118, 75, 162, 0.2)')
  
  const colors = {
    primary: '#667eea',
    secondary: '#764ba2',
    success: '#48bb78',
    warning: '#fbbf24',
    danger: '#f87171',
    info: '#60a5fa'
  }
  
  chart = new Chart(ctx, {
    type: props.type,
    data: {
      labels: props.data.labels || [],
      datasets: [{
        label: 'Turnos',
        data: props.data.values || [],
        backgroundColor: props.type === 'line' ? gradient : [
          colors.primary,
          colors.secondary,
          colors.success,
          colors.warning,
          colors.danger,
          colors.info
        ],
        borderColor: colors.primary,
        borderWidth: 2,
        tension: 0.4,
        fill: props.type === 'line',
        pointBackgroundColor: colors.primary,
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 5,
        pointHoverRadius: 8,
        pointHoverBackgroundColor: colors.secondary,
        pointHoverBorderColor: '#fff',
        pointHoverBorderWidth: 3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: props.type !== 'line',
          position: 'bottom',
          labels: {
            usePointStyle: true,
            padding: 20,
            font: {
              size: 12,
              family: 'Inter, sans-serif'
            }
          }
        },
        tooltip: {
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          titleColor: '#1f2937',
          bodyColor: '#4b5563',
          borderColor: '#e5e7eb',
          borderWidth: 1,
          padding: 12,
          boxPadding: 6,
          usePointStyle: true,
          callbacks: {
            label: (context) => {
              return `${context.dataset.label}: ${context.raw} turnos`
            }
          }
        },
        datalabels: {
          display: props.type === 'doughnut',
          color: '#fff',
          font: {
            weight: 'bold',
            size: 14
          },
          formatter: (value) => {
            return value > 0 ? value : ''
          }
        }
      },
      scales: props.type !== 'doughnut' && props.type !== 'polarArea' ? {
        y: {
          beginAtZero: true,
          grid: {
            color: 'rgba(0, 0, 0, 0.05)',
            drawBorder: false
          },
          ticks: {
            stepSize: 1,
            callback: (value) => value + ' turnos'
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      } : undefined,
      animation: {
        duration: 1000,
        easing: 'easeInOutQuart',
        onComplete: () => {
          // Animación de entrada con GSAP si está disponible
          if (window.gsap && chartCanvas.value) {
            window.gsap.from(chartCanvas.value, {
              opacity: 0,
              scale: 0.9,
              duration: 1,
              ease: 'power3.out'
            })
          }
        }
      },
      layout: {
        padding: {
          top: 20,
          bottom: 20
        }
      }
    }
  })
}
</script>