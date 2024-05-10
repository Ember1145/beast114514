import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useScrollStore = defineStore('scroll', ()=>{
    const savedPositions = ref<Record<string, number>>({})
    const savePosition = (routePath, state) => {
        savedPositions.value[routePath] = state       
      }
      const getPosition = (routePath) => {
        return savedPositions.value[routePath]
      }
      return{savePosition,savedPositions,getPosition}
},
{
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'scrollPosition',
          storage: sessionStorage
        }
      ]
    }
  });