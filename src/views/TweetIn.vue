<template>
  <div class="sum">
    <div class="header">
      <el-button circle text :icon="Back" @click="handleClose"></el-button>
      <div class="ft">帖子</div>
    </div>
    <div class="main">
      <ChainFull :tweetChain="histories[route.path].top" :hideLastChain="false"></ChainFull>
      <TweetAround :tweet="histories[route.path].center"></TweetAround>
      <mediaBut :tweetId="histories[route.path].center.tweetId" :realParent="histories[route.path].top[0][0]?.tweetId||histories[route.path].center.tweetId"></mediaBut>
      <div v-for="(section, index) in histories[route.path].combinedComments" :key="index">
        <template v-if="isCommon(section)">
          <TwiDown v-for="tweet in section" :key="tweet.tweetId" :tweet="tweet"></TwiDown>
        </template>
        <template v-else>
          <ChainFull :tweetChain="section" :hideLastChain="true"></ChainFull>      
        </template>
      </div>
      <div class="more"><el-button>More</el-button></div>
    </div>
  </div>

</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { TwiDetailStore } from '@/stores/TwiDetailStore'
import TwiDown from '@/components/TwiDown.vue'
import mediaBut from '@/components/TweetUploader/mediaBut.vue'
import TweetAround from '@/components/IconButton/TweetAround.vue'
import { Back } from '@element-plus/icons-vue'
import router from '@/router'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'
import ChainFull from '@/components/Chain/chainFull.vue'
import { storeToRefs } from 'pinia'
import { throttle} from 'lodash'
const route=useRoute()
onMounted(async() => {
  window.addEventListener('scroll', onScroll);
});
const usetwiDetail = TwiDetailStore()
const { histories } = storeToRefs(usetwiDetail)
const isCommon = (section) => {
  return Array.isArray(section) && !(Array.isArray(section[0]));
};
const onScroll = throttle(async () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
  const viewportHeight = window.innerHeight;
  const scrollHeight = document.documentElement.scrollHeight;
  if (scrollTop + viewportHeight >= scrollHeight - 100) { // 给予一点容错值
    usetwiDetail.loadData(route.params.emailCut,route.params.tweetId,route.path)
  }
}, 500);

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
onBeforeRouteUpdate(async (to) => {
  if (!usetwiDetail.histories[to.path]) {
    await usetwiDetail.loadData(to.params.emailCut,to.params.tweetId,to.path);
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
  .more{
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
