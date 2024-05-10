<!--  -->
<template>
  <div>
    <TwiDown
    v-for="i in tweets" :key="i.tweetId" :tweet="i"
    ></TwiDown>
  </div>
</template>

<script setup lang="ts">
import TwiDown from '@/components/TwiDown.vue'
defineProps({
  tweets: Array as () => Tweet[]
})
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

<style lang="scss" scoped></style>
