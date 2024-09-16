<!--  -->
<template>
  <div class="sum" >
    <div class="placeholder"></div>
    <input type="file" @change="selectFile" ref="fileInput" hidden />
    <div class="header">
      <span class="word">{{ sendList[route.params.conversationId]?.username }}</span>
      <span class="but"><el-button circle class="fa-solid fa-gear" text></el-button></span>
    </div>
    <template
      v-for="message in dialogStore.dialogList[route.params.conversationId].chat"
      :key="message.messageId"
    >
      <div v-if="isCurrentUser(message.senderId)" class="right">
        <div class="user-info">
          <span class="username">{{ my.username }}</span>
          <span class="emailCut">@{{ my.emailCut }}</span>
        </div>
        <div>
          <span v-if="message.type === 0" class="right-text-bubble">{{ message.content }}</span>
          <el-image v-else-if="message.type === 1" :src="message.content" />
          <el-avatar :src="my.avatarUrl" class="avatar" />
        </div>
        <div class="time">{{formatDate(message.sentAt) }}</div>
      </div>
      <div v-else class="left">
        <div class="user-info">
          <span class="username">{{ message.username }}</span>
          <span class="emailCut">@{{ message.emailCut }}</span>
        </div>
        <div>
          <el-avatar :src="message.avatarUrl" class="avatar" />
          <span v-if="message.type === 0" class="left-text-bubble">{{ message.content }}</span>
          <el-image v-else-if="message.type === 1" :src="message.content" />
        </div>
        <div class="time">{{formatDate(message.sentAt) }}</div>
      </div>
    </template>

    <div class="bottom">
      <el-button
        @click="triggerFileInput"
        circle
        text
        :disabled="content != '' || files.length > 0"
      >
        <i class="fa-solid fa-image" style="color: #1d9bf0"></i>
      </el-button>
      <el-button circle text>
        <i class="fa-solid fa-face-smile" style="color: #1d9bf0"></i>
      </el-button>
      <div
        class="inputBar"
        ref="editableDiv"
        contenteditable="true"
        @input="handleInput"
        :data-placeholder="showPlaceholder ? '' : '开启你的对话'"
      >
        <div class="grid" v-for="(file, index) in files.slice(0, 1)" :key="index">
          <div class="media" v-if="isImage(file)">
            <el-image :src="file.preview" alt="Image Preview" />
            <el-button class="fa-solid fa-xmark" @click="removeFile(index)" circle text></el-button>
          </div>
          <div class="media" v-if="isVideo(file)">
            <video :src="file.preview" controls />
            <el-button class="fa-solid fa-xmark" @click="removeFile(index)" circle text></el-button>
          </div>
        </div>
      </div>
      <el-button circle text class="right" @click="uploadFiles">
        <i class="fa-solid fa-paper-plane" style="color: #1d9bf0"></i>
      </el-button>
    </div>
    <div class="placeholder"></div>
  </div>
</template>

<script setup lang="ts">
import { postMsg } from '@/api/Dialog/postMsg'
import { singleFileService } from '@/api/twi/twi'
import { useDialogStore } from '@/stores/Conversation/dialogStore'
import { useSendListStore } from '@/stores/Conversation/sendListStore'
import { myStore } from '@/stores/myStore'
import { ElMessage } from 'element-plus'
import { storeToRefs } from 'pinia'
import { PropType, computed, onMounted, onUnmounted, ref, watchEffect } from 'vue'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'
import { throttle } from 'lodash'
import { nextTick } from 'vue'
import { formatDate } from '@/utils/FormatDate'

onMounted(() => {
  window.addEventListener('scroll', onScroll)
  // 进入聊天视图时，滚动到最下方
  scrollToBottom()
})

function scrollToBottom() {
  // 请确保DOM已更新
  nextTick(() => {
    window.scrollTo(0, document.body.scrollHeight)
  })
}
const onScroll = throttle(async () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop

  // 如果接近页面顶部并准备加载旧消息
  if (scrollTop === 0) {
    const oldScrollHeight = document.body.scrollHeight

    await dialogStore.loadData(route.params.conversationId) // 加载旧消息

    // 计算新旧滚动内容的高度差
    nextTick(() => {
      const newScrollHeight = document.body.scrollHeight
      const heightDifference = newScrollHeight - oldScrollHeight

      // 调整滚动条位置以保持阅读位置
      window.scrollBy(0, heightDifference)
    })
  }
}, 500)
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
const sendListStore = useSendListStore()
const { sendList } = storeToRefs(sendListStore)
const route = useRoute()
interface Message {
  content: string
  userId: string
  type: number
  sentAt: string
}
defineProps({
  message: {
    type: Object as PropType<Message>,
    required: true
  }
})
interface FileWithPreview {
  name: string
  preview: string
  type: string
  size: number
  raw: File
}
const my = myStore()
const isCurrentUser = (senderId) => {
  return senderId === my.userId
}
const removeFile = (index: number) => {
  files.value.splice(index, 1)
}
const isImage = (file: FileWithPreview) => {
  return file.type.startsWith('image/')
}

