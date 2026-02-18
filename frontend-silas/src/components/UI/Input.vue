<template>
  <div class="w-full">
    <label v-if="label" :for="id" class="block text-sm font-medium text-secondary-700 mb-1">
      {{ label }}
      <span v-if="required" class="text-red-500">*</span>
    </label>
    <div class="relative">
      <div v-if="icon" class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
        <i :class="[icon, 'text-secondary-400']" />
      </div>
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :required="required"
        class="block w-full rounded-xl border-secondary-200 shadow-sm transition-all duration-200 focus:border-primary-500 focus:ring-primary-500 disabled:bg-secondary-50 disabled:cursor-not-allowed"
        :class="[icon ? 'pl-10' : 'pl-4', error ? 'border-red-300 focus:border-red-500 focus:ring-red-500' : '']"
        @input="$emit('update:modelValue', $event.target.value)"
        @blur="$emit('blur')"
      />
    </div>
    <p v-if="error" class="mt-1 text-sm text-red-600">{{ error }}</p>
    <p v-if="hint" class="mt-1 text-sm text-secondary-500">{{ hint }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  },
  hint: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  id: {
    type: String,
    default: () => `input-${Math.random().toString(36).substr(2, 9)}`
  }
})

defineEmits(['update:modelValue', 'blur'])
</script>