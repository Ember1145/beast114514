import { ref } from 'vue'
import { defineStore } from 'pinia'
import { userReply } from '@/api/user/user'
import { myStore } from '@/stores/myStore'

export const useUserReplyStore = defineStore(
  'userReplyStore',
  () => {
    const my = myStore()
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
      realParent:string
    }
    const loadData = async (emailCut, current, routePath) => {
      if (!replyHistories.value[routePath]) {
        replyHistories.value[routePath] = {
          replyTweet: [],
          current: 1,
          allDataLoaded: false
        }
      }
      if (replyHistories.value[routePath].allDataLoaded) {
        return
      }
      const response = await userReply(emailCut, current)
      if (response.data.length === 0) {
        replyHistories.value[routePath].allDataLoaded = true
        return
      }
      replyHistories.value[routePath].replyTweet = [
        ...replyHistories.value[routePath].replyTweet,
        ...response.data
      ]
      replyHistories.value[routePath].current += 1
    }
   
    const pushItem = (items: Tweet[], path: string) => {
      replyHistories.value[path].replyTweet.unshift(items);
    }
    const delItem = (tweetId) => {
        const path = `/${my.emailCut}/replies`
        for (let i = 0; i < replyHistories.value[path].replyTweet.length; i++) {
        // 获取当前子数组中的最后一个元素
        const lastItemOfChain = replyHistories.value[path].replyTweet[i][replyHistories.value[path].replyTweet[i].length - 1];
        // 检查当前子数组的最后一个元素是否是我们要找的元素
        if (lastItemOfChain.tweetId===tweetId) {
          // 如果找到匹配的回复链，则删除这个子数组
          replyHistories.value[path].replyTweet.splice(i, 1);
          // 既然找到匹配项，就不需要再遍历更多的子数组了
          break;
        }
      }
    }
    // const delItem = (tweetId) => {
    //   const path = `/${my.emailCut}/replies`
    //   const removeAndTrim = (items, emailCut) => {
    //     const indexToDelete = items.findIndex((item) => item.tweetId === tweetId)
    //     const isFirstMatch = items[0] && items[0].emailCut === emailCut
    //     const matchCount = items.slice(1).filter((item) => item.emailCut === emailCut).length
    //     if (indexToDelete !== -1) {
    //       if (isFirstMatch && matchCount === 1) {
    //         return [];
    //       }
    //       const tem = items.splice(0,indexToDelete)
    //       // 反转数组以便从末尾向上寻找与 emailCut 相同的推文索引
    //       const reversedItems = tem.slice().reverse() // 浅拷贝数组再反转
    //       const indexToSlice = reversedItems.findIndex((now) => now.emailCut === emailCut)
    //       // 如果找到匹配的推文，则进行修剪
    //       if (indexToSlice !== -1) {
    //         return reversedItems.slice(indexToSlice).reverse() // 再次反转以恢复原始顺序
    //       }
    //       return []
    //     }
    //     return items
    //   }
    //   replyHistories.value[path].replyTweet = replyHistories.value[path].replyTweet.map((items) =>
    //     removeAndTrim(items, my.emailCut)
    //   )
    //   if (path !== route.path) {
    //     replyHistories.value[route.path].replyTweet = replyHistories.value[
    //       route.path
    //     ].replyTweet.map((items) => removeAndTrim(items, route.params.emailCut))
    //   }
    // }
    const getPageState = (routePath) => {
      return replyHistories.value[routePath]
    }
    const clearHistory = () => {
      replyHistories.value = {}
    }
    return {
      getPageState,
      replyHistories,
      clearHistory,
      loadData,
      pushItem,
      delItem
    }
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
