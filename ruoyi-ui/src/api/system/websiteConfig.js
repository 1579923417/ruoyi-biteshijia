import request from '@/utils/request'

export function listWebsiteConfig(query) {
  return request({
    url: '/admin/system/websiteConfig/list',
    method: 'get',
    params: query
  })
}

export function getWebsiteConfig(id) {
  return request({
    url: `/admin/system/websiteConfig/${id}`,
    method: 'get'
  })
}

export function addWebsiteConfig(data) {
  return request({
    url: '/admin/system/websiteConfig',
    method: 'post',
    data: data
  })
}

export function updateWebsiteConfig(data) {
  return request({
    url: '/admin/system/websiteConfig',
    method: 'put',
    data: data
  })
}

export function delWebsiteConfig(ids) {
  return request({
    url: `/admin/system/websiteConfig/${ids}`,
    method: 'delete'
  })
}

export function listWebsiteConfigGroups() {
  return request({
    url: '/admin/system/websiteConfig/groups',
    method: 'get'
  })
}

