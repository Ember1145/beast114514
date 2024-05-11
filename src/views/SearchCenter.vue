<!--  -->
<template>
  <div class="sum">
    <div class="com">
      <div class="header">
      <el-button circle :icon="Back" text @click="router.go(-1)"></el-button>
      <el-input
        v-model="localQu"
        style="width: 80%"
        size="large"
        placeholder="Please Input"
        clearable
        :prefix-icon="Search"
        @keyup.enter="search"
      />
      <el-button circle :icon="Back"></el-button>
    </div>
    <div class="main">
      <el-menu mode="horizontal" :ellipsis="false" default-active="1">
        <el-menu-item @click="updateQuery({ qu })" index="1">热门</el-menu-item>
        <el-menu-item @click="updateQuery({ f: 'live' })" index="2">最新</el-menu-item>
        <el-menu-item @click="updateQuery({ f: 'user' })" index="3">用户</el-menu-item>
        <el-menu-item @click="updateQuery({ f: 'list' })" index="4">列表</el-menu-item>
      </el-menu>
    </div>
    </div>
    
    <div class="foot">
      <queryFoot :tweets="tweets" v-if="!route.query.f" />
      <queryLive v-else-if="route.query.f === 'live'" />
      <queryUser :items="items" v-else-if="route.query.f === 'user'" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Back, Search } from '@element-plus/icons-vue'
import {  onMounted, ref, watchEffect } from 'vue'
import queryFoot from '@/components/QueryPart/queryFoot.vue'
import queryLive from '@/components/QueryPart/queryLive.vue'
import queryUser from '@/components/QueryPart/queryUser.vue'
import { LocationQueryValue, useRoute } from 'vue-router'
import { userSearch } from '@/api/Search/Search'
import router from '@/router'
const route = useRoute()
const qu = ref(route.query.qu)
const f = ref(route.query.f)
const items = ref([]);
const tweets = ref([]);
interface RouteQuery {
  qu: LocationQueryValue | LocationQueryValue[];
  f?: LocationQueryValue | LocationQueryValue[]; // f 是可选属性
}
const search = () => {
  router.push({ path: '/Search', query: { qu: localQu.value } })
}
const localQu = ref(''); // 本地的响应式变量

// 当组件初始化时，设置 localQu 为 route.query.qu 的值
onMounted(() => {
  if (route.query.qu) {
    localQu.value = route.query.qu as string;
  }
});
watchEffect(async() => {
  qu.value = route.query.qu
  f.value = route.query.f
  const params:RouteQuery = { qu: qu.value }
  if (f.value !== undefined) {
    params.f = f.value
  }

   const response=await userSearch(params)
   items.value=response.data.users
   tweets.value=response.data.tweets
})

const updateQuery = (newQuery: any) => {
  if (newQuery.qu) {
    router.push({
      path: '/Search',
      query: {
        qu: newQuery.qu
      }
    })
  } else if (newQuery.f) {
    router.push({
      path: '/Search',
      query: {
        ...route.query,
        f: newQuery.f
      }
    })
  }
}
</script>

<style lang="scss" scoped>
.sum {
  width: 100%;
  .com{
    position: sticky;
    top: 0;
    z-index: 100;
    .header {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    background-color: white;
  }
  .main {
    .el-menu {
      width: 100%;
      .el-menu-item {
        flex: 1;
      }
    }
  }
  }
  
}
</style>
