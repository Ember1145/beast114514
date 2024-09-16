<template>
  <div class="real-header">
    <div class="header">
      <span class="word">通知</span>
      <span class="but"><el-button circle class="fa-solid fa-gear" text></el-button></span>
    </div>
    <div class="tab">
      <el-tabs v-model="activeName" class="demo-tabs" :stretch="true" @tab-click="handleClick">
        <el-tab-pane label="消息" name="1" ></el-tab-pane>
        <el-tab-pane label="Config" name="2"></el-tab-pane>
        <el-tab-pane label="回复" name="3"></el-tab-pane>
      </el-tabs>
    </div>
  </div>
  <div class="foot"><RouterView></RouterView></div>
</template>

<script setup lang="ts">
import { useWebSocket } from '@/utils/webSocket'
import { myStore } from '@/stores/myStore'
import { useWebSocketStore } from '@/stores/useWebSocketStore'
import router from '@/router'
import { storeToRefs } from 'pinia'
import { ref } from 'vue'
import { TabsPaneContext } from 'element-plus'
const my = myStore()
const myId = my.userId
useWebSocket(myId)

const webSocketStore = useWebSocketStore()
const { savedMsg } = storeToRefs(webSocketStore)
const activeName = ref('first')
const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab)
  if (tab.props.name === '1') {
    router.push({ name: 'main' })
  } else if (tab.props.name === '3') {
    router.push({ name: 'ReplyBar' })
  }
}
</script>
<style lang="scss" scoped>
.real-header {
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 2000;
  position: sticky;
  top: 0;
  height: 16vh;
  border: 1px solid var(--el-border-color);
  border-bottom: none;
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 8vh;
    padding: 10px;
    .word {
      font-weight: 600;
      font-size: 20px;
    }
  }
  .tab {
    border-top: none;
    border-bottom: none;
    margin-top: 20px;
  }
}
</style>
