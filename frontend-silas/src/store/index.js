import { createStore } from 'vuex'
import axios from 'axios'

export default createStore({
  state: {
    turns: [],
    activeTurns: [],
    services: [],
    dashboard: null,
    loading: false,
    error: null
  },
  
  mutations: {
    SET_TURNS(state, turns) {
      state.turns = turns
    },
    SET_ACTIVE_TURNS(state, turns) {
      state.activeTurns = turns
    },
    SET_SERVICES(state, services) {
      state.services = services
    },
    SET_DASHBOARD(state, dashboard) {
      state.dashboard = dashboard
    },
    ADD_TURN(state, turn) {
      state.turns.push(turn)
    },
    UPDATE_TURN(state, updatedTurn) {
      const index = state.turns.findIndex(t => t.id === updatedTurn.id)
      if (index !== -1) {
        state.turns.splice(index, 1, updatedTurn)
      }
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    SET_ERROR(state, error) {
      state.error = error
    }
  },
  
  actions: {
    async fetchActiveTurns({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.get('/api/turns/active')
        commit('SET_ACTIVE_TURNS', response.data)
      } catch (error) {
        commit('SET_ERROR', error.message)
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchServices({ commit }) {
      try {
        const response = await axios.get('/api/services')
        commit('SET_SERVICES', response.data)
      } catch (error) {
        commit('SET_ERROR', error.message)
      }
    },
    
    async createTurn({ commit }, turnData) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.post('/api/turns', turnData)
        commit('ADD_TURN', response.data)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.message)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async callTurn({ commit, dispatch }, { id, attentionPoint }) {
      try {
        const response = await axios.put(`/api/turns/${id}/call?attentionPoint=${encodeURIComponent(attentionPoint)}`)
        commit('UPDATE_TURN', response.data)
        await dispatch('fetchActiveTurns')
        return response.data
      } catch (error) {
        throw error
      }
    },
    
    async startTurn({ commit, dispatch }, id) {
      try {
        const response = await axios.put(`/api/turns/${id}/start`)
        commit('UPDATE_TURN', response.data)
        await dispatch('fetchActiveTurns')
        return response.data
      } catch (error) {
        throw error
      }
    },
    
    async completeTurn({ commit, dispatch }, id) {
      try {
        const response = await axios.put(`/api/turns/${id}/complete`)
        commit('UPDATE_TURN', response.data)
        await dispatch('fetchActiveTurns')
        return response.data
      } catch (error) {
        throw error
      }
    },
    
    async cancelTurn({ commit, dispatch }, { id, reason }) {
      try {
        const response = await axios.put(`/api/turns/${id}/cancel`, { reason })
        commit('UPDATE_TURN', response.data)
        await dispatch('fetchActiveTurns')
        return response.data
      } catch (error) {
        throw error
      }
    }
  },
  
  getters: {
    pendingTurns: state => state.activeTurns.filter(t => t.status === 'PENDING'),
    calledTurns: state => state.activeTurns.filter(t => t.status === 'CALLED'),
    inProgressTurns: state => state.activeTurns.filter(t => t.status === 'IN_PROGRESS'),
    completedTurns: state => state.turns.filter(t => t.status === 'COMPLETED'),
    getTurnByTicket: state => ticketNumber => 
      state.turns.find(t => t.ticketNumber === ticketNumber)
  }
})