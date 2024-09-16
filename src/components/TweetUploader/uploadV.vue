<script setup lang="ts">
import { ref } from 'vue'

import { ElImage, ElMessage, UploadFile, UploadProps } from 'element-plus'
import { fileAddService, twiAddService } from '@/api/twi/twi'
const fileList = ref<UploadFile[]>([])
const media = ref<string[]>([])
const textData = ref<string>('')


// 当文件列表发生改变时（文件被添加或删除），更新你的 fileList
const onChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  fileList.value = [...uploadFiles]
}

// 当文件被删除时，从你的 fileList 中移除
// 注意这里只有一个参数了
const handleRemove = (file: UploadFile) => {
  const index = fileList.value.findIndex(item => item.uid === file.uid);
  // 如果找到了，就从 fileList 中移除
  if (index !== -1) {
    fileList.value.splice(index, 1);
  }
}

// 提交所有文件和文本数据
const submitAll = async () => {
  try {
    if(fileList.value.length>0){
      console.log('当前 fileList 包含的文件: ', fileList.value)
    const formData = new FormData()
    // 先上传所有文件
    const uploads = fileList.value.map(async (file) => {
      if (file.raw) {
        formData.append('files', file.raw)
      }
    })
    const response = await fileAddService(formData)
    // 保存上传文件的URL
    console.log(response)
    media.value = [...response.data]

    // 等待所有文件上传完成
    await Promise.all(uploads)
    ElMessage.warning('上传' + media.value)
    }
    
    // 创建一个包含上传文件URL和文本数据的JSON对象
    const data = {
      media: media.value,
      content: textData.value
    }
    if (fileList.value.length > 0 || textData.value.trim() !== "") {
    twiAddService(data)
  } else {
    ElMessage.warning('请至少提供一张图片或一些文本');
    return
  }

    ElMessage.success('表单提交成功')
    clearTweet()
    
  } catch (error) {
    // 如果出现错误，这里处理
    console.error('提交过程中出现错误：', error)
    ElMessage.error('表单提交失败')
  }
}
const clearTweet=()=>{
  fileList.value=[],
  textData.value=''
}
const handleDownload = (file: UploadFile) => {
  console.log(file)
}
const handlePictureCardPreview = (file: UploadFile) => {
  dialogImageUrl.value = file.url!
  dialogVisible.value = true
}

const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const disabled = ref(false)
const uploadRef = ref()



defineExpose({clearTweet})
</script>
<template>
  <el-upload
    :limit="4"
    ref="uploadRef"
    list-type="picture-card"
    :auto-upload="false"
    :on-change="onChange"
    :file-list="fileList"
  >
    <el-icon><Plus /></el-icon>

    <template #file="{ file }">
      <div>
        <el-image class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
        <span class="el-upload-list__item-actions">
          <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
            <el-icon><zoom-in /></el-icon>
          </span>
          <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleDownload(file)">
            <el-icon><Download /></el-icon>
          </span>
          <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemove(file)">
            <el-icon><Delete /></el-icon>
          </span>
        </span>
      </div>
    </template>
  </el-upload>
  <el-input v-model="textData"></el-input>
  <el-button class="ml-3" type="success" @click="submitAll"> upload to server </el-button>
  <el-dialog v-model="dialogVisible">
    <img w-full :src="dialogImageUrl" alt="Preview Image" />
  </el-dialog>
</template>
