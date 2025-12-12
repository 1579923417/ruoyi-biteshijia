import request from '@/utils/request'

export function listGlobalPower(query) {
  return request({ url: '/admin/global/power/list', method: 'get', params: query })
}

export function getGlobalPower(id) {
  return request({ url: `/admin/global/power/${id}`, method: 'get' })
}

export function addGlobalPower(data) {
  return request({ url: '/admin/global/power', method: 'post', data })
}

export function updateGlobalPower(data) {
  return request({ url: '/admin/global/power', method: 'put', data })
}

export function delGlobalPower(ids) {
  return request({ url: `/admin/global/power/${ids}`, method: 'delete' })
}

