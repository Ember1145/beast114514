
<template>
    <div>
    <ChainFull :tweetChain="userReplyStore.replyHistories[route.path]?.replyTweet" :hideLastChain="true"></ChainFull>
    </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import ChainFull from '../Chain/chainFull.vue';
import { useUserReplyStore } from '@/stores/UserPageDown/UserReplyStore';
const route = useRoute();
const userReplyStore=useUserReplyStore()
</script>
<style lang="scss" scoped>

</style>