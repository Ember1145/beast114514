<template>
  <div class="aside">
    <div class="top"><el-button type="primary" circle plain class="fa-solid fa-x" ></el-button></div>
    <el-aside class="icon-list" v-for="item in items" :key="item.id" :span="4">
      <div class="icon-item">
        <el-button
          round
          size="large"
          link
          @click="handleClick(item.path, item.id)"
          :class="{ active: useLeft.activeButton === item.id }"
        >
          <i :class="item.iconClass"></i>
          <div class="font">{{ item.text }}</div>
        </el-button>
      </div>
    </el-aside>
    <el-button type="primary" class="tip" @click="visible = true">发帖</el-button>
  </div>
  <el-dropdown placement="top" trigger="click">
    <div class="avatar-button">
      <el-avatar :size="50" icon="UserFilled" :src="useMy.avatarUrl" />
      <div class="rig">
        <div>{{ useMy.username }}</div>
        <div>@{{ useMy.emailCut }}</div>
      </div>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item >添加用户</el-dropdown-item>
        <el-dropdown-item @click="clearToken">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <el-dialog v-model="visible" title="发送推文" @close="resetField">
    <UploadV ref="uploadRef"></UploadV>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import UploadV from './uploadV.vue'
import { useLeftStore } from '@/stores/leftStore'
import { useRouter } from 'vue-router'
import { myStore } from '@/stores/myStore'
import { useInteractionStore } from '@/stores/Interaction/InteractionStore'
const interactionStore=useInteractionStore()
const useMy=myStore()
const router = useRouter()
const useLeft = useLeftStore()
const uploadRef = ref()
interface IconItem {
  id: number
  iconClass: string
  text: string
  path: string // 新增的路径属性
}
const visible = ref(false)
const resetField = () => {
  if (uploadRef.value) {
    uploadRef.value.clearTweet()
  }
}
const handleClick = async (path: any, id: any) => {
  await router.push(path)
  useLeft.setActiveButton(id)
}
const items = ref<IconItem[]>([
  { id: 1, iconClass: 'fa-solid fa-house', text: '主页', path: '/home' },
  { id: 2, iconClass: 'fa-solid fa-diagram-project', text: '社群', path: '/Search' },
  { id: 3, iconClass: 'fa-solid fa-star', text: '书签', path: '/test1' },
  { id: 4, iconClass: 'fa-solid fa-bell', text: '通知', path: '/test' },
  { id: 5, iconClass: 'fa-solid fa-floppy-disk', text: '私信', path: '/home' },
  { id: 6, iconClass: 'fa-solid fa-heart', text: '个人资料',  path: `/${useMy.emailCut}` }
])
const clearToken=()=>{
  useMy.removeAll()
  interactionStore.removeAll()
  router.push('/login')

}
</script>

<style scoped lang="scss">
.aside {
  position: fixed;

  .top {
    margin-left: 50px;
    margin-top: 20px;
    .fa-solid fa-x {
      width: 60px;
    }
  }
  .tip {
    width: 13vw;
    height: 7vh;
    border-radius: 30px;
    margin-left: 20px;
  }
}

.el-aside {
  margin-left: 20px;
}

.icon-item .el-button {
  width: 200px;
  height: 80px;
  margin-left: 10px;
  i {
    font-size: 20px;
    margin-right: 10px;
  }
  .font {
    font-size: 20px;
  }
}
.active {
  color: black;
}

.icon-item .el-button:hover {
  background-color: #f0f0f0;
}
.el-dropdown-menu{
  width: 200px;
}
.avatar-button {
  position: fixed;
  bottom: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  width: 15vw;
  height: 10vh;
  border: none;
  border-radius: 12px;
  .el-avatar {
    margin-left: 10px;
  }
  .rig{
    margin-left: 10px;
  }
}
.avatar-button:hover {
  background-color: #f0f0f0;
}

.avatar {
  width: 24px;
  height: 24px;
  margin-right: 8px;
}
</style>
