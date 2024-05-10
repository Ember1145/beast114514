import { defineStore } from 'pinia'
import { InteractionState } from './InteractionState'
import { ref } from 'vue'
import { handleLike, handleShare } from '@/api/twi/twi'
import { useUserLikedStore } from '../UserPageDown/UserLikedStore'
import { myStore } from '../myStore'
const userLikedStore=useUserLikedStore()
const my=myStore()
interface InteractionStatus {
  tweetId: string
  likeState: InteractionState
  shareState: InteractionState
  likeCount: number
  shareCount: number
  commentCount:number
}

export const useInteractionStore = defineStore(
  'interaction',
  () => {
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
    const interactions = ref<Record<string, InteractionStatus>>({})
    const initInteraction = (
      tweetId: string,
      likeCount: number,
      commentCount:number,
      shareCount: number,
      liked: number,
      shared: number
    ) => {
      interactions.value[tweetId] = {
        tweetId,
        likeCount: likeCount ||0,
        commentCount:commentCount||0,
        shareCount: shareCount || 0, // 使用传入的分享数量
        likeState: liked || InteractionState.Inactive,
        shareState: shared || InteractionState.Inactive,
      }
    }

    const toggleLike = async (tweet: Tweet) => {
      const path=`/${my.emailCut}/likes`
      const interaction = interactions.value[tweet.tweetId]
      const liked =
        interaction.likeState === InteractionState.Active
          ? InteractionState.Inactive
          : InteractionState.Active

      // // Update the local like state
      interaction.likeState = liked
      if (liked === InteractionState.Active) {
        interaction.likeCount++ 
        userLikedStore.pushItem(tweet,path)
      } else {
        interaction.likeCount-- 
      }
      if (interaction.likeCount < 0) {
        interaction.likeCount = 0
      }
      const data = {
        tweetId: tweet.tweetId,
        liked: liked
      }
      await handleLike(data)
    }

    const toggleShare = async(tweetId: string) => {
      const interaction = interactions.value[tweetId]
      const shared =
        interaction.shareState === InteractionState.Active
          ? InteractionState.Inactive
          : InteractionState.Active
          interaction.shareState = shared
          if (shared === InteractionState.Active) {
            interaction.shareCount++ 
          } else {
            interaction.shareCount-- 
          }
          if (interaction.shareCount < 0) {
            interaction.shareCount = 0
          }
          const data = {
            tweetId: tweetId,
            shared:shared
          }
          await handleShare(data)
        }
    

    const removeAll=()=>{
      interactions.value={}
    }

    return { interactions, initInteraction, toggleLike, toggleShare ,removeAll}
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'Interaction',
          storage: localStorage,
        }
      ]
    }
  }
)
