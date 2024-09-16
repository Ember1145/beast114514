<template>
    <el-col :span="6" class="border-right">
      <DialogBar></DialogBar>
    </el-col>

    <el-col :span="12">
      <router-view></router-view>
    </el-col>

</template>

<script setup lang="ts">
import DialogBar from '@/components/DialogPart/DialogBar.vue'

</script>

<style lang="scss" scoped>

/* 为左边和中间的el-col添加边框 */
.border-right {
  border-right: 1px solid var(--el-border-color); /* 或其他您喜欢的颜色 */
  height: 100%; /* 确保它们填充父容器, 这里父容器是.full-height-border */
}
</style>
