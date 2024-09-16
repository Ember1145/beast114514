import { ref } from 'vue'
import { defineStore } from 'pinia'
import { userShareAndMy } from '@/api/user/user'

export const useUserShareAndMyStore = defineStore(
  'UserShareAndMyStore',
  () => {
    const CombineHistories = ref<Record<string, CombinedState>>({})
    interface CombinedState {
      combinedTweet?: Tweet[]
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
    const loadData = async (emailCut, current,routePath) => {
      if (!CombineHistories.value[routePath]) {
        CombineHistories.value[routePath] = {
          combinedTweet: [],
          current: 1,
          allDataLoaded: false 
        }
      }
      if(CombineHistories.value[routePath].allDataLoaded){
        return;
      }
      const response = await userShareAndMy(emailCut, current)
      if (response.data.length === 0) {
        CombineHistories.value[routePath].allDataLoaded = true; 
        return;
      }
      CombineHistories.value[routePath].combinedTweet = [...CombineHistories.value[routePath].combinedTweet,...response.data]
      CombineHistories.value[routePath].current += 1
      console.log('combined',CombineHistories.value[routePath].combinedTweet)
    }
    const savePageState = (routePath, state: CombinedState) => {
      CombineHistories.value[routePath] = {
        ...CombineHistories.value[routePath], // 展开任何现有的路由状态
        ...state
      }
    }
    const pushItem = (item: Tweet, path) => {
      const already = CombineHistories.value[path].combinedTweet.some(
        (tweet) => tweet.tweetId === item.tweetId
      );
      if (!already) {
        CombineHistories.value[path].combinedTweet.unshift(item);
      }
    };
    const delItem = (tweetId: string, path) => {
      const index = CombineHistories.value[path].combinedTweet.findIndex((item) => item.tweetId === tweetId);
      if(index !== -1) {
        CombineHistories.value[path].combinedTweet.splice(index, 1);
      }
    };
    const getPageState = (routePath) => {
      return CombineHistories.value[routePath]
    }
    const clearHistory = () => {
      CombineHistories.value = {}
    }
    return { savePageState, getPageState, CombineHistories, clearHistory, loadData,pushItem,delItem}
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'ReplyHistory',
          storage: sessionStorage
        }
      ]
    }
  }
)
