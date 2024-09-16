import request from '@/utils/request'
export const fileAddService = (data:FormData) => {
    return request({
      url: '/upload/photo',
      method: 'post',
      data: data,
    })
  }
  export const singleFileService = (data:FormData) => {
    return request({
      url: '/upload/single',
      method: 'post',
      data: data,
    })
  }
  export const twiAddService = (params: any) => {
    return request({
      url: '/save',
      method: 'post',
      data: { ...params }
    })
  }
  export const twiDetail = (emailCut,tweetId,current) => {
    // const requestBody = {
    //   parentId: params.parentId,
    //   current: current,
    // };
    return request({
      url: `/${emailCut}/status/${tweetId}`,
      method: 'get',
      params:{current} 
    });
  };
  export const handleLike = (params:any) => {
    return request({
      url: '/updateLike',
      method: 'post',
      data: { ...params }
    })
  }
  export const handleShare = (params:any) => {
    return request({
      url: '/updateShare',
      method: 'post',
      data: { ...params }
    })
  }
  export const queryStatus = (tweetId:any) => {
    return request({
      url: '/queryStatus',
      method: 'get',
      params: { tweetId }
    })
  }
  export const deleteTweet = (tweetId:any) => {
    return request({
      url: `/delete/${tweetId}`,
      method: 'delete',
    })
  }
  export const getDeliver = () => {
    return request({
      url: '/deliver',
      method: 'get',
    })
  }
  export const refreshDelivery = () => {
    return request({
      url: '/deliver/refresh',
      method: 'delete',
    })
  }

