<!--  -->
<template>
  <div class="tweet-interactions">
    <div class="interaction" @mouseenter="handleComment" @mouseleave="commentOut">
      <RoundButton :buttonHover="commentHover || props.fatherColor">
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
        <RoundButton :buttonHover="shareHover || props.fatherColor">
          <i
            class="fa-solid fa-repeat"
            :style="{
              color: shareActive ? 'green' : shareHover ? 'green' : 'initial'
            }"
          ></i
        ></RoundButton>
        <div
          class="share-count"
          :class="{ 'bubble-animation': shareActive }"
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
      <RoundButton :buttonHover="likeHover || props.fatherColor">
        <i
          :class="[likeActive ? 'fa-solid fa-heart liked' : 'fa-regular fa-heart']"
          :style="{
            color: likeActive ? 'red' : likeHover ? 'red' : 'initial'
          }"
        ></i
      ></RoundButton>
      <div
        class="interaction-count"
        :class="{ 'fade': isFading }"
        :style="{
          color: likeActive ? 'red' : likeHover ? 'red' : 'initial'
        }"
      >
        {{ interactionStore.interactions[props.tweet?.tweetId]?.likeCount }}
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
import { useInteractionStore } from '@/stores/Interaction/InteractionStore'
import { InteractionState } from '@/stores/Interaction/InteractionState'
import { PropType, computed, onMounted, ref } from 'vue'
import { queryStatus } from '@/api/twi/twi'
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
  },
  fatherColor: String
})
const interactionStore = useInteractionStore()
onMounted(async () => {
  const response = await queryStatus(props.tweet.tweetId)
  interactionStore.initInteraction(
    props.tweet.tweetId,
    response.data?.likeCount || 0,
    response.data?.commentCount || 0,
    response.data?.shareCount || 0,
    response.data?.liked || InteractionState.Inactive,
    response.data?.shared || InteractionState.Inactive
  )
})
const likeActive = computed(() => {
  return interactionStore.interactions[props.tweet?.tweetId]?.likeState === InteractionState.Active
})
const isFading=ref()
const toggleLike = () => {
  interactionStore.toggleLike(props.tweet)
  isFading.value = true; // 触发淡入动画
    setTimeout(() => {
       isFading.value = false; // 动画完成后，恢复正常
    }, 300);
}
const toggleShare = () => {
  interactionStore.toggleShare(props.tweet.tweetId)
}
const shareActive = computed(() => {
  return interactionStore.interactions[props.tweet?.tweetId]?.shareState === InteractionState.Active
})
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
  likeHover.value = 'rgba(255,0,0,0.1)'
}
const likeOut = () => {
  likeHover.value = ''
}
const likeHover = ref()
</script>

<style lang="scss" scoped>
@keyframes like-animation {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.5);
  }
  100% {
    transform: scale(1);
  }
}
.liked {
  animation: like-animation 0.5s ease; /* 为 'liked' 状态添加动画 */
}
.interaction-count {
  transition: opacity 0.3s ease;
}
.fade {
  opacity: 0.5;
}
@keyframes slide-and-fade {
  0% {
    transform: translateY(100%);  /* 初始状态为完全下沉 */
    opacity: 0.1;
  }
  100% {
    transform: translateY(0);  /* 向上滑动并开始消失 */
    opacity: 1;
  }
}
.bubble-animation {
  animation: slide-and-fade 0.5s ease forwards; /* 加上 forwards 让动画完成时停在结束位置 */
}
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
    color: rgb(100, 170, 224)
  }
}
</style>
