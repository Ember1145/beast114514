// src/utils/useWebSocket.js
import router from '@/router'
import { useDialogStore } from '@/stores/Conversation/dialogStore'
import { useSendListStore } from '@/stores/Conversation/sendListStore'
import { myStore } from '@/stores/myStore'
import { useWebSocketStore } from '@/stores/useWebSocketStore'
import { ref } from 'vue'


export const useWebSocket = (sId) => {
  const my=myStore()
  const webSocket = ref(null)
  const messages = ref([])
  const connect = () => {
    if (webSocket.value && webSocket.value.readyState === WebSocket.OPEN) return
    webSocket.value = new WebSocket(`ws://localhost:8080/webSocket/${sId}`)

    webSocket.value.onopen = () => {
      console.log('WebSocket connection established')
      // 订阅消息等
    }
    const webSocketStore = useWebSocketStore()
    const sendListStore=useSendListStore()
    const dialogListStore=useDialogStore()
    webSocket.value.onmessage = (message) => {
      console.log('Received message: ', message.data)
      const data = JSON.parse(message.data)
      console.log('Parsed JSON data:', data)
      if (data.type === 'like') {
        webSocketStore.saveMsg(data.likeMsgVO)
      } else if (data.type === 'count') {
        webSocketStore.messageCount = data.msgCount
      }
      else if(data.type==='onlineList'){
        sendListStore.sendList[data.sendListVO.conversationId]=data.sendListVO
        webSocketStore.dialogCount=data.dialogCount
      }
      else if(data.type==='onlineMsg'){
        dialogListStore.pushItem(data.dialogVO.conversationId,{...data.dialogVO})
      }
      else if(data==='FORCE_LOGOUT'){
        my.removeAll()
        dialogListStore.clear()
        sendListStore.sendList={}
        router.push('/login')
      }
    }

    webSocket.value.onclose = () => {
      console.log('WebSocket connection closed')
      // 你可能想要在这里处理重连逻辑
      // reconnect()
    }

    webSocket.value.onerror = (error) => {
      console.error('WebSocket error:', error)
      // 错误处理逻辑
    }
  }

  const disconnect = () => {
    if (webSocket.value) {
      webSocket.value.close()
    }
  }

  // const reconnect = () => {
  //   setTimeout(() => {
  //     connect()
  //   }, 5000) // 1秒后尝试重连
  // }

  // 返回WebSocket连接，以便在组件外部使用或发送消息
  return {
    webSocket,
    connect,
    disconnect,
    messages
  }
}
