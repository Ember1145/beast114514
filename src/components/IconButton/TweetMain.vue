<template>
  <el-container class="common">
    <el-aside width="64px">
      <el-avatar :size="46" :src="tweet?.avatarUrl" @click.stop="handlePage" />
    </el-aside>
    <el-container>
      <el-header>
        <ul class="up">
          <li class="name" @click.stop="handlePage">{{ tweet?.username }}</li>
          <li class="email" @click.stop="handlePage">@{{ tweet?.emailCut }}</li>
          <li class="time">{{ formatDate(tweet?.createdAt!) }}</li>
          <li>
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
          </li>
        </ul>
        <div class="down">
          {{ tweet?.content }}
        </div></el-header
      >
      <el-main>
        <div class="tweet-media" v-if="tweet?.media" @click.stop>
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
      </el-main>
      <slot name="foot"></slot>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { PropType, computed, defineProps } from 'vue'
import { MoreFilled } from '@element-plus/icons-vue'
import { myStore } from '@/stores/myStore'
import router from '@/router'
import { deleteTweet } from '@/api/twi/twi'
import { useUserShareAndMyStore } from '@/stores/UserPageDown/ShareAndMyStore'
import { formatDate } from '@/utils/FormatDate'
import {computeColumn,computeRow,isImage} from '@/utils/mediaBuild'
const my = myStore()
const varHeight = computed(() => {
  if (props.tweet.media.length < 3) {
    return '280px'
  }
  return '150px'
})
const ShareAndMyStore = useUserShareAndMyStore()
const delTweet = async (tweet) => {
  const path = `/${my.emailCut}`
  await deleteTweet(tweet.tweetId)
  ShareAndMyStore.delItem(tweet.tweetId, path)
}
const handlePage = () => {
  router.push(`/${props.tweet.emailCut}`)
}

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
const props = defineProps({
  tweet: {
    type: Object as PropType<Tweet>,
    required: true
  }
})
</script>

<style scoped lang="scss">
.common {
  padding: 15px;
  padding-bottom: 8px;
  border: 1px solid var(--el-border-color);
  border-top: none;
  min-height: 40px;
  height: auto;
  .el-aside {
    width: 50px;
    .el-avatar {
      cursor: pointer;
      margin-right: 1px;
      &:hover {
        background-color: #536471;
      }
    }
  }
  .el-header {
    padding: 0;
    padding-left: 10px;
    height: auto;
    .down {
      font-size: 18px;
      margin-top: 10px;
      white-space: pre-wrap;
    }
    ul {
      display: flex;
      justify-content: flex-start;
      height: 20px;
      li {
        white-space: nowrap; 
        min-width: 0;
        line-height: 1;
        &:first-child {
          font-weight: 700;
          position: relative;
          cursor: pointer;
          &:hover::after {
            content: '';
            position: absolute;
            left: 0;
            right: 0;
            bottom: 0; /* 调整这个值来控制下划线与文本之间的距离 */
            height: 3px; /* 下划线的粗细 */
            background-color: black;
          }
        }
        &:nth-child(2) {
          color: rgb(97, 96, 96);
        }
        &:nth-child(-n + 3) {
          margin-right: 60px;
        }

        &:last-child {
          margin-left: auto;
        }
      }
    }
  }

  .el-main {
    padding: 0;
    .tweet-media {
      display: grid;
      max-width: 500px; /* 限制容器的最大宽度 */
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
