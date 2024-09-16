<template>
  <div class="all" @click="handleOutsideClick">
    <input type="file" @change="selectFile" ref="fileInput" hidden />
    <div
      class="top"
      ref="editableDiv"
      contenteditable="true"
      @input="handleInput"
      :data-placeholder="showPlaceholder ? '' : '请输入内容'"
    ></div>
    <div class="gridp">
      <div class="grid" v-for="(file, index) in files.slice(0, 4)" :key="index">
        <div class="media" v-if="isImage(file)">
          <el-image :src="file.preview" alt="Image Preview" />
          <el-button class="fa-solid fa-xmark" @click="removeFile(index)" circle text></el-button>
        </div>
        <div class="media" v-if="isVideo(file)">
          <video :src="file.preview" controls />
          <el-button class="fa-solid fa-xmark" @click="removeFile(index)" circle text></el-button>
        </div>
      </div>
    </div>
    <div class="tag">
      <el-check-tag :checked="checked1" type="primary" @change="onChange1">
      Music
    </el-check-tag>
    <el-check-tag :checked="checked2" type="success" @change="onChange2">
      Picture
    </el-check-tag>
    <el-check-tag :checked="checked" type="success" @change="onChange">
      Life
    </el-check-tag>
  </div>
    <div class="foot">
      <el-button @click="triggerFileInput" circle :disabled="files.length >= 4" text>
        <i class="fa-solid fa-image" style="color: #1d9bf0"></i>
      </el-button>
      <el-button circle text @click="openEmojiPicker">
        <i class="fa-solid fa-face-smile" style="color: #1d9bf0"></i>
      </el-button>
      <el-button circle text>
        <i class="fa-solid fa-list" style="color: #1d9bf0"></i>
      </el-button>
      <el-button round color="black" @click="uploadFiles" size="large">发帖</el-button>
    </div>
  </div>
  <div class="emoji"><EmojiPicker :hide-search="true" :disable-skin-tones="true"
    :hide-group-names="true" v-show="emojiVision" @select="onSelectEmoji"/></div>
</template>

<script setup lang="ts">
import { ref, watchEffect, onUnmounted, computed, onMounted } from 'vue'
import { fileAddService, twiAddService } from '@/api/twi/twi'
import { TwiDetailStore } from '@/stores/TwiDetailStore'
import { ElMessage } from 'element-plus'
import EmojiPicker from 'vue3-emoji-picker'
import 'mm/vue3-emoji-picker/dist/style.css'
import { useUserShareAndMyStore } from '@/stores/UserPageDown/ShareAndMyStore'
import { myStore } from '@/stores/myStore'
const checked = ref(false)
const checked1 = ref(false)
const checked2 = ref(false)
const tag=ref([])
const onChange = (status: boolean) => {
  checked.value = status;
  const tagIndex = tag.value.indexOf("life");
  if (status === true) {
    // 如果状态为true且标签数组中没有"Life"，则添加它
    if (tagIndex === -1) {
      tag.value.push("life");
    }
  } else {
    // 如果状态为false且标签数组中存在"Life"，则移除它
    if (tagIndex !== -1) {
      tag.value.splice(tagIndex, 1);
    }
  }
};

const onChange1 = (status: boolean) => {
  checked1.value = status;
  const tagIndex = tag.value.indexOf("music");
  
  if (status === true) {
    // 如果状态为true且标签数组中没有"Life"，则添加它
    if (tagIndex === -1) {
      tag.value.push("music");
    }
  } else {
    // 如果状态为false且标签数组中存在"Life"，则移除它
    if (tagIndex !== -1) {
      tag.value.splice(tagIndex, 1);
    }
  }
};

const onChange2 = (status: boolean) => {
  checked2.value = status;
  const tagIndex = tag.value.indexOf("picture");
  
  if (status === true) {
    // 如果状态为true且标签数组中没有"Life"，则添加它
    if (tagIndex === -1) {
      tag.value.push("picture");
    }
  } else {
    // 如果状态为false且标签数组中存在"Life"，则移除它
    if (tagIndex !== -1) {
      tag.value.splice(tagIndex, 1);
    }
  }
};
const my = myStore()
const ShareAndMyStore = useUserShareAndMyStore()
const usetwiDetail = TwiDetailStore()
const emojiVision = ref(false)
const openEmojiPicker = () => {
  emojiVision.value = !emojiVision.value;
  console.group(emojiVision.value)
}
const handleOutsideClick = (event) => {
  const sumElement = document.querySelector('.sum .emoji');
  if (sumElement && !sumElement.contains(event.target) && emojiVision.value) {
    emojiVision.value = false;
  }
};
onMounted(() => {
  // 在文档上添加点击事件监听器
  document.addEventListener('click', handleOutsideClick);
});
onUnmounted(() => {
  // 在实例卸载时移除事件监听器
  document.removeEventListener('click', handleOutsideClick);
});

