<template>
  <div
    ref="cardRef"
    class="glass-card rounded-xl p-4 hover-lift cursor-pointer"
    :class="{ 'border-l-4': true, 'border-l-red-500': turn.priority === 'VIP' || turn.priority === 'EMERGENCY', 'border-l-orange-500': turn.priority === 'HIGH', 'border-l-blue-500': turn.priority === 'NORMAL', 'border-l-green-500': turn.priority === 'LOW' }"
    @mouseenter="startAnimation"
    @mouseleave="stopAnimation"
  >
    <div class="flex items-start justify-between">
      <div>
        <div class="flex items-center space-x-2 mb-2">
          <PriorityBadge :priority="turn.priority" />
          <span class="text-xs font-medium px-2 py-0.5 rounded-full" :class="statusClass">
            {{ turn.statusDescription }}
          </span>
        </div>
        <p class="text-lg font-bold gradient-text">{{ turn.ticketNumber }}</p>
        <p class="text-secondary-700">{{ turn.customerName }}</p>
        <p class="text-sm text-secondary-500 mt-1">{{ turn.serviceName }}</p>
      </div>
      <div class="text-right">
        <p class="text-2xl font-bold text-primary-600">#{{ turn.waitingNumber }}</p>
        <p v-if="turn.estimatedWaitTime" class="text-xs text-secondary-500">
          Espera: ~{{ turn.estimatedWaitTime }} min
        </p>
      </div>
    </div>
    
    <div v-if="showActions" class="mt-4 flex space-x-2">
      <Button
        v-if="turn.status === 'PENDING'"
        variant="primary"
        size="sm"
        @click="$emit('call', turn)"
      >
        <i class="fas fa-bell mr-1" /> Llamar
      </Button>
      <Button
        v-if="turn.status === 'CALLED'"
        variant="primary"
        size="sm"
        @click="$emit('start', turn)"
      >
        <i class="fas fa-play mr-1" /> Iniciar
      </Button>
      <Button
        v-if="turn.status === 'IN_PROGRESS'"
        variant="primary"
        size="sm"
        @click="$emit('complete', turn)"
      >
        <i class="fas fa-check mr-1" /> Completar
      </Button>
      <Button
        v-if="turn.status !== 'COMPLETED' && turn.status !== 'CANCELLED'"
        variant="danger"
        size="sm"
        @click="$emit('cancel', turn)"
      >
        <i class="fas fa-times mr-1" /> Cancelar
      </Button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import gsap from 'gsap'
import PriorityBadge from './PriorityBadge.vue'
import Button from '../UI/Button.vue'

const props = defineProps({
  turn: {
    type: Object,
    required: true
  },
  showActions: {
    type: Boolean,
    default: false
  }
})

const cardRef = ref(null)

const statusClass = computed(() => {
  switch (props.turn.status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800'
    case 'CALLED':
      return 'bg-blue-100 text-blue-800'
    case 'IN_PROGRESS':
      return 'bg-purple-100 text-purple-800'
    case 'COMPLETED':
      return 'bg-green-100 text-green-800'
    case 'CANCELLED':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-secondary-100 text-secondary-800'
  }
})

const startAnimation = () => {
  gsap.to(cardRef.value, {
    scale: 1.02,
    boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    duration: 0.3,
    ease: 'power2.out'
  })
}

const stopAnimation = () => {
  gsap.to(cardRef.value, {
    scale: 1,
    boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
    duration: 0.3,
    ease: 'power2.out'
  })
}
</script>