<template>
  <div class="header">
    <el-input
      v-model="qu"
      style="width: 240px"
      size="large"
      placeholder="Please Input"
      clearable
      :prefix-icon="Search"
      @keyup.enter="search"
    />
  </div>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { ref } from 'vue'
import router from '@/router'
const qu = ref()
const search = () => {
  router.push({ path: '/Search', query: { qu: qu.value } })
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 8vh;

  .el-input {
    border-radius: 20px;
    width: 80%;
    position: fixed;
  }
}
</style>
