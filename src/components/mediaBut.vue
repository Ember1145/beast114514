<template>
  <div class="sum">
    <input type="file" @change="selectFile" ref="fileInput" hidden />
    <div class="top" ref="editableDiv" contenteditable="true" 
    @input="handleInput" :data-placeholder="showPlaceholder ? '' : '请输入内容'"></div>
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
    <div class="foot">
      <el-button
        class="fa-solid fa-image"
        @click="triggerFileInput"
        circle
        :disabled="files.length >= 4"
      ></el-button>
      <el-button class="fa-solid fa-face-smile" circle></el-button>
      <el-button class="fa-solid fa-list" circle></el-button>
      <el-button round color="black" @click="uploadFiles">回复</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect, onUnmounted, computed } from 'vue'
import { fileAddService, twiAddService } from '@/api/twi/twi'
import { TwiDetailStore } from '@/stores/TwiDetailStore'
import { ElMessage } from 'element-plus';
const usetwiDetail = TwiDetailStore()
const props = defineProps({
  tweetId: String
})
const showPlaceholder = computed(() => {
  return Text.value||files.value.length>0
});
const editableDiv = ref<HTMLElement | null>(null)
const Text = ref<string>('')
const handleInput = (e: Event) => {
  const target = e.target as HTMLElement | null // 更改为了 HTMLDivElement
  Text.value =  target.innerText
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
  if (files.value.length===0 && Text.value.trim() === '') {
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
    parentId: props.tweetId,
    content: Text.value,
    media: media.value
  }

  const newComment = await twiAddService(commentData)
  usetwiDetail.pushItem(newComment.data)
  files.value = []
  Text.value = ''
  if (editableDiv.value) {
      editableDiv.value.innerText = ''; // 这将清空内容可编辑的 div
    }
  ElMessage.success('评论成功')
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
.sum {
  width: 100%;
  height: auto;
  border: 1px solid var(--el-border-color);
  border-top: none;
  margin-top: 1px;
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
</style>
