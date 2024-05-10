import { ref } from 'vue'
import { defineStore } from 'pinia'

export const usePageHistoryStore = defineStore(
  'usePageHistoryStore',
  () => {
    const histories = ref<Record<string, PageState>>({})
    interface PageState {
      top?: Tweet[][]
      center?: Tweet
      combineComments: Tweet[] | Tweet[][]
      current: number
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
    }
    const savePageState = (routePath, state: PageState) => {
      histories.value[routePath] = {
        ...histories.value[routePath], // 展开任何现有的路由状态
        ...state
      }
    }
    const getPageState = (routePath) => {
      return histories.value[routePath]
    }
    const clearHistory = () => {
      histories.value = {}
    }
    return { savePageState, getPageState, histories, clearHistory }
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'TweetHistory',
          storage: sessionStorage
        }
      ]
    }
  }
)
