<!--  -->
<template>
  <div class="sum">
    <div class="top">
      <el-avatar :size="46" :src="tweet.avatarUrl" @click.stop="handlePage"></el-avatar>
      <div class="name">
        <div class="user">{{ tweet.username }}</div>
        <div class="emailCut">@{{ tweet.emailCut }}</div>
      </div>
      <el-button circle></el-button>
    </div>
    <div class="down">   
        {{ tweet?.content }}
    </div>
    <div class="main">
      <div class="tweet-media" v-if="tweet?.media">
        <template v-for="(mediaItem, i) in tweet.media" :key="i">
          <el-image v-if="isImage(mediaItem)"
            :src="mediaItem"
            :style="{ gridRow: computeRow(i), gridColumn: computeColumn() }"
            :preview-src-list="tweet.media.filter(isImage)"
          />
          <video v-else
            :src="mediaItem"
            controls
            :style="{ gridRow: computeRow(i), gridColumn: computeColumn() }"
          ></video>
        </template>
      </div>
    </div>
      <CommentIcon :tweet="tweet"></CommentIcon>
  </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import CommentIcon from '@/components/CommentIcon.vue'
import router from '@/router';
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
const handlePage =async () => {
  router.push(`/${props.tweet.emailCut}`)
}
const props = defineProps({
  tweet: {
    type: Object as PropType<Tweet>,
    required: true
  }
})
const computeRow = (i: any) => {
  if (!props.tweet.media || !Array.isArray(props.tweet.media)) return
  if (props.tweet.media.length === 1) {
    return 'span 2'
  }
  if (props.tweet.media.length === 2) {
    return '1/3'
  }
  return props.tweet.media.length === 3 && i === 0 ? 'span 2' : 'span 1'
}

const computeColumn = () => {
  if (!props.tweet.media || !Array.isArray(props.tweet.media)) return
  if (props.tweet.media.length === 1) {
    return 'span 2'
  }
}
const isImage = (mediaItem: string): boolean => {
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp'];
  return imageExtensions.some(ext => mediaItem.endsWith(ext));
}
</script>

<style lang="scss" scoped>
.sum {
  padding: 15px;
  border: 1px solid var(--el-border-color);
  border-top: none;
  border-bottom:none ;
  margin-bottom: 1px;
  padding-bottom: 0;
  .down{
    font-size: 20px;
    margin-top: 20px;
    margin-bottom: 20px;
    white-space: pre-wrap;
  }
  .top {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    .el-avatar{
      cursor: pointer;
      position: relative;
      z-index: 101;
    }
    .name {
      margin-left: 10px;
      .user {
        font-weight: 600;
        font-size: 20px;
        cursor: pointer;
      }
      .emailCut {
        font-size: 15px;
        cursor: pointer;
      }
    }
    .el-button {
      margin-left: auto;
      margin-right: 20px;
    }
  }
  .main {
    margin: 0 auto; /* 正确的语法，用于水平居中 */
    max-width: 800px;
    .tweet-media {
      display: grid;
      max-width: 800px; /* 限制容器的最大宽度 */
      min-height: 10px;
      max-height: 500px;
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
}
</style>
