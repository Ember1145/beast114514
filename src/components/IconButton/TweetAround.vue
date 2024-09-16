<!--  -->
<template>
  <div class="sum">
    <div class="top">
      <el-avatar :size="46" :src="tweet.avatarUrl" @click.stop="handlePage"></el-avatar>
      <div class="name">
        <div class="user">{{ tweet.username }}</div>
        <div class="emailCut">@{{ tweet.emailCut }}</div>
      </div>
      <div class="right">
        <el-dropdown trigger="click">
        <el-button circle :icon="MoreFilled" text @click.stop></el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-if="tweet.emailCut === my.emailCut" @click="delTweet(tweet)"
              >删除推文</el-dropdown-item
            >
            <el-dropdown-item>引用</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      </div>
      
    </div>
    <div class="down">
      {{ tweet?.content }}
    </div>
    <div class="main">
      <div class="tweet-media" v-if="tweet?.media">
        <template v-for="(mediaItem, i) in tweet.media" :key="i">
          <el-image
            v-if="isImage(mediaItem)"
            :src="mediaItem"
            :style="{
              gridRow: computeRow(i,tweet.media),
              gridColumn: computeColumn(tweet.media),
              'min-height': varHeight
            }"
            :preview-src-list="tweet.media.filter(isImage)"
          />
          <video
            v-else
            :src="mediaItem"
            controls
            :style="{ gridRow: computeRow(i,tweet.media), gridColumn: computeColumn(tweet.media) }"
          ></video>
        </template>
      </div>
      {{ formatDate(tweet.createdAt )}}
    </div>
    <CommentIcon :tweet="tweet"></CommentIcon>
  </div>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { MoreFilled } from '@element-plus/icons-vue'
import CommentIcon from './CommentIcon.vue'
import router from '@/router'
import { myStore } from '@/stores/myStore'
import { deleteTweet } from '@/api/twi/twi'
import { useUserReplyStore } from '@/stores/UserPageDown/UserReplyStore'
import { formatDate } from '@/utils/FormatDate'
import {computeColumn,computeRow,isImage} from '@/utils/mediaBuild'
const userReplyStore = useUserReplyStore()
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
const varHeight = computed(() => {
  if (props.tweet.media.length < 3) {
    return '280px'
  }
  return '150px'
})
const handlePage = async () => {
  router.push(`/${props.tweet.emailCut}`)
}
const my = myStore()
const delTweet = async (tweet) => {
  await deleteTweet(tweet.tweetId)
  userReplyStore.delItem(tweet.tweetId)
}
const props = defineProps({
  tweet: {
    type: Object as PropType<Tweet>,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.sum {
  padding: 15px;
  border: 1px solid var(--el-border-color);
  border-top: none;
  border-bottom: none;
  margin-bottom: 1px;
  padding-bottom: 0;
  .down {
    font-size: 20px;
    margin-top: 20px;
    margin-bottom: 20px;
    white-space: pre-wrap;
  }
  .top {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    .el-avatar {
      cursor: pointer;
      position: relative;
      z-index: 101;
    }
    .right {
      margin-left: auto;
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
