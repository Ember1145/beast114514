<template>
  <div class="layout">
    <el-container>
      <el-header class="one">
        <el-menu mode="horizontal" :ellipsis="false" default-active="1" class="menu">
          <el-menu-item index="1">为您推荐</el-menu-item>
          <el-menu-item index="2">正在关注</el-menu-item>
        </el-menu>
        <TweetPublish></TweetPublish>
      </el-header>
      <el-main class="main">
        
      </el-main>
    </el-container>
  </div>
</template>
<script setup lang="ts">
import TweetPublish from '@/components/TweetPublish.vue';

</script>

<style lang="scss">
.layout {
  height: 100vh;
  .main{
    padding: 0;
  }

  
  .one {
    max-width: 50vw;
    padding: 0;
    width: 100%;
    height: 8vh;
    border: 1px solid var(--el-border-color);
    border-top: none;
    position: sticky;
    opacity: 0.8;
    top:0;
    z-index: 1000;
    .menu{

      .el-menu-item {
      width: 50%;

    }
    }
    
  }
}
</style>
