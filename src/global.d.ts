interface ImportMeta {
    env: Record<string, any>;
  }
  declare module '*.vue' {
    import { DefineComponent } from 'vue'
    const component: DefineComponent<{}, {}, any>
    export default component
  }