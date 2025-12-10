import request from '@/utils/request'

export function listAppMenu(query) {
  return request({
    url: '/admin/app/menu/list',
    method: 'get',
    params: query
  })
}

export function getAppMenu(id) {
  return request({
    url: `/admin/app/menu/${id}`,
    method: 'get'
  })
}

export function addAppMenu(data) {
  return request({
    url: '/admin/app/menu',
    method: 'post',
    data: data
  })
}

export function updateAppMenu(data) {
  return request({
    url: '/admin/app/menu',
    method: 'put',
    data: data
  })
}

export function delAppMenu(ids) {
  return request({
    url: `/admin/app/menu/${ids}`,
    method: 'delete'
  })
}

export function changeAppMenuStatus(id, status) {
  return request({
    url: '/admin/app/menu/changeStatus',
    method: 'put',
    params: { id, status }
  })
}

export function listRootAppMenu(query) {
  return request({
    url: '/admin/app/menu/roots',
    method: 'get',
    params: query
  })
}

export function listMenuTypes() {
  return request({
    url: '/admin/app/menu/types',
    method: 'get'
  })
}
