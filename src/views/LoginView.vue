<template>
  <div class="container">
    <div class="left">
      <el-icon><Edit /></el-icon>
    </div>
    <div class="login-container">
      <div class="login-box">
        <h1 class="title">正发生</h1>
        <img src="assets/114.jpg" alt="Logo" class="logo" />
        <el-form :model="formData" ref="formRef" class="login-form" :rules="rules">
          <el-form-item class="form-item">
            <el-input
              v-model="formData.email"
              placeholder="邮箱"
              prop="email"
              class="input-with-prefix"
            >
              <template #prefix>
                <el-icon><edit /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="form-item">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="密码"
              show-password
              class="input-with-prefix"
              prop="password"
            >
            </el-input>
          </el-form-item>
          <el-form-item class="form-item">
            <el-button type="primary" @click="handle" :loading="loading" class="login-button">
              创建账号
            </el-button>
          </el-form-item>
        </el-form>
        <p class="register-link">
          已经有账号? <el-button @click="handleLogin(formData)">登录</el-button>
        </p>
      </div>
    </div>
  </div>

  <el-dialog v-model="visible" align-center="true">
    <template #header><div>注册账号</div></template>
    <el-form
      :model="registryForm"
      ref="registryRef"
      :rules="rules"
      label-position="right"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="username" label="展示名">
            <el-input v-model="registryForm.username"></el-input>
          </el-form-item>
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="registryForm.email"></el-input>
          </el-form-item>
          <el-form-item prop="emailCut" label="用户唯一标识">
            <el-input v-model="registryForm.emailCut"></el-input>
          </el-form-item>
          <el-form-item prop="pass" label="密码">
            <el-input v-model="registryForm.pass" showpassword></el-input>
          </el-form-item>
          <el-form-item prop="password" label="确认密码">
            <el-input v-model="registryForm.password"> </el-input>
          </el-form-item>
          <el-button @click="handleRegistry(registryForm)">注册</el-button>
        </el-col>
      </el-row>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { userAddService, userLoginService } from '@/api/user/user'
import { useRouter } from 'vue-router'
import { AxiosError } from 'axios'
import { myStore } from '@/stores/myStore'
const formRef = ref<FormInstance>()
const visible = ref(false)
const registryRef = ref<FormInstance>()
const router = useRouter()
const useMy=myStore()
const handleLogin = async (formData: any) => {
  const valid = await formRef.value?.validate() // 这里依然需要使用表单的实例来执行校验
  if (!valid) {
    ElMessage.error('验证未通过')
    return
  }
  // 如果前端验证通过，再发送异步请求
  const response = await userLoginService(formData)
  useMy.setToken(response.data.token)
  useMy.setEmailCut(response.data.emailCut)
  useMy.setUserId(response.data.userId)
  useMy.setAvatarUrl(response.data.avatarUrl)
  useMy.setUsername(response.data.username)
  ElMessage.success('登录成功')
  router.push('/home')
}

const handle = () => {
  visible.value = !visible.value
}

const formData = reactive({
  email: '',
  password: '',
  username: ''
})

const loading = ref(false)

const handleRegistry = async (formData: any) => {
  try {
    const valid = await registryRef.value?.validate() // 这里依然需要使用表单的实例来执行校验
    if (!valid) {
      console.log('验证未通过')
      return
    }
    const response = await userAddService(formData)
    
    console.log('后端返回的数据：', response)

  } catch (error: unknown) {
    if (
      error instanceof AxiosError &&
      error.response &&
      error.response.data &&
      'msg' in error.response.data
    ) {
      // 使用后端提供的错误信息
      const errorMessage = (error.response.data as { msg: string }).msg
      ElMessage.error(errorMessage)
    } else {
      // 如果后端没有提供响应，则使用一般性的错误消息
      ElMessage.error('网络错误或服务器无响应')
    }
    console.error('发生错误：', error)
  }
  visible.value = false
}

const registryForm = reactive({
  username: '',
  email: '',
  emailCut: '',
  pass: '',
  password: ''
})

const validatePass = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/.test(value)) {
    callback(new Error('至少包括一个数字字母且大于6位'))
  } else {
    if (registryForm.password !== '') {
      if (!registryRef.value) return
      registryRef.value.validateField('pass', () => null)
    }
    callback()
  }
}
const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('Please input the password again'))
  } else if (value !== registryForm.pass) {
    callback(new Error("Two inputs don't match!"))
  } else {
    callback()
  }
}
const validateEmailCut = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入唯一标识'))
  } else if (!/^(?!_)[a-zA-Z0-9_]{4,10}(?<!_)$/.test(value)) {
    callback(new Error('请输入4到10位的字母数字或者下划线'))
  }
  else{callback()}
}
const rules = reactive<FormRules<typeof registryForm>>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  emailCut: [
    { required: true, message: '请输入唯一标识', trigger: 'blur' },
    { validator: validateEmailCut, trigger: 'blur' }
  ],
  pass: [{ validator: validatePass, trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [{ validator: validatePass2, trigger: 'blur' }]
})
</script>

<style lang="scss" scoped></style>

<style lang="scss">
@import 'mm/element-plus/theme-chalk/src/index.scss';

$primary-color: #1890ff;
$text-color: #333;
$border-color: #dcdfe6;
.container {
  display: flex;
  background-color: #f0f2f5;
  .left {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    .el-icon {
      font-size: 50vh;
    } /* 占据左半屏幕一半的宽度 */
  }
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
  margin-right: 100px;
}

.login-box {
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: $text-color;
  margin-bottom: 20px;
}

.logo {
  width: 100px;
  height: auto;
  margin-bottom: 30px;
  border-radius: 100px;
}

.login-form {
  width: 300px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
}
</style>
@/api/user/user
