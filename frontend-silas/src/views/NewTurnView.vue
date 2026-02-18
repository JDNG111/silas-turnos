<template>
  <div class="p-6 lg:p-8">
    <div class="max-w-2xl mx-auto">
      <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold gradient-text mb-2">Nuevo Turno</h1>
        <p class="text-gray-600">Genera un nuevo turno para un cliente</p>
      </div>

      <div class="glass-card rounded-2xl p-8">
        <form @submit.prevent="handleSubmit">
          <!-- Selección de Servicio -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Servicio <span class="text-red-500">*</span>
            </label>
            <div class="grid grid-cols-2 gap-3">
              <button
                v-for="service in services"
                :key="service.id"
                type="button"
                class="p-4 rounded-xl border-2 transition-all duration-300 text-left relative overflow-hidden group"
                :class="[
                  form.serviceId === service.id 
                    ? 'border-purple-600 bg-purple-50 shadow-lg scale-105' 
                    : 'border-gray-200 hover:border-purple-300 hover:bg-purple-50/50'
                ]"
                @click="selectService(service)"
              >
                <!-- Efecto de brillo -->
                <div 
                  v-if="form.serviceId === service.id"
                  class="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent -skew-x-12 animate-shine"
                ></div>
                
                <p class="font-medium">{{ service.name }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ service.code }}</p>
                
                <!-- Checkmark animado -->
                <Transition
                  enter-active-class="transition duration-300 ease-out"
                  enter-from-class="opacity-0 scale-50"
                  enter-to-class="opacity-100 scale-100"
                  leave-active-class="transition duration-200 ease-in"
                  leave-from-class="opacity-100 scale-100"
                  leave-to-class="opacity-0 scale-50"
                >
                  <div 
                    v-if="form.serviceId === service.id"
                    class="absolute top-2 right-2 w-6 h-6 gradient-bg rounded-full flex items-center justify-center"
                  >
                    <i class="fas fa-check text-white text-xs"></i>
                  </div>
                </Transition>
              </button>
            </div>
            <p v-if="errors.serviceId" class="mt-1 text-sm text-red-600">{{ errors.serviceId }}</p>
          </div>

          <!-- Datos del Cliente -->
          <div class="space-y-4 mb-6">
            <div class="relative">
              <Input
                v-model="form.customerName"
                label="Nombre del Cliente"
                placeholder="Ej: Juan Pérez"
                :error="errors.customerName"
                required
                icon="fas fa-user"
              />
              <Transition
                enter-active-class="transition duration-300 ease-out"
                enter-from-class="opacity-0 translate-y-2"
                enter-to-class="opacity-100 translate-y-0"
              >
                <div v-if="form.customerName" class="absolute right-3 top-9 text-green-500">
                  <i class="fas fa-check-circle"></i>
                </div>
              </Transition>
            </div>

            <Input
              v-model="form.customerPhone"
              label="Teléfono"
              placeholder="3001234567"
              :error="errors.customerPhone"
              icon="fas fa-phone"
            />

            <Input
              v-model="form.customerEmail"
              label="Email"
              type="email"
              placeholder="cliente@email.com"
              :error="errors.customerEmail"
              icon="fas fa-envelope"
            />
          </div>

          <!-- Prioridad con animación -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">Prioridad</label>
            <div class="flex gap-2">
              <button
                v-for="priority in priorities"
                :key="priority.value"
                type="button"
                class="flex-1 py-2 px-3 rounded-lg border transition-all duration-300 text-sm relative overflow-hidden"
                :class="[
                  form.priority === priority.value 
                    ? priority.selectedClass + ' scale-105 shadow-lg' 
                    : priority.class
                ]"
                @click="form.priority = priority.value"
              >
                <span class="relative z-10">{{ priority.label }}</span>
                <div 
                  v-if="form.priority === priority.value"
                  class="absolute inset-0 bg-white/20 animate-pulse"
                ></div>
              </button>
            </div>
          </div>

          <!-- Notas -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">Notas adicionales</label>
            <textarea
              v-model="form.notes"
              rows="3"
              class="w-full rounded-xl border-gray-200 shadow-sm focus:border-purple-500 focus:ring-purple-500 transition-all duration-300"
              placeholder="Información adicional para el operador..."
            />
          </div>

          <div class="flex gap-3">
            <Button 
              type="submit" 
              variant="primary" 
              :loading="loading"
              fullWidth
              class="relative overflow-hidden group"
            >
              <span class="relative z-10">
                <i class="fas fa-ticket-alt mr-2"></i> 
                {{ loading ? 'Generando...' : 'Generar Turno' }}
              </span>
              <div class="absolute inset-0 bg-white/20 transform -skew-x-12 -translate-x-full group-hover:translate-x-full transition-transform duration-700"></div>
            </Button>
            <Button type="button" variant="secondary" @click="resetForm">
              Limpiar
            </Button>
          </div>
        </form>
      </div>

      <!-- Turno Generado con animación -->
      <Transition
        enter-active-class="transition duration-500 ease-out"
        enter-from-class="opacity-0 scale-95 translate-y-4"
        enter-to-class="opacity-100 scale-100 translate-y-0"
        leave-active-class="transition duration-300 ease-in"
        leave-from-class="opacity-100 scale-100 translate-y-0"
        leave-to-class="opacity-0 scale-95 translate-y-4"
      >
        <div v-if="generatedTurn" class="mt-6 glass-card rounded-2xl p-8 text-center relative overflow-hidden">
          <!-- Confeti animado -->
          <div class="absolute inset-0 pointer-events-none">
            <div v-for="n in 20" :key="n" class="confetti" :style="confettiStyle(n)"></div>
          </div>

          <div class="w-20 h-20 gradient-bg rounded-2xl flex items-center justify-center mx-auto mb-4 animate-bounce">
            <i class="fas fa-check text-white text-3xl"></i>
          </div>
          
          <h2 class="text-2xl font-bold mb-2 animate-pulse">¡Turno Generado!</h2>
          
          <div class="text-5xl font-bold gradient-text mb-4 animate-float">
            {{ generatedTurn.ticketNumber }}
          </div>
          
          <p class="text-gray-600 mb-2">Cliente: {{ generatedTurn.customerName }}</p>
          <p class="text-gray-600 mb-6">Servicio: {{ generatedTurn.serviceName }}</p>
          
          <div class="flex gap-3">
            <Button variant="primary" @click="printTicket" class="flex-1">
              <i class="fas fa-print mr-2" /> Imprimir
            </Button>
            <Button variant="secondary" @click="generatedTurn = null" class="flex-1">
              Nuevo Turno
            </Button>
          </div>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useToast } from 'vue-toastification'
