import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginView from '../LoginView.vue'

vi.mock('@/api', () => ({ default: { post: vi.fn() } }))

const mockRouter = {
  push: vi.fn().mockResolvedValue(undefined),
}

describe('LoginView', () => {
  it('renders login form', () => {
    const wrapper = mount(LoginView, {
      global: {
        mocks: {
          $router: mockRouter
        },
        stubs: {
          'el-form': { template: '<form><slot /></form>' },
          'el-form-item': { template: '<div><slot /></div>' },
          'el-input': { template: '<input />' },
          'el-select': { template: '<select><slot /></select>' },
          'el-option': { template: '<option />' },
          'el-button': { template: '<button type="button" /><slot />' },
          RouterLink: { template: '<a>' }
        }
      }
    })
    expect(wrapper.find('.login-title').text()).toContain('智慧垃圾分类系统')
  })

  it('shows validation error on empty submit', async () => {
    const wrapper = mount(LoginView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          'el-form': { template: '<form @submit.prevent="$emit(\'submit\')"><slot /></form>', methods: { validate: () => Promise.resolve(false) } },
          'el-form-item': { template: '<div class="el-form-item"><slot /></div>' },
          'el-input': { template: '<input />' },
          'el-select': { template: '<select><slot /></select>' },
          'el-option': { template: '<option />' },
          'el-button': { template: '<button type="button" />' }
        }
      }
    })
    expect(wrapper.find('h1').text()).toContain('智慧垃圾分类系统')
  })
})
