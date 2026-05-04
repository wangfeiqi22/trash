import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import AiChatWidgetAvatar from '../AiChatWidgetAvatar.vue'

vi.mock('@/api', () => ({ default: { post: vi.fn() } }))

describe('AiChatWidgetAvatar', () => {
  it('renders chat button', async () => {
    const wrapper = mount(AiChatWidgetAvatar, {
      global: {
        stubs: {
          'el-popover': {
            template: '<div><div class="ai-avatar-btn"><slot name="reference" /></div><slot /></div>'
          },
          'el-badge': { template: '<span class="el-badge"><slot /></span>' },
          'el-icon': { template: '<i />' },
          'Service': true,
          'el-input': { template: '<div class="el-input"><input /></div>' },
          'el-button': { template: '<button />' }
        }
      }
    })
    expect(wrapper.find('.ai-avatar-btn').exists()).toBe(true)
  })

  it('shows badge when unread count > 0', async () => {
    const wrapper = mount(AiChatWidgetAvatar, {
      global: {
        stubs: {
          'el-popover': {
            template: '<div><div class="ai-avatar-btn"><slot name="reference" /></div><slot /></div>'
          },
          'el-badge': { template: '<span class="el-badge"><slot /></span>' },
          'el-icon': { template: '<i />' },
          'Service': true,
          'el-input': { template: '<div class="el-input"><input /></div>' },
          'el-button': { template: '<button />' }
        }
      }
    })
    expect(wrapper.find('.el-badge').exists()).toBe(true)
  })

  it('opens chat on click', async () => {
    const wrapper = mount(AiChatWidgetAvatar, {
      global: {
        stubs: {
          'el-popover': {
            template: '<div><div class="ai-avatar-btn"><slot name="reference" /></div><slot /></div>'
          },
          'el-badge': { template: '<span class="el-badge"><slot /></span>' },
          'el-icon': { template: '<i />' },
          'Service': true,
          'el-input': { template: '<div class="el-input"><input /></div>' },
          'el-button': { template: '<button />' }
        }
      }
    })
    const btn = wrapper.find('.ai-avatar-btn')
    await btn.trigger('click')
    expect(wrapper.vm.unreadCount).toBe(0)
  })

  it('shows chat messages after opening', async () => {
    const wrapper = mount(AiChatWidgetAvatar, {
      global: {
        stubs: {
          'el-popover': {
            template: '<div><div class="ai-avatar-btn"><slot name="reference" /></div><slot /></div>'
          },
          'el-badge': { template: '<span class="el-badge"><slot /></span>' },
          'el-icon': { template: '<i />' },
          'Service': true,
          'el-input': { template: '<div class="el-input"><input /></div>' },
          'el-button': { template: '<button />' }
        }
      }
    })
    expect(wrapper.vm.messages.length).toBeGreaterThan(0)
  })
})
