<template>
  <div class="sum">
    <TweetMain :key="tweet.tweetId" :tweet="tweet" @click="getIn(tweet)">
      <template #foot>
        <TweetIcon
          :tweetId="tweet.tweetId"
          :key="'tweet-icon-' + tweet.tweetId"
        ></TweetIcon>
      </template>
    </TweetMain>
  </div>
</template>

<script setup lang="ts">
import TweetIcon from './TweetIcon.vue'
import TweetMain from './TweetMain.vue'
import { TwiDetailStore } from '@/stores/TwiDetailStore'
import router from '@/router'
import {  twiDetail } from '@/api/twi/twi'
import { PropType, ref } from 'vue'
import { usePageHistoryStore } from '@/stores/pageHistoryStore'
import { useScrollStore } from '@/stores/useScrollStore'
const useScroll=useScrollStore()
const pageHistoryStore = usePageHistoryStore()
const usetwiDetail = TwiDetailStore()
defineProps({
  tweet: {
    type: Object as PropType<Tweet>,
    required: true
  }
})

const saveCurrentPageState = (path) => {
  pageHistoryStore.savePageState(path, {
    current:pageHistoryStore.histories[router.currentRoute.value.fullPath]?.current||2,
    top: usetwiDetail.top,
    center: usetwiDetail.center,
    combineComments:usetwiDetail.combinedComments
  })
  console.log('状态已保存', pageHistoryStore.histories)
}

const getIn = async (tweet: Tweet) => {
  const path = `/${tweet.emailCut}/status/${tweet.tweetId}`
  useScroll.savePosition(router.currentRoute.value.fullPath,window.scrollY)
  const response = await twiDetail(tweet, 1)
  console.log(response)
  usetwiDetail.top = response.data.topChain
  usetwiDetail.center = tweet
  usetwiDetail.loadComments(response.data.focusChains,response.data.commentVOList)
  saveCurrentPageState(path)
  router.push(path)
 
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
</script>

<style lang="scss" scoped>
.sum {

  width: 100%;
  height: auto;
  &:hover {
    background-color: #fdfafa;
    cursor: pointer;
  }
}
</style>
