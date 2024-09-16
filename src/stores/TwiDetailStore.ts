import { ref } from 'vue'
import { defineStore } from 'pinia'
import { twiDetail } from '@/api/twi/twi'

export const TwiDetailStore = defineStore(
  'TwiDetailStore',
  () => {
    const histories = ref<Record<string, PageState>>({})
    interface PageState {
      top?: Tweet[][]
      center?: Tweet
      combinedComments: Tweet[] | Tweet[][]
      current: number
      allDataLoaded?: boolean
    }
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

    const loadData = async (emailCut, tweetId, path) => {
      if (!histories.value[path]) {
        histories.value[path] = {
          top: [],
          center: undefined,
          combinedComments: [],
          current: 1,
          allDataLoaded: false
        }
      }
      if (histories.value[path].allDataLoaded) {
        return
      }
      const response = await twiDetail(emailCut, tweetId, histories.value[path].current)
      if (response.data.length === 0) {
        histories.value[path].allDataLoaded = true
        return
      }
      const flattened = response.data.topChain.flat();
      histories.value[path].center = flattened.pop();
      histories.value[path].top=[flattened]
      histories.value[path].combinedComments.push(response.data.focusChains)
      histories.value[path].combinedComments.push(response.data.commentVOList)
      histories.value[path].current += 1
    }
    // const pushItem=()=>{

    // }
    const clearHistory = () => {
      histories.value = {}
    }
    return {
      top,
      loadData,
      histories,
      clearHistory
    }
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'TweetIn',
          storage: sessionStorage
        }
      ]
    }
  }
)
