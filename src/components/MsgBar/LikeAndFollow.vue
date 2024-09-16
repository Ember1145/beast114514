<!--  -->
<template>
  <div class="sum" v-for="(normalizedMsg, index) in newArr" :key="index" @click="getIn(normalizedMsg.messages[0].tweet.emailCut,normalizedMsg.messages[0].tweet.tweetId)">
    <div class="left">
      <i class="fa-solid fa-heart"></i>
      <div class="time">{{ formatDate(normalizedMsg.messages[0].createdAt) }}</div>
    </div>
    <div class="right">
      <div class="top">
        <el-avatar
          v-for="(msg, i) in normalizedMsg.messages"
          :key="i"
          :size="40"
          :src="msg.avatarUrl"
          @click.stop="handlePage(msg.emailCut)"
        ></el-avatar>
      </div>
      <div class="font">
        <template v-if="normalizedMsg.isFolded">
          <span class="name"
            >{{ normalizedMsg.messages[0].username }},{{ normalizedMsg.messages[1].username }} 等{{
              normalizedMsg.messages[0].likeCount
            }}人喜欢了你的推文</span
          >
        </template>
        <template v-else>
          <span class="name">{{ normalizedMsg.messages[0].username }}</span
          >喜欢了你的推文
        </template>
      </div>
      <div class="content">{{ normalizedMsg.content }}</div>
    </div>
    <div class="grid" @click.stop>
      <template v-for="(mediaItem, i) in normalizedMsg.media" :key="mediaItem">
        <el-image
          v-if="isImage(mediaItem)"
          :src="mediaItem"
          :style="{
            gridRow: computeRow(i, normalizedMsg.media),
            gridColumn: computeColumn(normalizedMsg.media)
          }"
          :preview-src-list="normalizedMsg.media.filter(isImage)"
        />
        <video
          v-else
          :src="mediaItem"
          controls
          :style="{
            gridRow: computeRow(i, normalizedMsg.media),
            gridColumn: computeColumn(normalizedMsg.media)
          }"
        ></video>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getLikeMsg } from '@/api/message'
import router from '@/router';
import { useWebSocketStore } from '@/stores/useWebSocketStore'
import { storeToRefs } from 'pinia'
import { formatDate } from '@/utils/FormatDate';
import {computeColumn,computeRow,isImage} from '@/utils/mediaBuild'
const handlePage = (emailCut) => {
  router.push(`/${emailCut}`)
}
const getIn = async (emailCut,tweetId) => {
  const path = `/${emailCut}/status/${tweetId}`
  router.push(path)
}
const webSocketStore = useWebSocketStore()
const { messageCount, newArr } = storeToRefs(webSocketStore)
</script>

<style lang="scss" scoped>
.sum {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 10px;
  height: auto;
  border: 1px solid var(--el-border-color);
  border-top: none;
  cursor: pointer;
  &:hover{
    background-color: #fdfafa;
  }
   
  
  .left { 
    margin-left: 20px;
    white-space: nowrap; 
    min-width: 89px;
    i {
      font-size: 30px;
      color: red;
    }
    .time {
      color: #536471;
      font-size: 12px;
    }
  }
  .right {
    margin-left: 10px;
    width: 64%;
    .top {
      display: flex;
      align-items: center;
      cursor: pointer;
    }
    .font {
      margin-top: 10px;
      .name {
        font-weight: 600;
        font-size: 15px;
        margin-right: 10px;
      }
    }
    .content {
      font-size: 14px;
      margin-top: 10px;
      color: #536471;
      white-space: nowrap; /* 保证文本在一行内显示 */
      overflow: hidden; /* 隐藏超出容器部分的内容 */
      text-overflow: ellipsis; /* 显示省略号来表示隐藏的文本 */
    }
  }

  .grid {
    min-width: 100px;
    display: grid;
    max-width: 100px; /* 限制容器的最大宽度 */
    max-height: 100px;
    grid-template-rows: repeat(2, 1fr); /* 使网格有两行 */
    grid-template-columns: repeat(2, 1fr);
    grid-gap: 1px;
    border-radius: 10px;
    overflow: hidden;
    .el-image {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }
}
</style>
