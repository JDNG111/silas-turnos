<template>
  <div class="glass-card rounded-2xl p-6">
    <h3 class="text-lg font-semibold mb-4">Turnos Recientes</h3>
    <div class="space-y-3">
      <div
        v-for="(turn, index) in turns"
        :key="turn.id"
        ref="turnItems"
        class="flex items-center justify-between p-3 rounded-xl bg-white/50 hover:bg-white transition-all duration-200 cursor-pointer"
        @click="$emit('select', turn)"
      >
        <div class="flex items-center space-x-3">
          <PriorityBadge :priority="turn.priority" />
          <div>
            <p class="font-medium">{{ turn.ticketNumber }}</p>
            <p class="text-sm text-secondary-500">{{ turn.customerName }}</p>
          </div>
        </div>
        <div class="text-right">
          <p class="text-sm font-medium">{{ turn.serviceName }}</p>
          <p class="text-xs text-secondary-500">{{ turn.createdAtFormatted }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'
import PriorityBadge from '../Turns/PriorityBadge.vue'

const props = defineProps({
  turns: {
    type: Array,
    required: true
  }
})

const turnItems = ref([])

onMounted(() => {
  gsap.from(turnItems.value, {
    opacity: 0,
    x: -20,
    duration: 0.5,
    stagger: 0.1,
    ease: 'power2.out'
  })
})
</script>