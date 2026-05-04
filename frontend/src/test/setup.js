import { vi } from 'vitest'

vi.stubGlobal('localStorage', {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
})

vi.stubGlobal('navigator', {
  ...navigator,
  geolocation: {
    getCurrentPosition: vi.fn((success) =>
      success({ coords: { latitude: 31.23, longitude: 121.47 } })
    )
  }
})
