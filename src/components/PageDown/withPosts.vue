<!--  -->
<template>
    <div class="sum">
        <div v-for="(section, index) in combinedComments" :key="index">
        <template v-if="isCommon(section)">
          <TwiDown v-for="tweet in section" :key="tweet.tweetId" :tweet="tweet"></TwiDown>
        </template>
        <template v-else>
          <ChainFull :tweetChain="section"></ChainFull>      
        </template>
      </div>
    </div>
</template>

<script setup lang="ts">
import TwiDown from '@/components/TwiDown.vue';
const isCommon = (section) => {
  return Array.isArray(section) && !(Array.isArray(section[0]));
};
</script>

<style lang="scss" scoped>

</style>