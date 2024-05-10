import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useLeftStore = defineStore('leftStore', () => {
    const activeButton = ref(1)


    const setActiveButton= (button: any) => {
        activeButton.value = button;
        console.log(activeButton.value);
      }
    
    return{activeButton,setActiveButton}
    
},{
    persist:{
      enabled:true,//持久化存储
      strategies: [
        {
          storage: sessionStorage,
          paths: ['activeButton']
        }
      ]
  },
  })