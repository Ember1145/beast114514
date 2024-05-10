import request from '@/utils/request'
export const fileAddService = (data:FormData) => {
    return request({
      url: '/upload/photo',
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
  export const twiDetail = (params:any,current:number) => {
    const requestBody = {
      parentId: params.parentId,
      current: current,
    };
  
    return request({
      url: `/${params.emailCut}/status/${params.tweetId}`,
      method: 'post',
      data: requestBody
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
