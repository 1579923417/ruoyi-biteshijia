import request from '@/utils/request'

export function listAppUser(query) {
  return request({
    url: '/admin/app/user/list',
    method: 'get',
    params: query
  })
}

export function getAppUser(id) {
  return request({
    url: `/admin/app/user/${id}`,
    method: 'get'
  })
}

export function addAppUser(data) {
  return request({
    url: '/admin/app/user',
    method: 'post',
    data: data
  })
}

export function updateAppUser(data) {
  return request({
    url: '/admin/app/user',
    method: 'put',
    data: data
  })
}

export function delAppUser(id) {
  return request({
    url: `/admin/app/user/${id}`,
    method: 'delete'
  })
}

export function resetAppUserPwd(id) {
  return request({
    url: `/admin/app/user/resetPwd/${id}`,
    method: 'put'
  })
}

export function listUserMiner(query) {
  return request({
    url: '/admin/app/user/miner/list',
    method: 'get',
    params: query
  })
}

export function updateUserMiner(data) {
  return request({
    url: '/admin/app/user/miner',
    method: 'put',
    data: data
  })
}

export function unbindMiner(id) {
  return request({
    url: '/admin/app/user/miner/unbind',
    method: 'put',
    params: { id }
  })
}

export function bindMiner(id, userId) {
  return request({
    url: '/admin/app/user/miner/bind',
    method: 'put',
    params: { id, userId }
  })
}

export function changeMinerStatus(id, status) {
  return request({
    url: '/admin/app/user/miner/changeStatus',
    method: 'put',
    params: { id, status }
  })
}

export function addUserMiner(data) {
  return request({
    url: '/admin/app/user/miner',
    method: 'post',
    data: data
  })
}

export function delUserMiner(ids) {
  return request({
    url: `/admin/app/user/miner/${ids}`,
    method: 'delete'
  })
}