const onSelectEmoji = (emoji) => {
  console.log(emoji)
  Text.value += emoji.i;
  updateDivContent(); 
};

const updateDivContent = () => {
  if (editableDiv.value) {
    editableDiv.value.innerText = Text.value;
  }
}

const showPlaceholder = computed(() => {
  return Text.value || files.value.length > 0
})

const editableDiv = ref<HTMLElement | null>(null)
const Text = ref<string>('')
const handleInput = (e: Event) => {
  const target = e.target as HTMLElement | null // 更改为了 HTMLDivElement
  Text.value = target.innerText
}
interface FileWithPreview {
  name: string
  preview: string
  type: string
  size: number
  raw: File
}
const media = ref<string[]>([])
const fileInput = ref<HTMLInputElement | null>(null)
const files = ref<FileWithPreview[]>([])
const triggerFileInput = () => {
  fileInput.value?.click()
}
const uploadFiles = async () => {
  if (files.value.length === 0 && Text.value.trim() === '') {
    ElMessage.warning('不能提交空内容')
    return
  }
  if (files.value.length > 0) {
    const formData = new FormData()
    files.value.forEach((file) => {
      // 确保 raw 存在，并且是 File 对象
      if (file.raw && file.raw instanceof File) {
        formData.append('files', file.raw, file.name)
        console.log('当前文件', file.raw)
      }
    })
    const response = await fileAddService(formData)
    console.log(response)
    media.value = [...response.data]
  }
  const commentData = {
    parentId: null,
    content: Text.value,
    media: media.value,
    realParent:null,
    tag:tag.value
  }

  const response = await twiAddService(commentData)
  const local={
    parentId: null,
    content: Text.value,
    media: media.value,
    realParent:null,
    tag:tag.value,
    avatarUrl:my.avatarUrl,
    emailCut:my.emailCut,
    username:my.username,
    userId:my.userId,
    tweetId:response.data.tweetId,
    createdAt:response.data.createdAt
  }
  // usetwiDetail.pushItem(local)
  files.value = []
  Text.value = ''
  if (editableDiv.value) {
    editableDiv.value.innerText = '' // 这将清空内容可编辑的 div
  }
    const path=`/${my.emailCut}`
    ShareAndMyStore.pushItem(local,path)
  
  ElMessage.success('发布成功')
}

const selectFile = (event: Event) => {
  const inputElement = event.target as HTMLInputElement
  if (!inputElement.files || !inputElement.files.length) return

  const file = inputElement.files[0]
  const reader = new FileReader()
  reader.readAsDataURL(file)

  reader.onload = () => {
    const newFile: FileWithPreview = {
      name: file.name,
      preview: reader.result as string,
      type: file.type,
      size: file.size,
      raw: file
    }
    files.value.push(newFile)
  }
}

const removeFile = (index: number) => {
  files.value.splice(index, 1)
}

const isImage = (file: FileWithPreview) => {
  return file.type.startsWith('image/')
}

const isVideo = (file: FileWithPreview) => {
  return file.type.startsWith('video/')
}

// 当组件卸载时，清除所有 preview URLs
watchEffect(() => {
  onUnmounted(() => {
    files.value.forEach((file) => {
      if (file.preview.startsWith('blob:')) {
        URL.revokeObjectURL(file.preview)
      }
    })
    files.value = []
  })
})
</script>
<style scoped lang="scss">
.all {
  width: 100%;
  height: auto;
  border: 1px solid var(--el-border-color);
  border-top: none;
  background-color: white;
  .top {
    min-height: 4px; /* 给予足够的高度以便能够点击并编辑 */
    padding: 15px; /* 添加一些内边距 */
    outline: none;
    position: relative;
    white-space: pre-wrap;
    &::after {
      content: attr(data-placeholder); /* 使用属性值作为内容 */
      color: #aaa; /* placeholder 文本的颜色 */
      position: absolute; /* 使伪元素绝对定位来定位文本 */
      pointer-events: none; /* 确保点击占位符文本时不会干扰到 div 输入 */
      left: 20px; /* 根据需要调整定位 */
      top: 0; /* 根据需要调整定位 */
      bottom: 0;
      margin: auto;
      width: fit-content;
      height: fit-content;
    }
  }
  .gridp {
    width: 100%;
    min-height: 1px;
    max-height: 300px;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    grid-gap: 1px;
    .grid {
      .media {
        position: relative;
        width: 100%; // 确保填满父项grid
        height: 100%;
        .el-button {
          position: absolute;
          top: 10px;
          right: 10px;
          opacity: 0.1;
        }
      }
    }
  }

  .el-image,
  video {
    border-radius: 10px;
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.foot {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  height: 10vh;
  padding: 20px;
  .el-button {
    &:last-child {
      margin-left: auto;
    }
    margin-left: 20px;
  }
}
.emoji {
    position: fixed;
    z-index: 100;
  }
</style>

