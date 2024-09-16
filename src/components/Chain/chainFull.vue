<template>
  <div>
    <div class="sum" v-for="(topChain, index) in tweetChain" :key="index">
      <ChainPart :topChain="topChain" :isFold="isFold(topChain)" :hideLastChain="hideLastChain">
      </ChainPart>
    </div>
  </div>
</template>

<script setup lang="ts">

import { PropType } from 'vue'
import ChainPart from './chainPart.vue'
 defineProps({
  tweetChain: {
    type: Object as PropType<Tweet[][]>,
    required: true
  },
  hideLastChain: Boolean
})
const isFold = (topChain) => {
  return topChain.length > 2 && topChain[0].tweetId !== topChain[1].parentId
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
</script>

<style lang="scss" scoped>
.sum {
  height: auto;
}
</style>
