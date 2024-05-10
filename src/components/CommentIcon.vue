<template>
  <div class="tweet-inter">
    <div class="interaction">
      <el-button circle text> <i class="fa-regular fa-comment"> </i></el-button
      > {{interactionStore.interactions[tweetId].commentCount }}
    </div>
    <div class="interaction">
      <el-button class="fa-solid fa-repeat" circle text></el-button> {{interactionStore.interactions[tweetId].shareCount }}
    </div>
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
        {{interactionStore.interactions[tweetId].likeCount }}
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
import { computed,  onMounted,  ref } from 'vue'
onMounted(async()=>{


})
const buttonHover=ref()
const handleLike=()=>{
  likeHover.value = true
  buttonHover.value='rgba(255,0,0,0.1)'
}
const likeOut=()=>{
  likeHover.value=false
  buttonHover.value='white'
}
const props=defineProps({
  tweetId:String
})
const interactionStore = useInteractionStore();

// 创建计算属性来确定按钮样式
const likeActive = computed(() => {
  return interactionStore.interactions[props.tweetId]?.likeState === InteractionState.Active;
});
const toggleLike = () => {
  interactionStore.toggleLike(props.tweetId);
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
