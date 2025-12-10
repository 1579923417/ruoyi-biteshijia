import request from '@/utils/request'

export function listBanner(query) {
  return request({
    url: '/admin/system/banner/list',
    method: 'get',
    params: query
  })
}

export function getBanner(bannerId) {
  return request({
    url: `/admin/system/banner/${bannerId}`,
    method: 'get'
  })
}

export function addBanner(data) {
  return request({
    url: '/admin/system/banner',
    method: 'post',
    data: data
  })
}

export function updateBanner(data) {
  return request({
    url: '/admin/system/banner',
    method: 'put',
    data: data
  })
}

export function delBanner(bannerId) {
  return request({
    url: `/admin/system/banner/${bannerId}`,
    method: 'delete'
  })
}

export function changeStatus(id, status) {
  return request({
    url: '/admin/system/banner/changeStatus',
    method: 'put',
    params: { id, status }
  })
}


