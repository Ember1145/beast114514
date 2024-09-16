import { getFullMsg } from '@/api/Dialog/postMsg'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDialogStore = defineStore(
  'DialogStore',
  () => {
    const dialogList = ref<Record<string, dialogState>>({})
    interface dialog {
      messageId:string
      content: string
      senderId:string
      type: number
      conversationId: string
      avatarUrl:string
      username:string
      emailCut:string
      sentAt:string
    }
    interface dialogState{
      chat:dialog[]
      current?:number
      allDataLoaded?:boolean
    }
    const pushItem = (index, item) => {
      if (!dialogList.value[index]) {
        dialogList.value[index] = { chat: [], current: 1, allDataLoaded: false };
      }
      dialogList.value[index].chat.push(item);
    }
    const loadData = async (conversationId) => {
      if (!dialogList.value[conversationId]) {
        dialogList.value[conversationId] = {
          chat: [],
          current: 1,
          allDataLoaded: false 
        }
      }
      if(dialogList.value[conversationId].allDataLoaded){
        return;
      }
      const response = await getFullMsg(conversationId,dialogList.value[conversationId].current);
      if (response.data.length === 0) {
        dialogList.value[conversationId].allDataLoaded = true; 
        return;
      }
   
      const sortedData = response.data.sort((a, b) => {
        const dateA = Date.parse(a.sentAt);
        const dateB = Date.parse(b.sentAt);
        return dateA - dateB; // 对于正序，如果a比b早，结果应为负数
      });
      dialogList.value[conversationId].chat = [
        ...sortedData,
        ...dialogList.value[conversationId].chat
      ];
      dialogList.value[conversationId].current += 1
      
    }
    const clear=()=>{
      dialogList.value={}
    }
    return { dialogList ,pushItem,loadData,clear}
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'dialogListStore',
          storage: localStorage
        }
      ]
    }
  }
)
