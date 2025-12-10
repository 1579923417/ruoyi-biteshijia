import request from '@/utils/request'

export function listBrand(query) {
  return request({
    url: '/admin/miner/brand/list',
    method: 'get',
    params: query
  })
}

export function getBrand(id) {
  return request({
    url: `/admin/miner/brand/${id}`,
    method: 'get'
  })
}

export function addBrand(data) {
  return request({
    url: '/admin/miner/brand',
    method: 'post',
    data: data
  })
}

export function updateBrand(data) {
  return request({
    url: '/admin/miner/brand',
    method: 'put',
    data: data
  })
}

export function delBrand(ids) {
  return request({
    url: `/admin/miner/brand/${ids}`,
    method: 'delete'
  })
}
