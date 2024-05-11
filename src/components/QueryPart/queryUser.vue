<template>
  <div class="summ" v-for="i in items" :key="i.userId">
    <div class="sum">
      <el-avatar :size="50" :src="i.avatarUrl"></el-avatar>
      <div class="name">
        <div class="username">{{ i.username }}</div>
        <div class="cut">@{{ i.emailCut }}</div>
      </div>
      <div class="but">
        <el-button v-if="useMy.emailCut !== i.emailCut" round color="black">关注</el-button>
      </div>
    </div>
    <div class="div">{{ i.introduction }}</div>
  </div>
</template>

<script setup lang="ts">
import { myStore } from '@/stores/myStore'
interface Item {
  userId: number
  username: string
  emailCut: string
  avatarUrl: string
  introduction: string
}

defineProps({
  items: Array<Item>
})

const useMy = myStore()
</script>

<style lang="scss" scoped>
.summ {
  cursor: pointer; // 设置鼠标在 .sum 上时为手型图标以表明可以点击

  &:hover {
    background-color: rgb(242, 245, 245); // 当鼠标悬停在 .sum 上时，改变背景色
  }
  .div{
    margin-left: 72px;
    font-size: 15px;
  }
  .sum {
    display: flex;
    justify-content: space-around;
    align-items: center;
    height: 8vh;

    .el-avatar {
      margin-left: 20px;
    }

    .name {
      margin-left: 10px;
      flex-grow: 1;
      .username {
        font-weight: 600;
      }
    }
    .el-button {
      margin-right: 20px; // 自动将所有的左边距推到按钮左边，从而将按钮推到最右边
    }
  }
}
</style>
