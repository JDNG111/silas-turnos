<template>
  <div class="min-h-screen bg-gray-50">
    <Navbar @toggle-sidebar="sidebarOpen = !sidebarOpen" />
    
    <Sidebar
      :is-open="sidebarOpen"
      @close="sidebarOpen = false"
    />
    
    <main class="lg:pl-64 pt-16">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Navbar from './components/Layout/Navbar.vue'
import Sidebar from './components/Layout/Sidebar.vue'

const sidebarOpen = ref(false)
</script>

<style>
/* Estilos globales */
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>