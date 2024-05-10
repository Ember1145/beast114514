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
          <li><el-button circle :icon="MoreFilled" text></el-button></li>
        </ul>

        <div class="down">
          {{ tweet?.content }}
        </div></el-header
      >
      <el-main>
        <div class="tweet-media" v-if="tweet?.media">
          <template v-for="(mediaItem, i) in tweet.media" :key="i">
            <el-image
              v-if="isImage(mediaItem)"
              :src="mediaItem"
              :style="{ gridRow: computeRow(i), gridColumn: computeColumn() }"
              :preview-src-list="tweet.media.filter(isImage)"
            />
            <video
              v-else
              :src="mediaItem"
              controls
              :style="{ gridRow: computeRow(i), gridColumn: computeColumn() }"
            ></video>
          </template>
        </div>
      </el-main>
      <slot name="foot"></slot>
    </el-container>
  </el-container>
</template>
<script setup lang="ts">
import { PropType, defineProps } from 'vue'
import { MoreFilled } from '@element-plus/icons-vue'
import { getUserPage } from '@/api/user/user'
import router from '@/router'
const handlePage = () => {
  getUserPage(props.tweet.emailCut)
  router.push(`/${props.tweet.emailCut}`)
}

const formatDate = (createdAt: string) => {
  const now = Date.now()
  const tweetDate = new Date(createdAt).getTime() // 假设 createdAt 是 ISO 8601 字符串
  const diffInSeconds = Math.floor((now - tweetDate) / 1000)
  const diffInMinutes = Math.floor(diffInSeconds / 60)
  const diffInHours = Math.floor(diffInMinutes / 60)
  if (diffInSeconds < 60) {
    return `${diffInSeconds}秒前`
  } else if (diffInMinutes < 60) {
    return `${diffInMinutes}分钟前`
  } else if (diffInHours < 24) {
    return `${diffInHours}小时前`
  } else {
    return formatDateToPattern(createdAt)
  }
}
const formatDateToPattern = (date: string) => {
  const dateObj = new Date(date)
  const yearStr = dateObj.getFullYear().toString()
  const monthStr = (dateObj.getMonth() + 1).toString()
  const dayStr = dateObj.getDate().toString()
  const formattedDate = `${yearStr}年${parseInt(monthStr)}月${parseInt(dayStr)}日`
  return formattedDate
}
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
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  return imageExtensions.some((ext) => mediaItem.endsWith(ext))
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
  position: relative;
 &:last-child::before {
      display: none; /* 这会隐藏最后一条评论的链条 */
    }
  &::before {
    content: '';
    position: absolute;
    left: 36px;
    top: 64px;
    height: 87%;
    border-left: 3px solid #CfD9DE;
    
  }
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
      line-height: 1;
      &:first-child {
        font-weight: 800;
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
    min-height: 10px;
    max-height: 500px;
    // height: 300px;
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
