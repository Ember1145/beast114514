<template>
    <el-row>
      <el-col :span="6">
        <leftBar />
      </el-col>

      <el-col :span="11">
     <router-view name="center"></router-view>
      </el-col>

      <el-col :span="7">
        <router-view name="right"></router-view>
      </el-col>
    </el-row>
</template>

<script setup lang="ts">
import leftBar from '@/components/leftBar.vue'
</script>

<style lang="scss" scoped></style>
