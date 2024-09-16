<template>
  <div class="common">
    <el-container class="container-main">
      <el-header
        ><el-button text circle :icon="Back" size="large" @click="$router.go(-1)"></el-button>
        <ul class="ul1">
          <li>{{ profiles[route.params.emailCut]?.username }}</li>
          <li>{{ profiles[route.params.emailCut]?.tweetsCount }}推文</li>
        </ul>
      </el-header>
      <el-main>
        <el-image :src="profiles[route.params.emailCut]?.backUrl" fit="cover"></el-image
        ><el-avatar :size="100" :src="profiles[route.params.emailCut]?.avatarUrl"></el-avatar>
        <div class="but" v-if="useMy.userId === profiles[route.params.emailCut]?.userId">
          <el-button round size="large" @click="editInfo">编辑个人资料</el-button>
        </div>
        <div class="but" v-if="useMy.userId !== profiles[route.params.emailCut]?.userId">
          <el-button circle size="large" :icon="MoreFilled"></el-button>
          <!-- 私信按钮 -->
          <el-button circle size="large" @click="handleEnv"
            ><i class="fa-solid fa-envelope"></i
          ></el-button>
          <el-button
            v-if="profiles[route.params.emailCut]?.status === 1"
            round
            color="black"
            size="large"
            @click="follow(profiles[route.params.emailCut].userId, route.params.emailCut)"
          >
            已关注
          </el-button>

          <el-button
            v-else-if="profiles[route.params.emailCut]?.status === 2"
            round
            color="black"
            size="large"
            @click="follow(profiles[route.params.emailCut]?.userId, route.params.emailCut)"
          >
            互相关注
          </el-button>
          <el-button
            v-else
            round
            color="black"
            size="large"
            @click="follow(profiles[route.params.emailCut]?.userId, route.params.emailCut)"
          >
            关注
          </el-button>
        </div>
      </el-main>
      <el-footer>
        <ul class="ul2">
          <li>{{ profiles[route.params.emailCut]?.username }}</li>
          <li>@{{ profiles[route.params.emailCut]?.emailCut }}</li>
          <li>
            {{ profiles[route.params.emailCut]?.introduction }}
          </li>
          <li>{{ profiles[route.params.emailCut]?.position }}</li>
          <li>
            <span class="following"
              >{{ profiles[route.params.emailCut]?.followingCount
              }}<span class="f">正在关注</span></span
            ><span class="follow"
              >{{ profiles[route.params.emailCut]?.followersCount
              }}<span class="d">关注者</span></span
            >
          </li>
        </ul>
        <el-menu mode="horizontal" :ellipsis="false" default-active="1">
          <el-menu-item index="1" @click="router.push({ name: 'posts' })">帖子</el-menu-item>
          <el-menu-item index="2" @click="router.push({ name: 'replies' })">回复</el-menu-item>
          <el-menu-item index="3" @click="router.push({ name: 'media' })">媒体</el-menu-item>
          <el-menu-item index="4" @click="router.push({ name: 'likes' })">喜欢</el-menu-item>
        </el-menu>
      </el-footer>
      <RouterView></RouterView>
    </el-container>
    <transition name="fade">
      <div class="dia" v-show="userVisible">
        <el-container>
          <el-header
            ><el-tooltip content="关闭" :show-arrow="false"
              ><el-button
                text
                circle
                :icon="Back"
                size="large"
                @click="userVisible = false"
              ></el-button
            ></el-tooltip>
            <el-button round color="black" @click="saveAll">保存</el-button>
          </el-header>
          <el-scrollbar height="400px">
            <el-main>
              <div class="image-container">
                <el-image :src="displayBg" fit="cover"></el-image>
                <div class="but">
                  <el-button circle :icon="CameraFilled" color="black" @click="upload"></el-button>
                  <el-button circle :icon="CloseBold" color="black" @click="clearFile"></el-button>
                </div>
              </div>
              <div class="avatar">
                <el-avatar :size="100" :src="displayAvatar"></el-avatar>
                <el-button
                  circle
                  :icon="CameraFilled"
                  color="black"
                  @click="uploadAvatar"
                ></el-button>
              </div>
            </el-main>
            <el-footer>
              <el-form>
                <el-form-item>
                  <el-input
                    v-model="username"
                    style="max-width: 600px"
                    maxlength="10"
                    show-word-limit
                  >
                    <template #prepend>用户名</template>
                  </el-input></el-form-item
                >
                <el-form-item>
                  <el-input
                    v-model="introduction"
                    type="textarea"
                    placeholder="简介"
                    maxlength="100"
                    show-word-limit
                  >
                  </el-input
                ></el-form-item>
                <el-form-item>
                  <el-input
                    v-model="position"
                    style="max-width: 600px"
                    maxlength="10"
                    show-word-limit
                  >
                    <template #prepend>位置</template>
                  </el-input></el-form-item
                >
              </el-form>
            </el-footer>
          </el-scrollbar>
          <div class="dialog" v-show="uploadVisible">
            <UploadSev ref="crop"></UploadSev>
            <div class="com">
              <el-button :icon="Back" circle text @click="uploadVisible = false"></el-button>
              <el-button v-if="isUploadBg" @click="saveBg" color="black">保存背景</el-button>
              <el-button v-else @click="saveAvatar" color="black">保存头像</el-button>
            </div>
          </div>
        </el-container>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Back } from '@element-plus/icons-vue'
