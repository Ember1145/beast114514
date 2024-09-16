<template>
  <template v-for="(tweet, index) in topChain" :key="index">
    <el-container
      class="common"
      @mouseenter="fatherColor = '#fdfafa'"
      @mouseleave="fatherColor = 'white'"
      :style="{ '--chain-display': hideLastChain ? 'none' : 'block' }"
      @click="getIn(tweet.emailCut, tweet.tweetId)"
    >
      <el-aside width="64px">
        <el-avatar :size="46" :src="tweet?.avatarUrl" @click.stop="handlePage(tweet.emailCut)" />
      </el-aside>
      <el-container>
        <el-header>
          <ul class="up">
            <li class="name" @click.stop="handlePage(tweet.emailCut)">{{ tweet?.username }}</li>
            <li class="email" @click.stop="handlePage(tweet.emailCut)">@{{ tweet?.emailCut }}</li>
            <li class="time">{{ formatDate(tweet?.createdAt!) }}</li>
            <li>
              <el-dropdown trigger="click">
                <el-button circle :icon="MoreFilled" text @click.stop></el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-if="tweet?.emailCut === my.emailCut"
                      @click="delTweet(tweet)"
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
                  gridRow: computeRow(i, tweet.media),
                  gridColumn: computeColumn(tweet.media),
                  'min-height': computeVarHeight(tweet.media)
                }"
                :preview-src-list="tweet.media.filter(isImage)"
              />
              <video
                v-else
                :src="mediaItem"
                controls
                :style="{
                  gridRow: computeRow(i, tweet.media),
                  gridColumn: computeColumn(tweet.media)
                }"
              ></video>
            </template>
          </div>
        </el-main>
        <TweetIcon :tweet="tweet" :fatherColor="fatherColor"></TweetIcon>
      </el-container>
    </el-container>
    <div
      class="more-reply"
      v-if="index === 0 && isFold"
      @click="getIn(tweet.emailCut, tweet.tweetId)"
    >
      <div class="font">更多回复</div>
    </div>
  </template>
</template>
<script setup lang="ts">
import { PropType, defineProps, ref } from 'vue'
import { MoreFilled } from '@element-plus/icons-vue'
import TweetIcon from '../IconButton/TweetIcon.vue'
import router from '@/router'
import { deleteTweet } from '@/api/twi/twi'
import { myStore } from '@/stores/myStore'
import { useUserReplyStore } from '@/stores/UserPageDown/UserReplyStore'
import { formatDate } from '@/utils/FormatDate'
import { computeColumn, computeRow, isImage } from '@/utils/mediaBuild'
const computeVarHeight = (media) => {
  return media.length < 3 ? '280px' : '150px'
}

const getIn = async (emailCut, tweetId) => {
  const path = `/${emailCut}/status/${tweetId}`
  router.push(path)
}
const userReplyStore = useUserReplyStore()
const my = myStore()
const delTweet = async (tweet) => {
  await deleteTweet(tweet.tweetId)
  userReplyStore.delItem(tweet.tweetId)
}
const fatherColor = ref()
const handlePage = (emailCut) => {
  router.push(`/${emailCut}`)
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
  realParent: string
}
const props = defineProps({
  topChain: {
    type: Object as PropType<Tweet[]>,
    required: true
  },

  hideLastChain: Boolean,
  isFold: Boolean
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
  cursor: pointer;
  &:hover {
    background-color: #fdfafa;
    cursor: pointer;
  }
  &::before {
    content: '';
    position: absolute;
    left: 36px;
    top: 64px;
    height: 99%;
    border-left: 3px solid #cfd9de;
    z-index: 100;
  }
  &:last-child::before {
    display: var(--chain-display, block);
  }
  .more-reply {
    position: relative;
    background-color: white;
    height: 40px;
    z-index: 101;
    display: flex;
    align-items: center;
    .font {
      color: rgb(64, 64, 204);
      cursor: pointer;
      position: relative;
      margin-left: 50px;
      &::before {
        content: '';
        position: absolute;
        left: -13px;
        height: 99%;
        border-left: 3px dashed #cfd9de;
        z-index: 100;
      }
    }
  }
  .el-aside {
    width: 50px;
    .el-avatar {
      position: relative;
      z-index: 101;
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
