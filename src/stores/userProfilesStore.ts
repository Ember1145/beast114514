import { reactive } from 'vue'
import { defineStore } from 'pinia'
import { followSev, getUserPage } from '@/api/user/user'
import { myStore } from './myStore'
export const userPageStore = defineStore(
  'userPageStore',
  () => {
    interface UserProfile {
      userId?: string
      username: string
      avatarUrl?: string
      introduction?: string
      createdAt: string
      backUrl?: string
      position?: string
      emailCut: string
      followersCount: number
      followingCount: number
      tweetsCount: number
      status: number
    }
  const useMy=myStore()
    const profiles = reactive<{ [emailCut: string]: UserProfile }>({})
    const getProfile = async (emailCut: string) => {
      if (profiles[emailCut]) {
        return profiles[emailCut]
      }
      const response = await getUserPage(emailCut)
      const profileData: UserProfile = response.data
      profiles[emailCut] = reactive(profileData)
      return profiles[emailCut]
    }
   
    const follow = async (userId, emailCut) => {
      const response=await followSev(userId)
      profiles[emailCut].status = response.data.status
      if (response.data.status === 0) {
        profiles[emailCut].followersCount--
        profiles[useMy.emailCut].followingCount--
      } else { 
        profiles[emailCut].followersCount ++
        profiles[useMy.emailCut].followingCount ++
      }
    }

    return {
      profiles,
      getProfile,
      follow
    }
  },
  {
    persist: {
      enabled: true, //持久化存储
      strategies: [
        {
          key: 'allUser',
          storage: sessionStorage
        }
      ]
    }
  }
)
