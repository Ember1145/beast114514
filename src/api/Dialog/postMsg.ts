import request from "@/utils/request"

export const getConversation = (sendId,targetId) => {
  return request({
    url: '/getConversation',
    method: 'get',
    params:{sendId,targetId}
  })
}
export const postMsg = (params) => {
    return request({
      url: '/send',
      method: 'post',
      data:{...params}
    })
  }
  export const getFullMsg = (conversationId,current) => {
    return request({
      url: `/messages/${conversationId}`,
      method: 'get',
      params:{current}
    })
  }
  export const getDialogList = (current) => {
    return request({
      url: '/messages',
      method: 'get',
      params:{current}
    })
  }
  export const clearCount = (conversationId) => {
    return request({
      url: '/messages/clear',
      method: 'patch',
      params:{conversationId}
    })
  }