import { CameraFilled } from '@element-plus/icons-vue'
import { CloseBold } from '@element-plus/icons-vue'
import UploadSev from '@/components/TweetUploader/uploadSev.vue'
import { singleService,  userUpdateService } from '@/api/user/user'
import { ElMessage } from 'element-plus'
import { userPageStore } from '@/stores/userProfilesStore'
import { myStore } from '@/stores/myStore'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'
import { MoreFilled } from '@element-plus/icons-vue'
import router from '@/router'
import { storeToRefs } from 'pinia'
import { throttle } from 'lodash'
import { useUserLikedStore } from '@/stores/UserPageDown/UserLikedStore'
import { useUserShareAndMyStore } from '@/stores/UserPageDown/ShareAndMyStore'
import { useSendListStore } from '@/stores/Conversation/sendListStore'
import { getConversation } from '@/api/Dialog/postMsg'
const userShareAndMyStore = useUserShareAndMyStore()
const userLikedStore = useUserLikedStore()
const sendListStore = useSendListStore()
const my = myStore()
const { CombineHistories } = storeToRefs(userShareAndMyStore)
const { likeHistories } = storeToRefs(userLikedStore)
onMounted(async () => {
  window.addEventListener('scroll', onScroll)
})
const handleEnv = async() => {
  const profile = profiles.value[route.params.emailCut]
  const { avatarUrl, emailCut, username, userId,conversationId } = profile
  if(conversationId){
    sendListStore.sendList[conversationId]={
    conversationId,
    avatarUrl,
    emailCut,
    username,
    userId
  }
  router.push(`/messages/${conversationId}`)
  return
  }
  const response = await getConversation(my.userId, profile.userId)
  profiles.value[route.params.emailCut].conversationId=response.data
  sendListStore.sendList[response.data]={
    avatarUrl,
    emailCut,
    username,
    userId,
    conversationId:response.data
  }
  router.push(`/messages/${response.data}`)
  
}
const onScroll = throttle(async () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  const viewportHeight = window.innerHeight
  const scrollHeight = document.documentElement.scrollHeight
  if (scrollTop + viewportHeight >= scrollHeight - 100) {
    if (route.name === 'likes') {
      const current = likeHistories.value[route.path]?.current
      userLikedStore.loadData(route.params.emailCut, current, route.path)
      saveCurrentPageState(route.path)
    } else if (route.name === 'posts') {
      const current = CombineHistories.value[route.path]?.current
      userShareAndMyStore.loadData(route.params.emailCut, current, route.path)
      saveShareState(route.path)
    }
  }
}, 1000)
const saveShareState = (path) => {
  userShareAndMyStore.savePageState(path, {
    current: CombineHistories.value[route.path]?.current || 1,
    combinedTweet: CombineHistories.value[route.path]?.combinedTweet
  })
}
const saveCurrentPageState = (path) => {
  userLikedStore.savePageState(path, {
    current: likeHistories.value[route.path]?.current || 1,
    likedTweet: likeHistories.value[route.path]?.likedTweet
  })
}
const useUserStore = userPageStore()
const { profiles } = storeToRefs(useUserStore)
const useMy = myStore()
const route = useRoute()
const follow = async (followId: any, emailCut: any) => {
  await useUserStore.follow(followId, emailCut)
}
const editInfo = () => {
  userVisible.value = true
  introduction.value = profiles.value[route.params.emailCut]?.introduction
  position.value = profiles.value[route.params.emailCut]?.position
  username.value = profiles.value[route.params.emailCut]?.username
}
watch(
  () => route.params.emailCut,
  async (newEmailCut) => {
    if (newEmailCut) {
      await useUserStore.getProfile(newEmailCut)
    }
  },
  { immediate: true }
)
onBeforeRouteUpdate(async (to, from) => {
  if (from.params.emailCut !== to.params.emailCut) {
    const userShareAndMyStore = useUserShareAndMyStore()
    const emailCut = to.params.emailCut
    if (userShareAndMyStore.CombineHistories[to.path]?.combinedTweet.length > 0) {
      console.log('什么都不做')
    } else {
      await userShareAndMyStore.loadData(emailCut, 1, to.path)
    }
  }
})
const avatar = ref()
const avatarUrl = ref()
const bgUrl = ref()
const uploadVisible = ref(false)
const crop = ref()
const userVisible = ref(false)
const username = ref()
const introduction = ref()
const position = ref()
const isUploadBg = ref(false)
const backUrl = ref()
const displayBg = computed(() => {
  if (bgUrl.value) {
    return bgUrl.value // 裁剪后展示裁剪的结果图片
  } else {
    return profiles.value[route.params.emailCut]?.backUrl // 未裁剪时展示原有图片
  }
})
const displayAvatar = computed(() => {
  if (avatar.value) {
    return avatar.value // 裁剪后展示裁剪的结果图片
  } else {
    return profiles.value[route.params.emailCut]?.avatarUrl // 未裁剪时展示原有图片
  }
})

