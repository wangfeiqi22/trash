import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import OrderDetailView from '../OrderDetailView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/order/detail/:id', name: 'OrderDetail' }]
})

vi.mock('@/api', () => ({ default: { get: vi.fn(), delete: vi.fn() } }))

const elStubs = {
  AppHeader: { template: '<div />' },
  'el-card': { template: '<div class="el-card"><slot /></div>' },
  'el-descriptions': { template: '<div class="el-descriptions"><slot /></div>' },
  'el-descriptions-item': { template: '<div class="el-descriptions-item"><slot /></div>' },
  'el-tag': { template: '<span class="el-tag"><slot /></span>' },
  'el-button': { template: '<button class="el-button"><slot /></button>' },
  'el-image': { template: '<div class="el-image"><slot /></div>' },
  'el-icon': { template: '<i />' },
  Loading: { template: '<i class="loading-icon" />' },
  'el-row': { template: '<div><slot /></div>' },
  'el-col': { template: '<div><slot /></div>' }
}

const mockOrderData = {
  id: 1, status: 'pending', statusText: '待处理', wasteTypeText: '可回收物',
  pickupTime: '2024-01-01 10:00', pickupAddress: '测试地址', createdAt: '2024-01-01'
}

describe('OrderDetailView', () => {
  beforeEach(() => { vi.clearAllMocks() })
  afterEach(() => { router.push('/') })

  it('renders order detail view', async () => {
    const api = (await import('@/api')).default
    api.get.mockResolvedValue({ data: mockOrderData })
    const wrapper = mount(OrderDetailView, {
      global: { plugins: [router], stubs: elStubs }
    })
    await router.isReady()
    await new Promise(r => setTimeout(r, 100))
    expect(wrapper.find('h2').exists()).toBe(true)
  })

  it('shows loading state initially', async () => {
    const api = (await import('@/api')).default
    api.get.mockImplementation(() => new Promise(() => {}))
    const wrapper = mount(OrderDetailView, {
      global: { plugins: [router], stubs: elStubs }
    })
    await router.isReady()
    expect(wrapper.find('.loading').exists()).toBe(true)
  })

  it('shows cancel button for pending orders', async () => {
    const api = (await import('@/api')).default
    api.get.mockResolvedValue({ data: mockOrderData })
    const wrapper = mount(OrderDetailView, {
      global: { plugins: [router], stubs: elStubs }
    })
    await router.isReady()
    await new Promise(r => setTimeout(r, 100))
    expect(wrapper.find('.detail-actions').exists()).toBe(true)
  })
})
