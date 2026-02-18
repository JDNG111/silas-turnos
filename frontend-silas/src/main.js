import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'

// Configurar axios
axios.defaults.baseURL = 'http://localhost:3000'

// Hacer GSAP disponible globalmente
window.gsap = gsap

const app = createApp(App)

app.use(router)
app.use(store)

app.mount('#app')