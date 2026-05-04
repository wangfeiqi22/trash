import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import OrderCreateView from '../OrderCreateView.vue'

vi.mock('@/api', () => ({ default: { post: vi.fn(), get: vi.fn() } }))

const mockRouter = { push: vi.fn() }

const elStubs = {
  AppHeader: { template: '<div />' },
  'el-form': { template: '<form><slot /></form>' },
  'el-form-item': { template: '<div class="el-form-item"><slot /></div>' },
  'el-select': { template: '<div class="el-select"><slot /></div>' },
  'el-option': { template: '<div class="el-option" />' },
  'el-date-picker': { template: '<div class="el-date-picker" />' },
  'el-input': { template: '<div class="el-input"><slot /></div>' },
  'el-upload': { template: '<div class="el-upload" />' },
  'el-button': { template: '<button class="el-button"><slot /></button>' },
  'el-icon': { template: '<i />' },
  Plus: { template: '<i />' }
}

describe('OrderCreateView', () => {
  it('renders order create form', async () => {
    const wrapper = mount(OrderCreateView, {
      global: { mocks: { $router: mockRouter }, stubs: elStubs }
    })
    expect(wrapper.text()).toContain('预约清运')
  })

  it('renders form items', async () => {
    const wrapper = mount(OrderCreateView, {
      global: { mocks: { $router: mockRouter }, stubs: elStubs }
    })
    expect(wrapper.findAll('.el-form-item').length).toBeGreaterThanOrEqual(4)
  })

  it('renders submit button', async () => {
    const wrapper = mount(OrderCreateView, {
      global: { mocks: { $router: mockRouter }, stubs: elStubs }
    })
    expect(wrapper.find('.el-button').exists()).toBe(true)
  })
})
