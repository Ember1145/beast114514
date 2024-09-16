import { getDialogList } from '@/api/Dialog/postMsg'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSendListStore = defineStore(
  'SendListStore',
  () => {
    const sendList = ref<Record<string, SessionInfo>>({})
    interface SessionInfo {
      avatarUrl: string
      username: string
      userId?: string
      emailCut: string
      lastMsg?: string
      conversationId?: string
      groupName?: string
      unreadCount?: number
      type: number
    }
    const current = ref()
    const allDataLoaded = ref(false)
    const loadData = async () => {
      if (current.value === undefined) {
        current.value = 1
      }
      if (allDataLoaded.value === true) {
        return
      }
      const response = await getDialogList(current.value)
      if (!response.data || response.data.length === 0) {
        allDataLoaded.value = true
        return
      }
      response.data.forEach((item) => {
        const id = item.conversationId // 假设 item 对象是聊天记录，其中包含 conversationId 属性
        sendList.value[id] = sendList.value[id] ? { ...sendList.value[id], ...item } : { ...item }
      })
      current.value += 1
    }
    const alterLastMsg = (index, lastMsg, type) => {
      sendList.value[index].lastMsg = lastMsg
      sendList.value[index].type = type
    }
    const pushItem = (newItem: SessionInfo, index) => {
      if (!sendList.value[index]) {
        sendList.value[index] = newItem
      }
    }
    return { sendList, pushItem, loadData, current, allDataLoaded, alterLastMsg }
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'sendListStore',
          storage: localStorage
        }
      ]
    }
  }
)
