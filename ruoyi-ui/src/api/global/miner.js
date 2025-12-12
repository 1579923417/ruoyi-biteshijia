import request from '@/utils/request'

export function listMinerFarm(query) {
  return request({ url: '/admin/global/miner/list', method: 'get', params: query })
}

export function getMinerFarm(id) {
  return request({ url: `/admin/global/miner/${id}`, method: 'get' })
}

export function addMinerFarm(data) {
  return request({ url: '/admin/global/miner', method: 'post', data })
}

export function updateMinerFarm(data) {
  return request({ url: '/admin/global/miner', method: 'put', data })
}

export function delMinerFarm(ids) {
  return request({ url: `/admin/global/miner/${ids}`, method: 'delete' })
}

