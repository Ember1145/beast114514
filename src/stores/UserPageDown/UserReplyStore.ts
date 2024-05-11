import { ref } from 'vue'
import { defineStore } from 'pinia'
import { userReply } from '@/api/user/user'

export const useUserReplyStore = defineStore(
  'userReplyStore',
  () => {
    const replyHistories = ref<Record<string, replyState>>({})
    interface replyState {
      replyTweet?: Tweet[][]
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
    }
    const loadData = async (emailCut, current,routePath) => {
      if (!replyHistories.value[routePath]) {
        replyHistories.value[routePath] = {
          replyTweet: [],
          current: 1,
          allDataLoaded: false
        }
      }
      if(replyHistories.value[routePath].allDataLoaded){
        return;
      }
      const response = await userReply(emailCut, current)
      if (response.data.length === 0) {
        replyHistories.value[routePath].allDataLoaded = true; 
        return;
      }
      replyHistories.value[routePath].replyTweet = [...replyHistories.value[routePath].replyTweet,...response.data]
      replyHistories.value[routePath].current += 1
    }
    const savePageState = (routePath, state: replyState) => {
      replyHistories.value[routePath] = {
        ...replyHistories.value[routePath], // 展开任何现有的路由状态
        ...state
      }
    }
    const pushItem = (item: Tweet, path: string) => {
      const already = replyHistories.value[path].replyTweet.some(
        (tweet) => tweet.tweetId === item.tweetId
      );
      if (!already) {
        replyHistories.value[path].replyTweet.unshift(item);
      }
    };
    const getPageState = (routePath) => {
      return replyHistories.value[routePath]
    }
    const clearHistory = () => {
      replyHistories.value = {}
    }
    return { savePageState, getPageState, replyHistories, clearHistory, loadData,pushItem}
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
