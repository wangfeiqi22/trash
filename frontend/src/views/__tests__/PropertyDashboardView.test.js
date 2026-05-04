import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import PropertyDashboardView from '../PropertyDashboardView.vue'
import { createRouter, createWebHistory } from 'vue-router'

vi.mock('@/api', () => ({ default: { get: vi.fn() } }))

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/property/dashboard', name: 'PropertyDashboard' }]
})

const elStubs = {
  AppHeader: { template: '<div />' },
  'el-table': { template: '<div class="el-table"><slot /></div>' },
  'el-table-column': { template: '<div class="el-table-column"><slot /></div>' },
  'el-card': { template: '<div class="el-card"><slot /></div>' },
  'el-tag': { template: '<span class="el-tag"><slot /></span>' },
  'el-button': { template: '<button class="el-button"><slot /></button>' },
  'el-row': { template: '<div class="el-row"><slot /></div>' },
  'el-col': { template: '<div class="el-col"><slot /></div>' }
}

describe('PropertyDashboardView', () => {
  it('renders property dashboard', async () => {
    const wrapper = mount(PropertyDashboardView, {
      global: { plugins: [router], stubs: elStubs }
    })
    expect(wrapper.text()).toContain('物业工作台')
  })

  it('shows stat cards', async () => {
    const wrapper = mount(PropertyDashboardView, {
      global: { plugins: [router], stubs: elStubs }
    })
    expect(wrapper.findAll('.el-card').length).toBeGreaterThanOrEqual(1)
  })

  it('displays stats numbers', async () => {
    const wrapper = mount(PropertyDashboardView, {
      global: { plugins: [router], stubs: elStubs }
    })
    expect(wrapper.findAll('.stat-num').length).toBe(3)
  })
})
