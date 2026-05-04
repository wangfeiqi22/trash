import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import DriverProfileView from '../DriverProfileView.vue'

vi.mock('@/api', () => ({ default: { get: vi.fn() } }))

const mockRouter = { push: vi.fn() }

const elStubs = {
  AppHeader: { template: '<div />' },
  'el-card': { template: '<div class="el-card"><slot /></div>' },
  'el-avatar': { template: '<div class="el-avatar"><slot /></div>' },
  'el-tag': { template: '<span class="el-tag"><slot /></span>' },
  'el-button': { template: '<button class="el-button"><slot /></button>' },
  'el-row': { template: '<div><slot /></div>' },
  'el-col': { template: '<div><slot /></div>' }
}

describe('DriverProfileView', () => {
  it('renders driver profile', async () => {
    const wrapper = mount(DriverProfileView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: { ...elStubs, 'ElMessageBox': { install: () => ({ confirm: vi.fn().mockResolvedValue() }) } }
      }
    })
    expect(wrapper.text()).toContain('司机中心')
  })

  it('shows avatar placeholder', async () => {
    const wrapper = mount(DriverProfileView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: elStubs
      }
    })
    expect(wrapper.find('.el-avatar').exists()).toBe(true)
  })

  it('shows stats cards', async () => {
    const wrapper = mount(DriverProfileView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: elStubs
      }
    })
    expect(wrapper.findAll('.stat-item').length).toBe(3)
  })

  it('shows logout button', async () => {
    const wrapper = mount(DriverProfileView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: { ...elStubs, 'ElMessageBox': { install: () => ({ confirm: vi.fn().mockResolvedValue() }) } }
      }
    })
    const buttons = wrapper.findAll('button')
    expect(buttons.some(b => b.text().includes('退出'))).toBe(true)
  })
})
