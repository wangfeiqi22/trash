import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/HomeDashboardView.vue')
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('../views/ChatView.vue')
  },
  {
    path: '/ai-assistant',
    redirect: '/chat'
  },
  {
    path: '/order/create',
    name: 'OrderCreate',
    component: () => import('../views/OrderCreateView.vue')
  },
  {
    path: '/order/history',
    name: 'OrderHistory',
    component: () => import('../views/OrderListView.vue')
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetailView.vue')
  },
  {
    path: '/order/list',
    redirect: '/order/history'
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboardView.vue')
  },
  {
    path: '/admin/kb',
    name: 'KnowledgeBase',
    component: () => import('../views/KnowledgeBaseView.vue')
  },
  {
    path: '/admin/sf',
    name: 'StationFleet',
    component: () => import('../views/StationFleetView.vue')
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../views/AdminUserAuditView.vue')
  },
  {
    path: '/driver/tasks',
    name: 'DriverTasks',
    component: () => import('../views/DriverTaskView.vue')
  },
  {
    path: '/driver/orders',
    name: 'DriverOrderList',
    component: () => import('../views/DriverOrderListView.vue')
  },
  {
    path: '/driver/order/:id',
    name: 'DriverOrderDetail',
    component: () => import('../views/DriverOrderDetailView.vue')
  },
  {
    path: '/user/profile',
    redirect: '/profile/user'
  },
  {
    path: '/vip/dashboard',
    redirect: '/profile/enterprise'
  },
  {
    path: '/admin/personal',
    redirect: '/admin/dashboard'
  },
  {
    path: '/profile/user',
    name: 'UserProfile',
    component: () => import('../views/UserProfileView.vue')
  },
  {
    path: '/profile/enterprise',
    name: 'EnterpriseProfile',
    component: () => import('../views/EnterpriseProfileView.vue')
  },
  {
    path: '/profile/driver',
    name: 'DriverProfile',
    component: () => import('../views/DriverProfileView.vue')
  },
  {
    path: '/property/dashboard',
    name: 'PropertyDashboard',
    component: () => import('../views/PropertyDashboardView.vue')
  },
  {
    path: '/station/work',
    name: 'StationWork',
    component: () => import('../views/StationWorkView.vue')
  },
  {
    path: '/mall',
    name: 'Mall',
    component: () => import('../views/MallView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null

  const publicRoutes = ['/login', '/register', '/mall']

  if (publicRoutes.includes(to.path)) {
    if (token && user && (to.path === '/login' || to.path === '/register')) {
      return next('/chat')
    }
    return next()
  }

  if (!token || !user) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  const role = user.role

  if (to.path.startsWith('/admin') && role !== 'admin') {
    return next('/home')
  }

  if (to.path.startsWith('/driver') && role !== 'driver') {
    return next('/home')
  }

  if (to.path.startsWith('/property') && role !== 'property') {
    return next('/home')
  }

  if (to.path.startsWith('/station') && role !== 'station') {
    return next('/home')
  }

  next()
})

export default router
