import request from '@/utils/request'
export const userAddService = (params: any) => {
  return request({
    url: '/register',
    method: 'post',
    data: { ...params }
  })
}
export const userLoginService = (params: any) => {
  return request({
    url: '/login',
    method: 'post',
    data: { ...params }
  })
}
export const singleService = (blobUrl:any) => {
  return new Promise((resolve, reject) => {
    fetch(blobUrl)
      .then((res) => res.blob())
      .then((blob) => {
        const single = new FormData();
        single.append('file', blob);

        return request({
          url: '/upload/single',
          method: 'post',
          data: single,
        });
      })
      .then((response) => resolve(response))
      .catch((error) => reject(error));
  });
};
export const userUpdateService = (params: any) => {
  return request({
    url: '/update',
    method: 'post',
    data: { ...params }
  })
}
export const getUserPage = (emailCut:any) => {
  return request({
    url: `/${emailCut}`,
    method: 'get',
  })
}
export const followSev = (userId:any) => {
  return request({
    url:'/follow',
    method: 'post',
    data: {userId}
  })
}
export const userLike = (emailCut,current) => {
  return request({
    url: `/${emailCut}/likes`,
    method: 'get',
    params:{current}
  })
}
