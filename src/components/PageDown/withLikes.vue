<!--  -->
<template>
    <div>
    <TwiDown v-for="i in likeHistories[route.path]?.likedTweet" :tweet="i" :key="i.tweetId"></TwiDown>
    </div>
</template>

<script setup lang="ts">
import TwiDown from '@/components/TwiDown.vue';
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
import { useUserLikedStore } from '@/stores/UserPageDown/UserLikedStore';
const route = useRoute();
const userLikedStore = useUserLikedStore();
const {likeHistories}=storeToRefs(userLikedStore)
</script>

<style lang="scss" scoped>

</style>