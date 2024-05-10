<!--  -->
<template>
  <div class="tweet-interactions">
    <div class="interaction" @mouseenter="handleComment" @mouseleave="commentOut">
      <RoundButton :buttonHover="commentHover">
        <i
          class="fa-regular fa-comment"
          :style="{
            color: commentHover ? 'blue' : 'initial'
          }"
        ></i
      ></RoundButton>
      <div
        class="interaction-count"
        :style="{
          color: commentHover ? 'blue' : 'initial'
        }"
      > {{interactionStore.interactions[props.tweet.tweetId]?.commentCount }}
      </div>
    </div>
<el-dropdown trigger="click">
    <div
      class="interaction"
      @mouseenter="handleShare"
      @mouseleave="shareOut"
      @click.stop
    >
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
      {{interactionStore.interactions[props.tweet.tweetId]?.shareCount }}
      </div>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item  @click.stop="toggleShare">转贴</el-dropdown-item>
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
      <RoundButton :buttonHover="likeHover">
        <i
          :class="[likeActive ? 'fa-solid fa-heart' : 'fa-regular fa-heart']"
          :style="{
            color: likeActive ? 'red' : likeHover ? 'red' : 'initial'
          }"
        ></i
      ></RoundButton>
      <div class="interaction-count"
        :style="{
          color: likeActive ? 'red' : likeHover ? 'red' : 'initial'
        }">
        {{interactionStore.interactions[props.tweet.tweetId]?.likeCount }}
      </div>
    </div>
    <div class="interaction">
      <el-button class="fa-solid fa-users" circle text></el-button>
    </div>
    <div class="right">
      <el-button class="fa-regular fa-bookmark" circle text></el-button>
      <el-button class="fa-solid fa-link" circle text></el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useInteractionStore } from '@/stores/Interaction/InteractionStore';
import { InteractionState } from '@/stores/Interaction/InteractionState';
import { PropType, computed, onMounted, ref } from 'vue'
import { queryStatus } from '@/api/twi/twi';
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
onMounted(async () => {
  const response = await queryStatus(props.tweet.tweetId)
  interactionStore.initInteraction(
      props.tweet.tweetId,
      response.data?.likeCount || 0,
      response.data?.commentCount || 0,
      response.data?.shareCount || 0,
      response.data?.liked || InteractionState.Inactive,
      response.data?.shared || InteractionState.Inactive
    );
})
const likeActive = computed(() => {
  return interactionStore.interactions[props.tweet.tweetId]?.likeState === InteractionState.Active;
});
const toggleLike = () => {
  interactionStore.toggleLike(props.tweet);
};
const toggleShare=()=>{
  interactionStore.toggleShare(props.tweet.tweetId);
}
const shareActive = computed(() => {
  return interactionStore.interactions[props.tweet.tweetId]?.shareState === InteractionState.Active;
});
const commentHover = ref()
const shareHover = ref()
const handleShare = () => {
  shareHover.value = 'rgba(0,255,0,0.1)'
}
const shareOut = () => {
  shareHover.value = ''
}
const handleComment = () => {
  commentHover.value = 'rgba(0,0,255,0.1)'
}
const commentOut = () => {
  commentHover.value = ''
}
const handleLike = () => {
  likeHover.value =  'rgba(255,0,0,0.1)'
}
const likeOut = () => {
  likeHover.value = ''
}
const likeHover = ref()

</script>

<style lang="scss" scoped>
.tweet-interactions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #536471;
  .interaction {
    display: flex;
    align-items: center;
  }
  .right {
    display: flex;
    justify-content: end;
    margin: 0;
  }
}
</style>