const isAllFieldEmpty = (object: any) => {
  return Object.values(object).every(
    (value) => value === undefined || value === null || value === ''
  )
}
const upload = () => {
  uploadVisible.value = true
  isUploadBg.value = true
  crop.value.clickUploadButton()
}
const uploadAvatar = () => {
  uploadVisible.value = true
  crop.value.clickUploadButton()
}
const saveBg = async () => {
  bgUrl.value = await crop.value.getResult()
  isUploadBg.value = false
  uploadVisible.value = false
}
const saveAvatar = async () => {
  avatar.value = await crop.value.getResult()
  uploadVisible.value = false
}
const clearFile = () => {
  bgUrl.value = ''
}
const saveAll = () => {
  let uploadPromises = []

  if (bgUrl.value) {
    const uploadBgPromise = singleService(bgUrl.value)
      .then((response: any) => {
        backUrl.value = response.data
      })
      .catch(() => {
        ElMessage.error('上传背景图失败')
      })
    uploadPromises.push(uploadBgPromise)
  }

  if (avatar.value) {
    const uploadAvatarPromise = singleService(avatar.value)
      .then((response: any) => {
        avatarUrl.value = response.data
      })
      .catch(() => {
        ElMessage.error('上传头像失败')
      })
    uploadPromises.push(uploadAvatarPromise)
  }
  Promise.all(uploadPromises).then(async () => {
    // 假设你这里已经有了 oldAvatarUrl 和 oldBackUrl 变量，它们存储了旧的 URL
    const userInfo = {
      avatarUrl: avatarUrl.value,
      backUrl: backUrl.value,
      username: username.value,
      position: position.value,
      introduction: introduction.value,
      // 只有在 URL 真的改变时才添加旧的 URL
      ...(avatarUrl.value
        ? { oldAvatarUrl: profiles.value[route.params.emailCut]?.avatarUrl }
        : {}),
      ...(backUrl.value ? { oldBackUrl: profiles.value[route.params.emailCut]?.backUrl } : {})
    }
    Object.keys(userInfo).forEach((key) => {
      if (userInfo[key] === undefined) {
        delete userInfo[key]
      }
    })
    if (isAllFieldEmpty(userInfo)) {
      ElMessage.error('不能提交空的表单')
      return
    }
    await userUpdateService(userInfo)
    // Pinia state update 可能需要剔除 oldAvatarUrl 和 oldBackUrl
    const { oldAvatarUrl, oldBackUrl, ...userProfileUpdate } = userInfo
    useUserStore.profiles[route.params.emailCut] = {
      ...useUserStore.profiles[route.params.emailCut],
      ...userProfileUpdate
    }

    ElMessage.success('修改成功!')
    userVisible.value = false
  })
}
</script>
<style scoped lang="scss">
.container-main {
  width: 100%;
  border: 1px solid var(--el-border-color);
  border-top: none;
  box-sizing: border-box;
  .el-header {
    display: flex;
    height: 8vh;
    align-items: center;
    position: sticky;
    top: 0;
    background-color: rgba(255, 255, 255, 0.8);
    z-index: 2000;
    .el-button {
      z-index: 2001;
    }
  }
  .ul1 {
    margin-left: 2px;
  }
  ul li:first-child {
    font-weight: 600;
  }
  .el-main {
    position: relative;
    margin: 0;
    padding: 0;
    .el-image {
      height: 24vh;
      width: 100%;
      object-fit: cover;
    }
    .el-avatar {
      position: absolute;
      bottom: 0px;
      left: 20px;
    }
    .but {
      display: flex;
      justify-content: flex-end;
      .el-button {
        margin-top: 20px;
        margin-right: 10px;
        margin-left: 0;
        font-weight: 600;
      }
    }
  }
  .el-footer {
    height: auto;
    width: 100%;
    padding: 0;
    margin: 0;
    .ul2 {
      margin-left: 15px;
      margin-top: 10px;
      li {
        width: 88%;
        margin-left: 3px;
        margin-top: 3px;
        word-wrap: break-word;
        word-break: break-all;
        &:nth-child(2) {
          color: #536471;
        }
        &:nth-last-child(1) {
          margin-bottom: 20px;
        }
        &:nth-last-child(2) {
          color: #536471;
          font-size: 13px;
        }
        &:last-child {
          .following {
            font-weight: 600;
            margin-right: 10px;
            &:hover {
              border-bottom: 1px solid black;
              cursor: pointer;
            }
            .f {
              font-weight: 400;
              margin-left: 3px;
            }
          }
          .follow {
            font-weight: 600;
            &:hover {
              border-bottom: 1px solid black;
              cursor: pointer;
            }
            .d {
              font-weight: 400;
              margin-left: 3px;
            }
          }
        }
      }
    }
    .el-menu {
      width: 100%;
      .el-menu-item {
        width: 25%;
      }
    }
  }
}
.dia {
  margin-top: 80px;
  width: 40vw;
  padding: 0;
  height: auto;
  border-radius: 20px;
  position: fixed; // 使用固定定位
  top: 35%; // top 和 left 都设为 50% 将 div 放置于页面中心
  left: 50%;
  transform: translate(-50%, -50%); // 使用 transform 平移将 div 完全居中
  z-index: 100;
  background-color: #f0f8ff;
  .el-container {
    .el-header {
      display: flex;
      position: sticky;
      top: 0;
      z-index: 101;
      justify-content: space-between;
      align-items: center;
      padding: 0;
      background-color: #f0f8ff;
      border-top-left-radius: 20px;
      border-top-right-radius: 20px;
    }
    .dialog {
      width: calc(40vw + 30px);
      background-color: #f0f8ff;
      height: 100%;
      border-radius: 20px;
      position: absolute;
      z-index: 103;
      .com {
        display: flex;
        justify-content: space-between;
        margin: 20px;
      }
    }
    .el-main {
      padding: 0;
      .image-container {
        position: relative;
        .el-image {
          height: 20vh;
          width: 100%;
          object-fit: cover;
        }
        .but {
          position: absolute;
          top: 50%;
          left: 50%;
          opacity: 0.8;
          transform: translate(-50%, -50%);
        }
      }
      .avatar {
        display: flex;
        align-items: center;
        position: relative;
        .el-avatar {
          position: relative;
          bottom: 40px;
          left: 20px;
        }
        .el-button {
          position: absolute;
          opacity: 0.7;
          left: 50px;
          transform: translate(10%, -120%);
        }
      }
    }
    .el-footer {
      height: 30vh;
    }
  }
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
