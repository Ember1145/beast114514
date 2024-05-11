<template>
  <div class="tweet-inter">
    <div class="interaction" @mouseenter="handleComment" @mouseleave="commentOut">
      <RoundButton :buttonHover="commentHover">
        <i
          class="fa-regular fa-comment"
          :style="{
            color: commentHover ?  'rgb(75, 155, 216)' : 'initial'
          }"
        ></i
      ></RoundButton>
      <div
        class="interaction-count"
        :style="{
          color: commentHover ? 'rgb(100, 170, 224)' : 'initial'
        }"
      >
        {{ interactionStore.interactions[props.tweet?.tweetId]?.commentCount }}
      </div>
    </div>
    <el-dropdown trigger="click">
      <div class="interaction" @mouseenter="handleShare" @mouseleave="shareOut" @click.stop>
        <RoundButton :buttonHover="shareHover">
          <i
            class="fa-solid fa-repeat"
            :style="{
              color: shareActive ? 'green' : shareHover ? 'green' : 'initial'
            }"
          ></i
        ></RoundButton>
        <div
          class="share-count"
          :style="{
            color: shareActive ? 'green' : shareHover ? 'green' : 'initial'
          }"
        >
          {{ interactionStore.interactions[props.tweet?.tweetId]?.shareCount }}
        </div>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click.stop="toggleShare">转贴</el-dropdown-item>
          <el-dropdown-item>引用</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <div
        class="interaction"
        @mouseenter="handleLike"
        @mouseleave="likeOut"
        @click.stop="toggleLike"
      >
        <RoundButton :buttonHover="buttonHover">
          <i
            :class="[likeActive ? 'fa-solid fa-heart' : 'fa-regular fa-heart']"
            :style="{
              color: likeActive ? 'red' : likeHover ? 'red' : 'initial'
            }"
          ></i></RoundButton>
        <div
          class="interaction-count"
          :style="{
            color: likeActive ? 'red' : likeHover ? 'red' : 'initial'
          }"
        >
        {{interactionStore.interactions[tweet.tweetId].likeCount }}
        </div>
      </div>
    <div class="interaction">
      <el-button class="fa-regular fa-bookmark" circle text></el-button> 
    </div>
    <div class="interaction">
      <el-button class="fa-solid fa-link" circle text></el-button>
    </div>
  </div>
</template>

<script setup lang="ts">

import { InteractionState } from '@/stores/Interaction/InteractionState';
import { useInteractionStore } from '@/stores/Interaction/InteractionStore';
import { PropType, computed,   ref } from 'vue'
const buttonHover=ref()
const handleLike=()=>{
  likeHover.value = true
  buttonHover.value='rgba(255,0,0,0.1)'
}
const likeOut=()=>{
  likeHover.value=false
  buttonHover.value='white'
}
const commentHover = ref()
const handleComment = () => {
  commentHover.value = 'rgba(0,0,255,0.1)'
}
const commentOut = () => {
  commentHover.value = ''
}
const shareHover = ref()
const handleShare = () => {
  shareHover.value = 'rgba(0,255,0,0.1)'
}
const shareOut = () => {
  shareHover.value = ''
}
const shareActive = computed(() => {
  return interactionStore.interactions[props.tweet?.tweetId]?.shareState === InteractionState.Active
})
const toggleShare = () => {
  interactionStore.toggleShare(props.tweet.tweetId)
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
const props = defineProps({
  tweet: {
    type: Object as PropType<Tweet>,
    required: true
  }
});
const interactionStore = useInteractionStore();

// 创建计算属性来确定按钮样式
const likeActive = computed(() => {
  return interactionStore.interactions[props.tweet.tweetId]?.likeState === InteractionState.Active;
});
const toggleLike = () => {
  interactionStore.toggleLike(props.tweet);
};
const likeHover = ref(false)
</script>

<style lang="scss" scoped>
.tweet-inter {
  border-top: 1px solid var(--el-border-color);
  border-bottom: 1px solid var(--el-border-color);
  height: 5vh;
  padding-top: 10px;
  padding-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  color: #536471;

  .interaction {
    display: flex;
    align-items: center;
    cursor: pointer;
  }
}
</style>
