<template>
  <button
    class="round-btn"
    :style="{
      'background-color': buttonHover
    }">  <slot></slot>
  </button>
</template>

<script setup lang="ts">
defineProps({
buttonHover:String
})

</script>
<style lang="scss">
.round-btn {
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  background-color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative; // 为 absolute 定位的 icon 做准备
  transition: background-color 0.2s; // 平滑背
}


</style>