const isVideo = (file: FileWithPreview) => {
  return file.type.startsWith('video/')
}
const selectFile = (event: Event) => {
  const inputElement = event.target as HTMLInputElement
  if (!inputElement.files || !inputElement.files.length) return
  const file = inputElement.files[0]
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    const newFile: FileWithPreview = {
      name: file.name,
      preview: reader.result as string,
      type: file.type,
      size: file.size,
      raw: file
    }
    files.value.push(newFile)
  }
}
const files = ref<FileWithPreview[]>([])
const fileInput = ref<HTMLInputElement | null>(null)
const triggerFileInput = () => {
  fileInput.value?.click()
}
const updateDivContent = () => {
  if (editableDiv.value) {
    editableDiv.value.innerText = content.value
  }
}
const showPlaceholder = computed(() => {
  return content.value || files.value.length > 0
})

const editableDiv = ref<HTMLElement | null>(null)
const content = ref<string>('')
const handleInput = (e: Event) => {
  const target = e.target as HTMLElement | null // 更改为了 HTMLDivElement
  content.value = target.innerText
}
const type = ref(0)
const dialogStore = useDialogStore()
const uploadFiles = async () => {
  if (files.value.length === 0 && content.value.trim() === '') {
    ElMessage.warning('不能提交空内容')
    return
  }
  if (files.value.length > 0) {
    const formData = new FormData()
    files.value.forEach((file) => {
      // 确保 raw 存在，并且是 File 对象
      if (file.raw && file.raw instanceof File) {
        formData.append('file', file.raw, file.name)
        console.log('当前文件', file.raw)
      }
    })
    const response = await singleFileService(formData)
    console.log(response)
    content.value = response.data
    type.value = 1
  }
  const msg = {
    sendId: my.userId,
    content: content.value,
    type: type.value,
    conversationId: route.params.conversationId
  }
  const response = await postMsg(msg)
  // 更新对话列表
  sendListStore.alterLastMsg(route.params.conversationId, content.value, type.value)
  dialogStore.pushItem(route.params.conversationId, {
    messageId: response.data,
    content: content.value,
    senderId: my.userId,
    type: type.value,
    conversationId: route.params.conversationId,
    emailCut: my.emailCut,
    avatarUrl: my.avatarUrl,
    username: my.username
  })
  files.value = []
  content.value = ''
  if (editableDiv.value) {
    editableDiv.value.innerText = '' // 这将清空内容可编辑的 div
    content.value = ''
  }

  ElMessage.success('发送成功')
}
onBeforeRouteUpdate(async (to) => {
  if (!dialogStore.dialogList[to.path]) {
    await dialogStore.loadData(to.params.conversationId)
  }
})
watchEffect(() => {
  onUnmounted(() => {
    files.value.forEach((file) => {
      if (file.preview.startsWith('blob:')) {
        URL.revokeObjectURL(file.preview)
      }
    })
    files.value = []
  })
})
</script>

<style lang="scss" scoped>
.sum {
  max-height: 100%; /* 或固定高度，比如 600px，根据需要而定 */
  overflow-y: auto; /* 当内容超出时出现滚动条 */
  overflow-x: hidden;
  .header {
    background-color: rgba(255, 255, 255, 0.8);
    z-index: 2000;
    position: fixed;
    top: 0;
    height: 16vh;
    width: 90%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 8vh;
    padding: 10px;
    .word {
      font-weight: 600;
      font-size: 20px;
    }
  }

  .right {
    margin-left: auto;
    color: white;
    text-align: right;
    .time {
      color: #7ac1c7;
      margin-top: 20px;
    }
    .user-info {
      .emailCut {
        color: #536471;
        font-size: 10px;
      }
      .username{
        color: black;
        margin-right: 3px;
        font-size: 16px;
      }
    }
    .right-text-bubble {
      min-height: 20px;
      max-width: 20vw;
      min-width: 20px;
      border-radius: 10px;
      background-color: #7ac1c7;
      color: black;
      padding: 10px;
      margin: 10px;
    }
  }

  .left {
    margin-right: auto;
    color: black;
    text-align: left;
    .time {
      color: #7ac1c7;
      margin-top: 20px;
    }
    .user-info {
      .emailCut {
        color: #536471;
        font-size: 10px;
      }
      .username{
        color: black;
        font-size: 16px;
        margin-right: 3px;
      }
    }
    .left-text-bubble {
      min-height: 20px;
      max-width: 20vw;
      width: auto;
      border-radius: 10px;
      background-color: #8989bb;
      color: black;
      padding: 10px;
      margin: 10px;
    }
  }

  .image-bubble {
    max-width: 100%;
    border-radius: 10px;
  }
}
.bottom {
  padding: 4px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  min-height: 50px;
  height: auto;
  background-color: rgb(227, 216, 216);
  border-radius: 50px;
  position: fixed;
  bottom: 10px;
  width: 700px;
  .inputBar {
    min-height: 4px; /* 给予足够的高度以便能够点击并编辑 */
    padding: 10px; /* 添加一些内边距 */
    outline: none;
    position: relative;
    white-space: pre-wrap;
    width: 580px;
    .el-image,
    video {
      border-radius: 20px;
      max-width: 200px;
      max-height: 100px;
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
    &::after {
      content: attr(data-placeholder); /* 使用属性值作为内容 */
      color: #aaa; /* placeholder 文本的颜色 */
      position: absolute; /* 使伪元素绝对定位来定位文本 */
      pointer-events: none; /* 确保点击占位符文本时不会干扰到 div 输入 */
      left: 20px; /* 根据需要调整定位 */
      top: 0; /* 根据需要调整定位 */
      bottom: 0;
      margin: auto;
      width: fit-content;
      height: fit-content;
    }
  }
}
.placeholder {
  height: 100px; /* Must be equal to the .bottom height */
  width: 100%;
}
</style>
