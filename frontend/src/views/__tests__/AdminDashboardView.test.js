import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminDashboardView from '../AdminDashboardView.vue'

vi.mock('@/api', () => ({ default: { get: vi.fn() } }))

const mockRouter = { push: vi.fn() }

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

describe('AdminDashboardView', () => {
  it('renders admin dashboard', async () => {
    const wrapper = mount(AdminDashboardView, {
      global: { mocks: { $router: mockRouter }, stubs: elStubs }
    })
    expect(wrapper.text()).toContain('管理后台')
  })

  it('renders stat cards', async () => {
    const wrapper = mount(AdminDashboardView, {
      global: { mocks: { $router: mockRouter }, stubs: elStubs }
    })
    expect(wrapper.findAll('.el-card').length).toBeGreaterThanOrEqual(1)
  })

  it('shows recent orders table', async () => {
    const wrapper = mount(AdminDashboardView, {
      global: { mocks: { $router: mockRouter }, stubs: elStubs }
    })
    expect(wrapper.find('.el-table').exists()).toBe(true)
  })
})
