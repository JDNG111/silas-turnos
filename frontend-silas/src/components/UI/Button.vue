<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    class="relative inline-flex items-center justify-center font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed hover-lift"
    :class="buttonClasses"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="absolute inset-0 flex items-center justify-center">
      <svg class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
      </svg>
    </span>
    <span :class="{ 'opacity-0': loading }">
      <slot />
    </span>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'outline', 'ghost', 'danger'].includes(value)
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg'].includes(value)
  },
  type: {
    type: String,
    default: 'button'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  fullWidth: {
    type: Boolean,
    default: false
  }
})

const buttonClasses = computed(() => {
  const classes = []
  
  // Variant styles
  switch (props.variant) {
    case 'primary':
      classes.push('gradient-bg text-white hover:shadow-lg focus:ring-primary-500')
      break
    case 'secondary':
      classes.push('bg-secondary-200 text-secondary-900 hover:bg-secondary-300 focus:ring-secondary-500')
      break
    case 'outline':
      classes.push('border-2 border-primary-600 text-primary-600 hover:bg-primary-50 focus:ring-primary-500')
      break
    case 'ghost':
      classes.push('text-secondary-700 hover:bg-secondary-100 focus:ring-secondary-500')
      break
    case 'danger':
      classes.push('bg-red-600 text-white hover:bg-red-700 focus:ring-red-500')
      break
  }
  
  // Size styles
  switch (props.size) {
    case 'sm':
      classes.push('px-3 py-1.5 text-sm rounded-lg')
      break
    case 'md':
      classes.push('px-4 py-2 text-base rounded-xl')
      break
    case 'lg':
      classes.push('px-6 py-3 text-lg rounded-2xl')
      break
  }
  
  if (props.fullWidth) {
    classes.push('w-full')
  }
  
  return classes
})
</script>