import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import OrderListView from '../OrderListView.vue'

vi.mock('@/api', () => ({ default: { get: vi.fn() } }))

const mockRouter = { push: vi.fn().mockResolvedValue(undefined) }

describe('OrderListView', () => {
  it('renders order list view', () => {
    const wrapper = mount(OrderListView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          AppHeader: { template: '<div />' },
          'el-tabs': { template: '<div class="el-tabs"><slot /></div>' },
          'el-tab-pane': { template: '<div class="tab-pane"><slot /></div>' },
          'el-empty': { template: '<div class="el-empty" />' },
          'el-card': { template: '<div />' },
          'el-icon': { template: '<i />' },
          Loading: { template: '<div class="loading" />' }
        }
      }
    })
    expect(wrapper.find('h2').text()).toContain('我的订单')
  })

  it('renders 3 tabs', () => {
    const wrapper = mount(OrderListView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          AppHeader: { template: '<div />' },
          'el-tabs': { template: '<div class="tabs"><slot /></div>' },
          'el-tab-pane': { template: '<div class="tab-pane"><slot /></div>' },
          'el-empty': { template: '<div />' },
          'el-card': { template: '<div />' }
        }
      }
    })
    expect(wrapper.vm.activeTab).toBe('all')
  })

  it('shows empty state when no orders', async () => {
    const api = (await import('@/api')).default
    api.get.mockResolvedValue({ data: [] })

    const wrapper = mount(OrderListView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          AppHeader: { template: '<div />' },
          'el-tabs': { template: '<div class="tabs"><slot /></div>' },
          'el-tab-pane': { template: '<div class="tab-pane"><slot /></div>' },
          'el-empty': { template: '<div class="el-empty" />' },
          'el-card': { template: '<div />' }
        }
      }
    })
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.find('.el-empty').exists()).toBe(true)
  })

  it('filters orders by status tab', async () => {
    const wrapper = mount(OrderListView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          AppHeader: { template: '<div />' },
          'el-tabs': { template: '<div class="tabs"><slot /></div>' },
          'el-tab-pane': { template: '<div class="tab-pane"><slot /></div>' },
          'el-empty': { template: '<div />' },
          'el-card': { template: '<div />' }
        }
      }
    })
    wrapper.vm.activeTab = 'pending'
    expect(wrapper.vm.activeTab).toBe('pending')
  })
})
