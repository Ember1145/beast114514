import { RouteRecordRaw, createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import { useUserLikedStore } from '@/stores/UserPageDown/UserLikedStore'
import { useUserShareAndMyStore } from '@/stores/UserPageDown/ShareAndMyStore'
import { useUserReplyStore } from '@/stores/UserPageDown/UserReplyStore'
import { useDialogStore } from '@/stores/Conversation/dialogStore'
import { useSendListStore } from '@/stores/Conversation/sendListStore'
import { myStore } from '@/stores/myStore'
import { TwiDetailStore } from '@/stores/TwiDetailStore'
import { useWebSocketStore } from '@/stores/useWebSocketStore'
import { useDeliverStore } from '@/stores/Delivery/useDeliverStore'
import { refreshDelivery } from '@/api/twi/twi'
import { getHomeUnreadCount } from '@/api/message'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: LoginView,
    beforeEnter: async (to, from, next) => {
      const my = myStore()
      if (my.token) {
        router.push('/home')
      } else {
        next()
      }
    }
  },
  {
    path: '/',
    component: () => import('@/views/HomeView.vue'),
    children: [
      {
        path: 'messages',
        component: () => import('@/views/ChatView.vue'),
        beforeEnter: async (to, from, next) => {
          const sendListStore = useSendListStore()
          //  if (Object.keys(sendListStore.sendList).length === 0) {
          await sendListStore.loadData()
          next()
        },
        children: [
          {
            path: '',
            component: () => import('@/components/DialogPart/EmptyWindow.vue')
          },
          {
            path: ':conversationId',
            component: () => import('@/components/DialogPart/DialogWindow.vue'),
            beforeEnter: async (to, from, next) => {
              const dialogListStore = useDialogStore()
              const conversationId = to.params.conversationId
              // if (dialogListStore.dialogList[conversationId]) {
              //   next()
              // } else {
              dialogListStore.dialogList = {}
              await dialogListStore.loadData(conversationId)
              next()
              // }
            }
          }
        ]
      },
      {
        path: '',
        component: () => import('@/views/TweetVue.vue'),
        beforeEnter: async (to, from, next) => {
          const deliverStore = useDeliverStore()
          const webSocketStore = useWebSocketStore()
          if (deliverStore.histories.length > 0) {
            next()
          } else {
            const response = await getHomeUnreadCount()
            webSocketStore.messageCount = response.data.messageCount
            webSocketStore.dialogCount = response.data.dialogCount
            await refreshDelivery()
            await deliverStore.loadData()
            next()
          }
        },
        redirect: '/home',
        children: [
          {
            path: 'home',
            components: {
              center: () => import('@/views/TwiVue.vue'),
              right: () => import('@/views/SearchR.vue')
            }
          },
          {
            path: ':emailCut',
            components: {
              center: () => import('@/views/userView.vue'),
              right: () => import('@/views/SearchR.vue')
            },
            children: [
              {
                path: '', // 默认子路径
                name: 'posts',
                component: () => import('@/components/PageDown/withPosts.vue'),
                beforeEnter: async (to, from, next) => {
                  const userShareAndMyStore = useUserShareAndMyStore()
                  const emailCut = to.params.emailCut
                  if (userShareAndMyStore.CombineHistories[to.path]?.combinedTweet.length > 0) {
                    next()
                  } else {
                    await userShareAndMyStore.loadData(emailCut, 1, to.path)
                    next()
                  }
                }
              },
              {
                path: 'replies', // 回复子路径
                name: 'replies',
                component: () => import('@/components/PageDown/withReplies.vue'),
                beforeEnter: async (to, from, next) => {
                  const userRplyStore = useUserReplyStore()
                  const emailCut = to.params.emailCut
                  if (userRplyStore.replyHistories[to.path]?.replyTweet.length > 0) {
                    next()
                  } else {
                    await userRplyStore.loadData(emailCut, 1, to.path)
                    next()
                  }
                }
              },
              {
                path: 'media', // 媒体子路径
                name: 'media',
                component: () => import('@/components/PageDown/withMedia.vue')
              },
              {
                path: 'likes', // 喜欢子路径
                name: 'likes',
                component: () => import('@/components/PageDown/withLikes.vue'),
                beforeEnter: async (to, from, next) => {
                  const userLikedStore = useUserLikedStore()
                  const emailCut = to.params.emailCut
                  if (userLikedStore.likeHistories[to.path]?.likedTweet.length > 0) {
                    next()
                  } else {
                    await userLikedStore.loadData(emailCut, 1, to.path)
                    next()
                  }
                }
              }
            ]
          },
          {
            path: ':emailCut/status/:tweetId',
            components: {
              center: () => import('@/views/TweetIn.vue'),
              right: () => import('@/views/SearchR.vue')
            },
            beforeEnter: async (to, from, next) => {
              const usetwiDetail = TwiDetailStore()
              if (usetwiDetail.histories[to.path]) {
                next()
              } else {
                await usetwiDetail.loadData(to.params.emailCut, to.params.tweetId, to.path)
                next()
              }
            }
          },
          {
            path: 'Search',
            components: {
              center: () => import('@/views/SearchCenter.vue'),
              right: () => import('@/views/SearchR.vue')
            }
          },
          {
            path: 'notifications',
            components: {
              center: () => import('@/views/NotificationPart.vue'),
              right: () => import('@/views/SearchR.vue')
            },
            children: [
              {
                path: '',
                name: 'main',
                component: () => import('@/components/MsgBar/LikeAndFollow.vue'),
                beforeEnter: async (to, from, next) => {
                  const webSocketStore = useWebSocketStore()
                  if (webSocketStore.newArr.length > 0) {
                    next()
                  } else {
                    await webSocketStore.loadData()
                    next()
                  }
                }
              },
              {
                path: 'mentioned',
                name: 'ReplyBar',
                component: () => import('@/components/MsgBar/ReplyMsg.vue')
              }
            ]
          }
        ]
      }
    ]
  }
]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      // 回到savedPosition
      return savedPosition
    } else {
      // 新的导航，滚动到顶部
      return { top: 0 }
    }
  }
})

export default router
