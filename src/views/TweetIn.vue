<template>
  <div class="sum">
    <div class="header">
      <el-button circle text :icon="Back" @click="handleClose"></el-button>
      <div class="ft">帖子</div>
    </div>
    <div class="main">
      <ChainFull :tweetChain="top"></ChainFull>
      <TweetAround :tweet="center"></TweetAround>
      <mediaBut :tweetId="center?.tweetId"></mediaBut>
      <div v-for="(section, index) in combinedComments" :key="index">
        <template v-if="isCommon(section)">
          <TwiDown v-for="tweet in section" :key="tweet.tweetId" :tweet="tweet"></TwiDown>
        </template>
        <template v-else>
          <ChainFull :tweetChain="section"></ChainFull>      
        </template>
      </div>
      <div class="more">更多</div>
    </div>
  </div>

</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { TwiDetailStore } from '@/stores/TwiDetailStore'
import TwiDown from '@/components/TwiDown.vue'
import mediaBut from '@/components/mediaBut.vue'
import TweetAround from '@/components/TweetAround.vue'
import { Back } from '@element-plus/icons-vue'
import router from '@/router'
import { usePageHistoryStore } from '@/stores/pageHistoryStore'
import { onBeforeRouteUpdate } from 'vue-router'
import ChainFull from '@/components/Chain/chainFull.vue'
import { storeToRefs } from 'pinia'
import { twiDetail } from '@/api/twi/twi'
import { throttle} from 'lodash'
onMounted(() => {
  window.addEventListener('scroll', onScroll);
});
const pageHistoryStore = usePageHistoryStore()
const usetwiDetail = TwiDetailStore()
const { top, center, combinedComments } = storeToRefs(usetwiDetail)
const isCommon = (section) => {
  return Array.isArray(section) && !(Array.isArray(section[0]));
};
const onScroll = throttle(async () => {
  const current=pageHistoryStore.histories[router.currentRoute.value.fullPath]?.current||2
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
  const viewportHeight = window.innerHeight;
  const scrollHeight = document.documentElement.scrollHeight;
  if (scrollTop + viewportHeight >= scrollHeight - 100) { // 给予一点容错值
    const response = await twiDetail(center.value, current);
    usetwiDetail.addComments(response.data.focusChains, response.data.commentVOList);
    pageHistoryStore.histories[router.currentRoute.value.fullPath].current += 1;
    saveCurrentPageState(router.currentRoute.value.fullPath);
    console.log()
  }
}, 300);
const saveCurrentPageState = (path) => {
  pageHistoryStore.savePageState(path, {
    current: pageHistoryStore.histories[router.currentRoute.value.fullPath].current,
    top: usetwiDetail.top,
    center: usetwiDetail.center,
    combineComments: usetwiDetail.combinedComments
  })
  console.log('状态已保存', pageHistoryStore.histories)
}
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
onBeforeRouteUpdate(async (to, from) => {
  const savedState = pageHistoryStore.getPageState(to.path)
  if (savedState && Object.keys(savedState).length > 0) {
    usetwiDetail.top = savedState.top
    usetwiDetail.center = savedState.center
    usetwiDetail.combinedComments = savedState.combineComments
    console.log('状态已恢复', pageHistoryStore.histories, 'dang', savedState)
  }
})
const handleClose = () => {
  router.go(-1)
}
</script>
<style lang="scss" scoped>
.sum {
  .header {
    padding-left: 20px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    height: 8vh;
    position: sticky;
    top: 0;
    background-color: rgba(255, 255, 255, 0.8);
    z-index: 2000;
    border: 1px solid var(--el-border-color);
    border-bottom: none;
    .ft {
      font-weight: 600;
      margin-left: 20px;
      font-size: 20px;
    }
  }
}
</style>
