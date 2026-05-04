import js from '@eslint/js'
import vuePlugin from 'eslint-plugin-vue'

export default [
  js.configs.recommended,
  ...vuePlugin.configs['vue3-recommended'],
  {
    name: 'custom-rules',
    rules: {
      'vue/multi-word-component-names': 'off',
      'vue/no-v-html': 'warn',
      'no-unused-vars': 'warn',
      'no-console': 'off'
    }
  },
  {
    name: 'ignore-patterns',
    ignores: ['dist/**', 'node_modules/**', 'coverage/**', '*.config.js']
  }
]
