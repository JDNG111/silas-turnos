import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: DashboardView
  },
  {
    path: '/turns',
    name: 'turns',
    component: () => import('../views/TurnsView.vue')
  },
  {
    path: '/new-turn',
    name: 'new-turn',
    component: () => import('../views/NewTurnView.vue')
  },
  {
    path: '/operator',
    name: 'operator',
    component: () => import('../views/OperatorPanelView.vue')
  },
  {
    path: '/display',
    name: 'display',
    component: () => import('../views/DisplayView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router