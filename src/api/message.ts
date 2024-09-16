import request from "@/utils/request"

export const getLikeMsg = (current) => {
    return request({
      url: '/message',
      method: 'get',
      params:{current}
    })
  }
  export const clearHomeCount= () => {
    return request({
      url: '/message/clear',
      method: 'patch',
    })
  }
  export const clearDialogCount= () => {
    return request({
      url: '/message/clearDialogCount',
      method: 'patch',
    })
  }
  export const getHomeUnreadCount = () => {
    return request({
      url: '/homeUnreadCount',
      method: 'get',
    })
  }