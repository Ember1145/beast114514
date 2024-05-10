import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import PiniaPersist from 'pinia-plugin-persist'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import '@fortawesome/fontawesome-free/css/all.min.css'
import '@fortawesome/fontawesome-free/css/all.css'

import '@/assets/base.css'
const app = createApp(App)
const pinia = createPinia()
pinia.use(PiniaPersist)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(pinia)
app.use(router)
app.mount('#app')
