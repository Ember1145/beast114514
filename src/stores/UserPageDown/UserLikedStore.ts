import { ref } from 'vue'
import { defineStore } from 'pinia'
import { userLike } from '@/api/user/user'

export const useUserLikedStore = defineStore(
  'userLikedStore',
  () => {
    const likeHistories = ref<Record<string, LikeState>>({})
    interface LikeState {
      likedTweet?: Tweet[]
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
      if (!likeHistories.value[routePath]) {
        likeHistories.value[routePath] = {
          likedTweet: [],
          current: 1,
          allDataLoaded: false
        }
      }
      if(likeHistories.value[routePath].allDataLoaded){
        return;
      }
      const response = await userLike(emailCut, current)
      if (response.data.length === 0) {
        likeHistories.value[routePath].allDataLoaded = true; 
        return;
      }
      likeHistories.value[routePath].likedTweet = [...likeHistories.value[routePath].likedTweet,...response.data]
      likeHistories.value[routePath].current += 1
    }
    const savePageState = (routePath, state: LikeState) => {
      likeHistories.value[routePath] = {
        ...likeHistories.value[routePath], // 展开任何现有的路由状态
        ...state
      }
    }
    const pushItem = (item: Tweet, path: string) => {
      const alreadyLiked = likeHistories.value[path]?.likedTweet.some(
        (tweet) => tweet.tweetId === item.tweetId
      );
      if (!alreadyLiked) {
        likeHistories.value[path]?.likedTweet.unshift(item);
      }
    };
    const getPageState = (routePath) => {
      return likeHistories.value[routePath]
    }
    const clearHistory = () => {
      likeHistories.value = {}
    }
    return { savePageState, getPageState, likeHistories, clearHistory, loadData,pushItem}
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'likeHistory',
          storage: sessionStorage
        }
      ]
    }
  }
)
