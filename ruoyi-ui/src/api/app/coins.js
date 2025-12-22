import request from '@/utils/request'

export function getCoinsTop100() {
  return request({
    url: '/admin/app/coins/top100',
    method: 'post'
  })
}

export function getMiners() {
  return request({
    url: '/admin/app/coins/miners',
    method: 'post'
  })
}

