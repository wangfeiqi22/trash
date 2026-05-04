import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import DriverTaskView from '../DriverTaskView.vue'
import { createRouter, createWebHistory } from 'vue-router'

vi.mock('@/api', () => ({ default: { get: vi.fn(), post: vi.fn() } }))

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/driver/tasks', name: 'DriverTasks' }]
})

const elStubs = {
  AppHeader: { template: '<div />' },
  'el-tabs': { template: '<div class="el-tabs"><slot /></div>' },
  'el-tab-pane': { template: '<div class="el-tab-pane"><slot /></div>' },
  'el-card': { template: '<div class="el-card"><slot /></div>' },
  'el-button': { template: '<button class="el-button"><slot /></button>' },
  'el-empty': { template: '<div class="el-empty"><slot /></div>' },
  'el-tag': { template: '<span class="el-tag"><slot /></span>' }
}

describe('DriverTaskView', () => {
  it('renders driver task view', async () => {
    const wrapper = mount(DriverTaskView, {
      global: { plugins: [router], stubs: elStubs }
    })
    expect(wrapper.text()).toContain('我的任务')
  })

  it('renders tabs', async () => {
    const wrapper = mount(DriverTaskView, {
      global: { plugins: [router], stubs: elStubs }
    })
    expect(wrapper.find('.el-tabs').exists()).toBe(true)
  })

  it('shows empty state when no orders', async () => {
    const api = (await import('@/api')).default
    api.get.mockResolvedValue({ data: { pendingOrders: [], activeOrders: [] } })
    const wrapper = mount(DriverTaskView, {
      global: { plugins: [router], stubs: elStubs }
    })
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.find('.el-empty').exists()).toBe(true)
  })

  it('displays tab panes', async () => {
    const wrapper = mount(DriverTaskView, {
      global: { plugins: [router], stubs: elStubs }
    })
    expect(wrapper.findAll('.el-tab-pane').length).toBeGreaterThanOrEqual(1)
  })
})
