import request from '@/utils/request.ts'
import constants from '@/utils/constants.ts'

export function mergeFile(data) {
  return request({
    url: '/upload/mergeFile',
    method: 'post',
    data: data
  })
}

export function sendFileInfo(data) {
  return request({
    url: '/upload/info',
    method: 'post',
    data: data
  })

}

export function cancelUpload(data) {
  return request({
    url: '/upload/cancel',
    method: 'get',
    params: data
  })
}

export function download(data) {
  return request({
    url: '/download',
    method: 'get',
    params: data,
    responseType: 'blob'
  })
}
