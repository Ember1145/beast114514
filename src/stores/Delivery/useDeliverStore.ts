import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getDeliver} from '@/api/twi/twi'

export const useDeliverStore = defineStore(
  'deliverStore',
  () => {
    const histories = ref<Tweet[]>([])
    interface Tweet {
      userId: string
      username: string
      emailCut: string
      content: string
      avatarUrl: string
      tweetId: string
      parentId: string
      media: Array<any>
      createdAt: string
      realParent:string
    }
    const loadData = async () => {
      const response = await getDeliver()
      if(response.data===null){
        return
      }
      histories.value.unshift(...response.data)
    }
    return {
      loadData,
      histories,
    }
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'deliverStore',
          storage: sessionStorage
        }
      ]
    }
  }
)
