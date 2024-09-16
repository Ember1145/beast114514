<template>
  <div class="aside">
    <div class="top" @mouseenter="handle" @mouseleave="mouseOut">
      <RoundButton :buttonHover="hoverColor" :size="40"
        ><i class="fa-brands fa-twitter"></i
      ></RoundButton>
    </div>
    <el-aside class="icon-list" v-for="item in items" :key="item.id" :span="4">
      <div
        class="roundBut"
        @click="handleClick(item.path, item.id)"
        :class="{ active: useLeft.activeButton === item.id }"
      >
        <i :class="item.iconClass"></i>
        <div
          v-if="item.iconClass === 'fa-solid fa-bell' && webSocketStore?.messageCount !== '0'"
          class="messageCount"
        >
          {{ webSocketStore?.messageCount }}
        </div>
        <div
          v-if="item.iconClass === 'fa-regular fa-comment' && webSocketStore?.dialogCount !== '0'"
          class="messageCount"
        >
          {{ webSocketStore?.dialogCount }}
        </div>
        <div class="font">{{ item.text }}</div>
      </div>
    </el-aside>
    <el-button type="primary" class="tip" @click="visible = true">发帖</el-button>
  </div>
  <el-dropdown placement="top" trigger="click">
    <div class="avatar-button">
      <el-avatar :size="50" icon="UserFilled" :src="useMy.avatarUrl" />
      <div class="rig">
        <div>{{ useMy.username }}</div>
        <div>@{{ useMy.emailCut }}</div>
      </div>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item>添加用户</el-dropdown-item>
        <el-dropdown-item @click="clearToken">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <el-dialog v-model="visible" title="发送推文" @close="resetField">
    <UploadV ref="uploadRef"></UploadV>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import UploadV from '@/components/TweetUploader/uploadV.vue'
import { useLeftStore } from '@/stores/leftStore'
import { useRouter } from 'vue-router'
import { myStore } from '@/stores/myStore'
import { useInteractionStore } from '@/stores/Interaction/InteractionStore'
import RoundButton from '../RoundButton.vue'
import { useWebSocketStore } from '@/stores/useWebSocketStore'
import { clearDialogCount, clearHomeCount } from '@/api/message'
import { useDialogStore } from '@/stores/Conversation/dialogStore'
import { useSendListStore } from '@/stores/Conversation/sendListStore'
import { useDeliverStore } from '@/stores/Delivery/useDeliverStore'
const webSocketStore = useWebSocketStore()
const interactionStore = useInteractionStore()
const dialogStore = useDialogStore()
const sendListStore = useSendListStore()
const useMy = myStore()
const router = useRouter()
const useLeft = useLeftStore()
const uploadRef = ref()
const hoverColor = ref()
const deliverStore = useDeliverStore()
const handle = () => {
  hoverColor.value = 'rgba(0,0,164,0.1)'
}
const mouseOut = () => {
  hoverColor.value = ''
}
interface IconItem {
  id: number
  iconClass: string
  text: string
  path: string // 新增的路径属性
}
const visible = ref(false)
const resetField = () => {
  if (uploadRef.value) {
    uploadRef.value.clearTweet()
  }
}
const handleClick = async (path: any, id: any) => {
  if (id === 3) {
    if (webSocketStore.messageCount !== 0) {
      webSocketStore.messageCount = ''
      await clearHomeCount()
    }
  }
  if (id === 5) {
    if (webSocketStore.dialogCount !== 0) {
      webSocketStore.dialogCount = ''
      await clearDialogCount()
    }
  }
  router.push(path)
  useLeft.setActiveButton(id)
}
const items = ref<IconItem[]>([
  { id: 1, iconClass: 'fa-solid fa-house', text: '主页', path: '/home' },
  { id: 2, iconClass: 'fa-solid fa-poo-storm', text: '社群', path: '/Search' },
  { id: 3, iconClass: 'fa-solid fa-bell', text: '通知', path: '/notifications' },
  { id: 4, iconClass: 'fa-regular fa-bookmark', text: '书签', path: '/notifications' },
  { id: 5, iconClass: 'fa-regular fa-comment', text: '私信', path: '/messages' },
  { id: 6, iconClass: 'fa-solid fa-book', text: '个人资料', path: `/${useMy.emailCut}` }
])
const clearToken = () => {
  useMy.removeAll()
  interactionStore.removeAll()
  dialogStore.clear()
  sendListStore.sendList = {}
  deliverStore.histories = []
  router.push('/login')
}
</script>

<style scoped lang="scss">
.aside {
  position: fixed;
  .top {
    margin-left: 50px;
    margin-top: 20px;
    i {
      font-size: 18px;
      cursor: pointer;
      color: #4b96d7;
    }
  }
  .tip {
    width: 13vw;
    height: 7vh;
    border-radius: 30px;
    margin-left: 45px;
  }
}

.el-aside {
  margin-left: 40px;
  .roundBut {
    cursor: pointer;
    padding: 20px;
    border-radius: 20px;
    width: 160px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    position: relative;
    &:hover {
      background-color: #f0f0f0;
    }
    .messageCount {
      position: absolute;
      top: 10px; /* 调整数字显示的具体位置 */
      left: 30px;
      background-color: red; /* 消息数量的背景色 */
      color: white; /* 消息数量的文本色 */
      border-radius: 50%; /* 圆形背景 */
      padding: 0 5px; /* 略微扩大背景区域 */
      font-size: 0.8em; /* 消息数量的字体大小 */
      /* 需要的话可以添加更多样式，如 border、box-shadow 等 */
    }
    i {
      margin: 0;
      width: 20px;
      font-size: 18px;
      margin-right: 0;
    }
    .font {
      font-size: 20px;
      margin-left: 20px;
    }
  }
}

.active {
  color: black;
}

.el-dropdown-menu {
  width: 200px;
}
.avatar-button {
  position: fixed;
  bottom: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  width: 15vw;
  height: 10vh;
  border: none;
  border-radius: 12px;
  .el-avatar {
    margin-left: 10px;
  }
  .rig {
    margin-left: 10px;
  }
}
.avatar-button:hover {
  background-color: #f0f0f0;
}

.avatar {
  width: 24px;
  height: 24px;
  margin-right: 8px;
}
</style>
