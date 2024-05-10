import request from '@/utils/request'
export const userSearch = (params:any) => {
    return request({
      url: '/Search',
      method: 'get',
      params,
    })
  }