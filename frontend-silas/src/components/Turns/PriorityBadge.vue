<template>
  <span
    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
    :class="badgeClasses"
  >
    <i :class="iconClass" class="mr-1" />
    {{ label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  priority: {
    type: String,
    required: true
  }
})

const badgeClasses = computed(() => {
  switch (props.priority) {
    case 'VIP':
    case 'EMERGENCY':
      return 'bg-red-100 text-red-800'
    case 'HIGH':
      return 'bg-orange-100 text-orange-800'
    case 'NORMAL':
      return 'bg-blue-100 text-blue-800'
    case 'LOW':
      return 'bg-green-100 text-green-800'
    default:
      return 'bg-secondary-100 text-secondary-800'
  }
})

const iconClass = computed(() => {
  switch (props.priority) {
    case 'VIP':
      return 'fas fa-crown'
    case 'EMERGENCY':
      return 'fas fa-ambulance'
    case 'HIGH':
      return 'fas fa-arrow-up'
    case 'NORMAL':
      return 'fas fa-minus'
    case 'LOW':
      return 'fas fa-arrow-down'
    default:
      return 'fas fa-tag'
  }
})

const label = computed(() => {
  switch (props.priority) {
    case 'VIP':
      return 'VIP'
    case 'EMERGENCY':
      return 'Emergencia'
    case 'HIGH':
      return 'Alta'
    case 'NORMAL':
      return 'Normal'
    case 'LOW':
      return 'Baja'
    default:
      return props.priority
  }
})
</script>