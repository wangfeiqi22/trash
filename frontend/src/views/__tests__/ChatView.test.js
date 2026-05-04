import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ChatView from '../ChatView.vue'

vi.mock('@/api', () => ({ default: { post: vi.fn() } }))

const mockRouter = { push: vi.fn() }

describe('ChatView', () => {
  it('renders chat view', async () => {
    const wrapper = mount(ChatView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          AppHeader: { template: '<div />' },
          'el-input': { template: '<div class="el-input"><slot /></div>' },
          'el-button': { template: '<button><slot /></button>' },
          'el-icon': { template: '<i />' }
        }
      }
    })
    expect(wrapper.find('.chat-messages').exists()).toBe(true)
  })

  it('shows initial greeting message', async () => {
    const wrapper = mount(ChatView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          AppHeader: { template: '<div />' },
          'el-input': { template: '<div class="el-input"><slot /></div>' },
          'el-button': { template: '<button><slot /></button>' },
          'el-icon': { template: '<i />' }
        }
      }
    })
    const messages = wrapper.findAll('.message')
    expect(messages.length).toBeGreaterThan(0)
    expect(messages[0].text()).toContain('智能垃圾分类助手')
  })

  it('renders chat input', async () => {
    const wrapper = mount(ChatView, {
      global: {
        mocks: { $router: mockRouter },
        stubs: {
          AppHeader: { template: '<div />' },
          'el-input': { template: '<div class="el-input"><input class="chat-input" /></div>' },
          'el-button': { template: '<button><slot /></button>' },
          'el-icon': { template: '<i />' }
        }
      }
    })
    expect(wrapper.find('.chat-input').exists()).toBe(true)
  })
})
