<!--  -->
<template>
  <div class="sum">
    <div class="header">
      <span class="word">私信</span>
      <span class="but"><el-button circle class="fa-solid fa-gear" text></el-button></span>
    </div>
    <div class="input">
      <el-input size="large" placeholder="Please Input" clearable :prefix-icon="Search" />
    </div>
    <template v-for="(value, key, index) in sendList" :key="key">
      <div class="detail" @click="handleClick(value.conversationId)">
        <el-avatar :src="value.avatarUrl"></el-avatar>
        <div class="right">
          <div class="message-count" v-if="value.unreadCount!==0">{{ value.unreadCount }}</div>
          <div class="username">
            {{ value.username }}<span class="emailCut">@{{ value.emailCut }}</span>
          </div>
          <div class="last-msg">{{ value.lastMsg }}</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { clearCount } from '@/api/Dialog/postMsg'
import router from '@/router'
import { useSendListStore } from '@/stores/Conversation/sendListStore'
import { Search } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'
const sendListStore = useSendListStore()
const handleClick=async(conversationId)=>{
  if(sendListStore.sendList[conversationId].unreadCount!==0){
    sendListStore.sendList[conversationId].unreadCount=0
    await clearCount(conversationId)
  }
  router.push(`/messages/${conversationId}`)
}
const { sendList } = storeToRefs(sendListStore)
</script>

<style lang="scss" scoped>
.sum {
  max-height: 100%; /* 或固定高度，比如 600px，根据需要而定 */
  overflow-y: auto; /* 当内容超出时出现滚动条 */
  overflow-x: hidden;
  position: fixed;
  width: 24vw;
  border: 1px solid var(--el-border-color);
  height: 100%;
  .header {
    background-color: rgba(255, 255, 255, 0.8);
    z-index: 2000;
    position: sticky;
    top: 0;
    display: flex;
    align-items: center;
    justify-content: space-between; // 此属性会将子元素推向两端
    padding: 10px;

    .word {
      font-weight: 600;
    }
    .but {
    }
  }
  .input {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    border-bottom: 1px solid var(--el-border-color);
    .el-input {
      width: 90%; // 撑满父容器的宽度
      border-radius: 40px;
    }
  }

  .detail {
    padding: 10px;
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    border-bottom: 1px solid var(--el-border-color);
    .el-avatar {
      margin-right: 10px;
    }
    .right {
      position: relative;
      .username {
        font-weight: 600;
        font-size: 14px;
      }
      .message-count {
      position: absolute;
      top: 16px; /* 调整数字显示的具体位置 */
      left: 40px;
      background-color: red; /* 消息数量的背景色 */
      color: white; /* 消息数量的文本色 */
      border-radius: 50%; /* 圆形背景 */
      padding: 0 5px; /* 略微扩大背景区域 */
      font-size: 1em; /* 消息数量的字体大小 */
      /* 需要的话可以添加更多样式，如 border、box-shadow 等 */
    }
      .emailCut {
        font-weight: 300;
        color: #536471;
      }
      .last-msg{
        color: #61a3d6;
      }
    }

    &:hover {
      background-color: #f9f3f3;
      cursor: pointer;
    }
  }
}
</style>
