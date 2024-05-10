import { RouteRecordRaw, createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import { userPageStore } from '@/stores/userProfilesStore';
import { useScrollStore } from '@/stores/useScrollStore';
import { nextTick } from 'vue';
import { useUserLikedStore } from '@/stores/UserPageDown/UserLikedStore'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: LoginView
  },
  {
    path: '/',
    component: () => import('@/views/HomeView.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        components: {
          center: () => import('@/views/TwiVue.vue'),
          right: () => import('@/views/SearchR.vue')
        }
      },
      {
        path: '/:emailCut',
        components: {
          center: () => import('@/views/userView.vue'),
          right: () => import('@/components/Chain/chainPart.vue')
        },
        children: [
          {
            path: '', // 默认子路径
            name: 'posts',
            component: () => import('@/components/PageDown/withPosts.vue'),
            
          },
          {
            path: 'replies', // 回复子路径
            name: 'replies',
            component: () => import('@/components/PageDown/withReplies.vue')
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
              const userLikedStore = useUserLikedStore();
              const emailCut = to.params.emailCut;
              if(userLikedStore.likeHistories[to.path]?.likedTweet.length>0){
               next()
              }else{
                await userLikedStore.loadData(emailCut,userLikedStore.likeHistories[to.path]?.current||1,to.path);
                next();
              }         
            },
          }
        ]
      },
      {
        path: '/:emailCut/status/:tweetId',
        components: {
          center: () => import('@/views/TweetIn.vue'),
          right: () => import('@/views/SearchR.vue')
        }
      },
      {
        path: '/Search',
        components: {
          center: () => import('@/views/SearchCenter.vue'),
          right: () => import('@/views/userView.vue')
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
  scrollBehavior(to, from) {
    const scrollStore = useScrollStore();
    const savedPosition = scrollStore.getPosition(to.path)
    if (savedPosition) {
      window.scrollTo(0, savedPosition);
    } else {
      window.scrollTo(0, 0)
    }
  },
})
// router.afterEach(async (to, from) => {
//   await nextTick(); 
//   const scrollStore = useScrollStore();
//   setTimeout(() => {
//     const savedPosition = scrollStore.getPosition(to.path);
//     if (savedPosition) {
//       window.scrollTo(0, savedPosition);
//     }
//   },200);
// });
export default router
