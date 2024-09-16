import { getLikeMsg } from '@/api/message'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useWebSocketStore = defineStore(
  'webSocketStore',
  () => {
    const newArr = ref([])
    const messageCount = ref()
    const dialogCount=ref()
    const current=ref()
    const allDataLoaded=ref()
    interface Tweet {
      userId: string
      username: string
      emailCut: string
      content?: string
      avatarUrl: string
      tweetId: string
      parentId: string
      media?: Array<any>
      createdAt: string
    }
    interface LikeMsg {
      username: string
      tweet:Tweet
      avatarUrl: string
      emailCut:string
      current: number
      folded: boolean
      likeCount?: number
      createdAt:string
      // type:number
    }
    const saveMsg = (msg: LikeMsg) => {
      newArr.value.unshift({
        messages: [msg], // 单条消息的数组形式
        isFolded: false,
        media: msg.tweet?.media,
        content: msg.tweet?.content
      })
    }
    const loadData=async()=>{
      if (current.value === undefined) {
        current.value = 1
      }
      if (allDataLoaded.value === true) {
        return
      }
      const response = await getLikeMsg(current.value)
      if (!response.data || response.data.length === 0) {
        allDataLoaded.value = true
        return
      }
    //  messageCount.value = ''
      loadMsg(response.data.likeMsgVOList, response.data.likeAggregate)
      current.value+=1
     
    }
    const loadMsg = (item: LikeMsg[], items: LikeMsg[][]) => {
      if (item) {
        item.forEach((msg) => {
          // msg 是一个 LikeMsg 对象               
          newArr.value.push({
            messages: [msg], // 单条消息的数组形式
            isFolded: false,
            media: msg.tweet?.media,
            content: msg.tweet?.content
          })
        })
      }
      // 处理 items 中的每组消息（折叠的）
      if (items) {
        items.forEach((group) => {
          // group 是一个 LikeMsg 数组，代表一组折叠的消息
          newArr.value.push({
            messages: group.slice(0, 2), // 仅显示前两条
            isFolded: true,
            media: group[0]?.tweet.media,
            content: group[0]?.tweet.content,
            likeCount: group[0]?.likeCount,
            createdAt:group[0]?.createdAt
          })
        })
      }
      newArr.value.sort((a, b) => new Date(b.messages[0].createdAt).getTime() - new Date(a.messages[0].createdAt).getTime());
    }
   

    
  
    return { saveMsg, messageCount, loadMsg, newArr,loadData,dialogCount }
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'WebSocketStore',
          storage: sessionStorage
        }
      ]
    }
  }
)
