import { ref } from 'vue'
import { defineStore } from 'pinia'

export const TwiDetailStore = defineStore(
  'TwiDetailStore',
  () => {
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
    const top = ref<Tweet[][]>([])
    const center = ref<Tweet>()
    const pushItem = (newItem: Tweet) => {
      const newArr: Tweet[] = [newItem];
      combinedComments.value.push(newArr);
    }
    const combinedComments = ref<(Tweet[] | Tweet[][])[]>([]);
    const pushArray = (newArr: Tweet[]) => {
      combinedComments.value.push(newArr)
    }
    const loadComments = (chainDownComments:Tweet[][],endComments:Tweet[]) => {
      combinedComments.value=[]
      combinedComments.value.push(chainDownComments);
      combinedComments.value.push(endComments);
    };
    const addComments = (chainDownComments:Tweet[][],endComments:Tweet[]) => {
      combinedComments.value.push(chainDownComments);
      combinedComments.value.push(endComments);
    };
    
const clearState=()=>{
  top.value=[]
  center.value=undefined
}
    return {
      top,
      center,
      pushArray,
      clearState,
     combinedComments,
      pushItem,
      loadComments,
      addComments
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