import Button from '../components/UI/Button.vue'
import Input from '../components/UI/Input.vue'

const store = useStore()
const toast = useToast()

const services = ref([])
const loading = ref(false)
const generatedTurn = ref(null)

const form = reactive({
  serviceId: null,
  customerName: '',
  customerPhone: '',
  customerEmail: '',
  priority: 'NORMAL',
  notes: ''
})

const errors = reactive({})

const priorities = [
  { value: 'LOW', label: 'Baja', class: 'bg-green-50 text-green-700 border-green-200 hover:bg-green-100', selectedClass: 'bg-green-500 text-white border-green-600' },
  { value: 'NORMAL', label: 'Normal', class: 'bg-blue-50 text-blue-700 border-blue-200 hover:bg-blue-100', selectedClass: 'bg-blue-500 text-white border-blue-600' },
  { value: 'HIGH', label: 'Alta', class: 'bg-orange-50 text-orange-700 border-orange-200 hover:bg-orange-100', selectedClass: 'bg-orange-500 text-white border-orange-600' },
  { value: 'VIP', label: 'VIP', class: 'bg-purple-50 text-purple-700 border-purple-200 hover:bg-purple-100', selectedClass: 'bg-purple-500 text-white border-purple-600' }
]

onMounted(async () => {
  await loadServices()
})

const loadServices = async () => {
  try {
    await store.dispatch('fetchServices')
    services.value = store.state.services
  } catch (error) {
    toast.error('Error al cargar servicios')
  }
}

const selectService = (service) => {
  form.serviceId = service.id
  if (errors.serviceId) delete errors.serviceId
  
  // Animación con GSAP
  if (window.gsap) {
    window.gsap.to('.selected', {
      scale: 1.1,
      duration: 0.3,
      yoyo: true,
      repeat: 1
    })
  }
}

const validateForm = () => {
  const newErrors = {}
  
  if (!form.serviceId) {
    newErrors.serviceId = 'Debe seleccionar un servicio'
  }
  
  if (!form.customerName || form.customerName.trim() === '') {
    newErrors.customerName = 'El nombre del cliente es requerido'
  }
  
  if (form.customerPhone && !/^\d{10}$/.test(form.customerPhone)) {
    newErrors.customerPhone = 'El teléfono debe tener 10 dígitos'
  }
  
  if (form.customerEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.customerEmail)) {
    newErrors.customerEmail = 'Email no válido'
  }
  
  Object.assign(errors, newErrors)
  return Object.keys(newErrors).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  try {
    const turn = await store.dispatch('createTurn', form)
    generatedTurn.value = turn
    resetForm()
    toast.success('Turno creado exitosamente')
    
    // Animación de confeti
    if (window.gsap) {
      window.gsap.to('.confetti', {
        y: 100,
        rotation: 360,
        duration: 2,
        stagger: 0.05,
        ease: 'power1.out'
      })
    }
  } catch (error) {
    toast.error('Error al crear el turno')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.serviceId = null
  form.customerName = ''
  form.customerPhone = ''
  form.customerEmail = ''
  form.priority = 'NORMAL'
  form.notes = ''
  Object.keys(errors).forEach(key => delete errors[key])
}

const printTicket = () => {
  window.print()
}

const confettiStyle = (index) => {
  const colors = ['#667eea', '#764ba2', '#48bb78', '#fbbf24', '#f87171']
  return {
    position: 'absolute',
    top: '-10px',
    left: Math.random() * 100 + '%',
    width: '8px',
    height: '8px',
    background: colors[index % colors.length],
    transform: `rotate(${Math.random() * 360}deg)`,
    animation: `confetti ${2 + Math.random() * 2}s ease-out forwards`
  }
}
</script>

<style scoped>
@keyframes confetti {
  to {
    transform: translateY(400px) rotate(720deg);
    opacity: 0;
  }
}

@keyframes shine {
  0% {
    transform: translateX(-100%) skewX(-12deg);
  }
  100% {
    transform: translateX(200%) skewX(-12deg);
  }
}

.animate-shine {
  animation: shine 1.5s infinite;
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}
</style>