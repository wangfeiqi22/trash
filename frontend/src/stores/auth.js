import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('token') || null)
  const refreshToken = ref(localStorage.getItem('refreshToken') || null)

  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || null)

  function setUser(newUser) {
    user.value = newUser
    if (newUser) {
      localStorage.setItem('user', JSON.stringify(newUser))
    } else {
      localStorage.removeItem('user')
    }
  }

  function setTokens(accessToken, newRefreshToken) {
    token.value = accessToken
    if (accessToken) {
      localStorage.setItem('token', accessToken)
    }
    if (newRefreshToken !== undefined) {
      refreshToken.value = newRefreshToken
      if (newRefreshToken) {
        localStorage.setItem('refreshToken', newRefreshToken)
      } else {
        localStorage.removeItem('refreshToken')
      }
    }
  }

  function clearAuth() {
    user.value = null
    token.value = null
    refreshToken.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  return { user, token, refreshToken, isLoggedIn, userRole, setUser, setTokens, clearAuth }
})
