import request from '@/utils/request.ts';
import {FolderDetailResponse} from '@/api/file/type.ts';

enum API {
  CREATE_FOLDER = '/folder/create',
  FOLDER_DETAIL = '/folder/',
  FOLDER_TREE = '/folder/tree',

  FILE_RENAME = '/file/rename',
  FILE_COPY = '/file/copy',
  FILE_DELETE = '/file/delete',
  FILE_SEARCH = '/file/search'
}


export const getFolderTree = () => {
  return request({
    url: API.FOLDER_TREE,
    method: 'get',
  })
}

export const createFolder = (parentId: string, folderName: string) => {
  return request({
    url: API.CREATE_FOLDER,
    method: 'get',
    params: {
      parentId: parentId,
      folderName: folderName,
    }
  })
}

export const getSubFoldersAndFiles = (folderId: string) => {
  return request<any, FolderDetailResponse>({
    url: API.FOLDER_DETAIL + folderId,
    method: 'get',
  })
}


export const searchFile = (value: string) => {
  return request<any, FolderDetailResponse>({
    url: API.FILE_SEARCH,
    method: 'get',
    params: {
      value: value
    }
  })
}

export const renameFile = (id: string, name: string) => {
  return request({
    url: API.FILE_RENAME,
    method: 'get',
    params: {
      id,
      name
    }
  })
}

export const copyFile = (id: string, targetFolderId: string) => {
  return request({
    url: API.FILE_COPY,
    method: 'get',
    params: {
      id,
      targetFolderId
    }
  })
}

export const deleteFile = (id: string, realDelete: boolean) => {
  return request({
    url: API.FILE_DELETE,
    method: 'get',
    params: {
      id,
      realDelete
    }
  })
}
