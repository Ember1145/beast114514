<template>
  <div class="but">
    <!-- <el-button class="uploadBut" @click="clickUploadButton" circle :icon="CameraFilled" text> </el-button> -->
    <input id="fileInput" type="file" @change="selectFile" ref="uploadInput" />
    <VuePictureCropper v-if="isShowModal" :img="pic" />
  </div>
</template>

<script setup lang="ts">
import { ref} from 'vue'
import VuePictureCropper, { cropper } from 'vue-picture-cropper'
const isShowModal = ref<boolean>(false)
const uploadInput = ref<HTMLInputElement | null>(null)
const pic = ref<string>('')
const blobURL = ref()

const clickUploadButton = () => {
  if (uploadInput.value !== null) {
    uploadInput.value.click()
  }
}
const selectFile = (e: Event) => {
  // Reset last selection and results
  pic.value = ''
  blobURL.value = ''

  // Get selected files
  const { files } = e.target as HTMLInputElement
  if (!files || !files.length) return

  // Convert to dataURL and pass to the cropper component
  const file = files[0]
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    // Update the picture source of the `img` prop
    pic.value = String(reader.result)
    isShowModal.value = true
    if (!uploadInput.value) return
    uploadInput.value.value = ''
  }
}

const getResult = async () => {
  if (!cropper) return
  const blob: Blob | null = await cropper.getBlob()
  if (!blob) return
   blobURL.value = URL.createObjectURL(blob)
  isShowModal.value = false
  return blobURL.value
}
defineExpose({getResult,clickUploadButton,selectFile})
</script>
<style scoped lang="scss">
#fileInput {
  visibility: hidden;
  position: absolute;
  width: 0;
  height: 0;
}
</style>
