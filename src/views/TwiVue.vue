<template>
  <div class="layout">
      <div class="one">
        <el-menu mode="horizontal" :ellipsis="false" default-active="1" class="menu">
          <el-menu-item index="1">为您推荐</el-menu-item>
          <el-menu-item index="2">正在关注</el-menu-item>
        </el-menu>
      </div>
    <div class="down"><TweetPublish></TweetPublish></div>
    <TwiDown v-for="tweet in deliverStore.histories" :key="tweet.tweetId" :tweet="tweet"></TwiDown>
  </div>
</template>
<script setup lang="ts">
import TweetPublish from '@/components/TweetUploader/TweetPublish.vue'
import TwiDown from '@/components/TwiDown.vue'
import { useDeliverStore } from '@/stores/Delivery/useDeliverStore'
import { onMounted, onUnmounted } from 'vue'
import { throttle } from 'lodash'
import { refreshDelivery } from '@/api/twi/twi'
const deliverStore = useDeliverStore()
onMounted(async () => {

  window.addEventListener('scroll', onScroll)
})
const onScroll = throttle(async () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  const viewportHeight = window.innerHeight
  const scrollHeight = document.documentElement.scrollHeight
  if (scrollTop + viewportHeight >= scrollHeight - 100) {
    deliverStore.loadData()
  }
}, 500)
// onUnmounted(async() => {
//   
// })

</script>
<script lang="ts">
export default {
  name: 'TwiVue',
}
</script>
<style lang="scss">
.layout {
  height: 100vh;
  width: 100%;
  .one {
    max-width: 45.4vw;
    padding: 0;
    width: 100%;
    border: 1px solid var(--el-border-color);
    border-top: none;
    border-bottom: none;
    position: fixed;
    opacity: 0.8;
    top: 0;
    z-index: 1000; 
    .menu {
      .el-menu-item {
        width: 50%;
      }
    }
  }
  .down{
    margin-top: 60px;
  }
}
</style>
