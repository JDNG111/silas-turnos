<template>
  <div
    ref="cardRef"
    class="glass-card rounded-2xl p-6 cursor-pointer hover-lift"
    @mouseenter="startAnimation"
    @mouseleave="stopAnimation"
  >
    <div class="flex items-center justify-between">
      <div>
        <p class="text-sm font-medium text-gray-600">{{ title }}</p>
        <p class="text-3xl font-bold gradient-text mt-2">{{ value }}</p>
      </div>
      <div
        class="w-12 h-12 rounded-2xl gradient-bg flex items-center justify-center text-white text-2xl"
      >
        <i :class="icon"></i>
      </div>
    </div>
    <div v-if="trend" class="mt-4 flex items-center text-sm">
      <span :class="trend > 0 ? 'text-green-600' : 'text-red-600'" class="flex items-center">
        <i :class="trend > 0 ? 'fas fa-arrow-up' : 'fas fa-arrow-down'" class="mr-1"></i>
        {{ Math.abs(trend) }}%
      </span>
      <span class="text-gray-500 ml-2">{{ trendLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  title: { type: String, required: true },
  value: { type: [String, Number], required: true },
  icon: { type: String, required: true },
  trend: { type: Number, default: null },
  trendLabel: { type: String, default: 'vs. ayer' }
})

const cardRef = ref(null)
let gsapInstance = null

onMounted(async () => {
  // Cargar GSAP dinámicamente solo si existe en window
  if (window.gsap) {
    gsapInstance = window.gsap
  }
})

const startAnimation = () => {
  if (gsapInstance && cardRef.value) {
    gsapInstance.to(cardRef.value, {
      scale: 1.02,
      duration: 0.3,
      ease: 'power2.out'
    })
  }
}

const stopAnimation = () => {
  if (gsapInstance && cardRef.value) {
    gsapInstance.to(cardRef.value, {
      scale: 1,
      duration: 0.3,
      ease: 'power2.out'
    })
  }
}
</script>