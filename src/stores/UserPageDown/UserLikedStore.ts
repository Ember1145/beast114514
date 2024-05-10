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
      if (!likeHistories.value[routePath]) {
        likeHistories.value[routePath] = {
          likedTweet: [],
          current: 1
        }
      }
      const response = await userLike(emailCut, current)
      likeHistories.value[routePath].likedTweet = response.data
      likeHistories.value[routePath].current += 1
    }
    const savePageState = (routePath, state: LikeState) => {
      likeHistories.value[routePath] = {
        ...likeHistories.value[routePath], // 展开任何现有的路由状态
        ...state
      }
    }
    const pushArray = (newArr: Tweet[], path) => {
      likeHistories.value[path].likedTweet.push(...newArr);  
    };
    const pushItem = (item: Tweet, path) => {
      likeHistories.value[path].likedTweet.unshift(item);  
    };
    // const delItem = (item: Tweet, path) => {
    //   const index = likeHistories.value[path].likedTweet.findIndex((tweet: Tweet) => tweet.tweetId === item.tweetId);
    //   if (index !== -1) {
    //     likeHistories.value[path].likedTweet.splice(index, 1);
    //   }
    // };
    const getPageState = (routePath) => {
      return likeHistories.value[routePath]
    }
    const clearHistory = () => {
      likeHistories.value = {}
    }
    return { savePageState, getPageState, likeHistories, clearHistory, loadData,pushArray,pushItem}
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
