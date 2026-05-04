import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import HomeDashboardView from '../HomeDashboardView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/home', name: 'Home' }]
})

const elStubs = {
  'el-card': { template: '<div class="el-card" @click="$attrs.onClick"><slot /></div>' },
  'el-icon': { template: '<i />' },
  'el-row': { template: '<div><slot /></div>' },
  'el-col': { template: '<div><slot /></div>' },
  'Plus': true, 'List': true, 'ChatDotRound': true
}

describe('HomeDashboardView', () => {
  it('renders welcome message', () => {
    const wrapper = mount(HomeDashboardView, {
      global: {
        plugins: [router],
        stubs: { ...elStubs, AppHeader: { template: '<div class="app-header-stub" />' }, 'router-view': true, 'router-link': true }
      }
    })
    expect(wrapper.text()).toContain('欢迎使用智慧垃圾分类系统')
  })

  it('renders 3 action cards', () => {
    const wrapper = mount(HomeDashboardView, {
      global: {
        plugins: [router],
        stubs: { ...elStubs, AppHeader: { template: '<div />' }, 'router-view': true, 'router-link': true }
      }
    })
    expect(wrapper.findAll('.el-card').length).toBe(3)
  })

  it('navigates on action card click', async () => {
    const pushSpy = vi.spyOn(router, 'push')
    const wrapper = mount(HomeDashboardView, {
      global: {
        plugins: [router],
        stubs: { ...elStubs, AppHeader: { template: '<div />' }, 'router-view': true, 'router-link': true }
      }
    })
    const cards = wrapper.findAll('.el-card')
    await cards[0].trigger('click')
    expect(pushSpy).toHaveBeenCalledWith('/order/create')
  })

  it('renders AppHeader component', () => {
    const wrapper = mount(HomeDashboardView, {
      global: {
        plugins: [router],
        stubs: { ...elStubs, AppHeader: { template: '<div class="app-header" />' }, 'router-view': true, 'router-link': true }
      }
    })
    expect(wrapper.find('.app-header').exists()).toBe(true)
  })
})
