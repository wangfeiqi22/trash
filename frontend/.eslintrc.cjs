module.exports = {
  env: {
    browser: true,
    es2021: true,
    'vue/setup-compiler-macros': true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  rules: {
    'vue/multi-word-component-names': 'off',
    'vue/no-v-html': 'warn',
    'no-unused-vars': 'warn',
    'no-console': 'off',
    'no-empty': 'off'
  }
}
