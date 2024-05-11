  <template>
    <div>
        <TwiDown v-for="tweet in userShareAndMyStore.CombineHistories[route.path]?.combinedTweet" :key="tweet.tweetId" :tweet="tweet"></TwiDown>
    </div>
</template>


<script setup lang="ts">
import TwiDown from '@/components/TwiDown.vue';
import { useUserShareAndMyStore } from '@/stores/UserPageDown/ShareAndMyStore';
import { useRoute } from 'vue-router';
const userShareAndMyStore=useUserShareAndMyStore()
const route=useRoute()
</script>
<style lang="scss" scoped>

</style>