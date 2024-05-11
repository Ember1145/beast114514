import axios, { AxiosInstance } from 'axios'
import router from '@/router'

const baseUrl = '/api'
const instance: AxiosInstance = axios.create({ baseURL: baseUrl })
import { myStore } from '@/stores/myStore'
import { ElMessage } from 'element-plus'

instance.interceptors.request.use(
  (request) => {
    const useMy = myStore()
    if (useMy.token) {
      request.headers.Authorization = `Bearer ${useMy.token}`;
  }
    return request
  },
  (err) => {
    //请求错误的回调
    Promise.reject(err)
  }
)

instance.interceptors.response.use(
  (response) => {
    // 判断业务状态码
    if (response.data.code === 1) {
      return response.data
    } else {
      // 如果业务状态码不为 0，则表示失败，直接返回响应数据

      // 如果业务状态码为 0，则表示成功，但是根据需要，你可能希望显示一些消息，比如后端返回的提示信息
      ElMessage.success(response.data.msg || '操作成功')
      // 异步操作的状态转换为失败，以便进入后续的 .catch 中进行错误处理
      return Promise.reject(response.data)
    }
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      alert('请先登录')
      router.push('/login')
    } else {
      // 显示服务异常信息
      ElMessage.success('服务yic')
    }
    return Promise.reject(error) // 异步的状态转化成失败的状态
  }
)

export default instance
