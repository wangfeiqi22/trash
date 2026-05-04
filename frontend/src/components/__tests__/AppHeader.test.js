import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import AppHeader from '../AppHeader.vue'
import { createRouter, createWebHistory } from 'vue-router'

vi.mock('@/api', () => ({ default: { get: vi.fn() } }))

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/', redirect: '/home' }]
})

const elStubs = {
  'el-dropdown': {
    template: '<div class="el-dropdown"><slot /></div>',
    name: 'ElDropdown'
  },
  'el-icon': { template: '<i />' },
  'Menu': true, 'User': true, 'ArrowDown': true,
  'el-dropdown-menu': { template: '<ul><slot /></ul>' },
  'el-dropdown-item': { template: '<li><slot /></li>' }
}

describe('AppHeader', () => {
  it('renders app title', async () => {
    const wrapper = mount(AppHeader, {
      global: { plugins: [router], stubs: elStubs }
    })
    await router.isReady()
    expect(wrapper.find('.app-title').text()).toContain('智慧垃圾分类')
  })

  it('shows user info when logged in', async () => {
    vi.stubGlobal('localStorage', {
      getItem: (key) => {
        if (key === 'user') return JSON.stringify({ username: 'testuser', role: 'admin' })
        return null
      },
      setItem: vi.fn(), removeItem: vi.fn(), clear: vi.fn()
    })
    const wrapper = mount(AppHeader, {
      global: { plugins: [router], stubs: elStubs }
    })
    await router.isReady()
    expect(wrapper.find('.user-info').text()).toContain('testuser')
  })

  it('renders user dropdown menu', async () => {
    vi.stubGlobal('localStorage', {
      getItem: (key) => {
        if (key === 'user') return JSON.stringify({ username: 'admin', role: 'admin' })
        return null
      },
      setItem: vi.fn(), removeItem: vi.fn(), clear: vi.fn()
    })
    const wrapper = mount(AppHeader, {
      global: { plugins: [router], stubs: elStubs }
    })
    await router.isReady()
    expect(wrapper.find('.el-dropdown').exists()).toBe(true)
  })
})
