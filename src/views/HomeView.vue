<template>
  <el-row>
    <el-col :span="6">
      <leftBar />
    </el-col>
    <router-view v-slot="{ Component }">
      <keep-alive :include="['TwiVue']">
        <component :is="Component" />
      </keep-alive>
    </router-view>
  </el-row>
</template>

<script setup lang="ts">
import leftBar from '@/components/MsgBar/leftBar.vue'
import { onMounted, onUnmounted } from 'vue'
import { useWebSocket } from '@/utils/webSocket'
import { myStore } from '@/stores/myStore'
const my = myStore()
const myId = my.userId
const { connect, disconnect } = useWebSocket(myId)
onMounted(() => {
  connect()
})
onUnmounted(() => {
  disconnect()
})
</script>

<style lang="scss" scoped></style>
