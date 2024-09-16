<template>
  
    <el-col :span="11">
      <!-- <KeepAlive :include="['TwiVue']"><router-view name="center"></router-view></KeepAlive> -->
      <router-view name="center">
       
      </router-view>
    </el-col>

    <el-col :span="7">
      <router-view name="right"></router-view>
    </el-col>
 
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped></style>
