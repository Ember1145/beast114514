<template>
  <div class="sum" @mouseenter="fatherColor = '#fdfafa'" @mouseleave="fatherColor = 'white'">
    <TweetMain :key="tweet.tweetId" :tweet="tweet"  @click="getIn(tweet.emailCut,tweet.tweetId)">
      <template #foot>
        <TweetIcon
          :tweet="tweet"
          :fatherColor="fatherColor"
          :key="'tweet-icon-' + tweet.tweetId"
        ></TweetIcon>
      </template>
    </TweetMain>
  </div>
</template>

<script setup lang="ts">
import router from '@/router';
import TweetIcon from './IconButton/TweetIcon.vue'
import TweetMain from './IconButton/TweetMain.vue'
import { PropType, ref } from 'vue'
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
const fatherColor = ref()
defineProps({
  tweet: {
    type: Object as PropType<Tweet>,
    required: true
  }
})
const getIn = async (emailCut,tweetId) => {
  const path = `/${emailCut}/status/${tweetId}`
  router.push(path)
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
