const file_suffixs = [
  '7z',
  'avi',
  'bmp',
  'cpp',
  'css',
  'dmg',
  'doc',
  'docx',
  'exe',
  'html',
  'java',
  'jpg',
  'js',
  'json',
  'md',
  'mp3',
  'mp4',
  'pdf',
  'png',
  'ppt',
  'profile',
  'rar',
  'txt',
  'xlsx',
  'xml',
  'zip',
  'folder',
]

const file_suffixs_icon_set = new Set<String>()
file_suffixs.forEach(str => {
  file_suffixs_icon_set.add(str)
})

export default {

  BASE_API_PATH: import.meta.env.VITE_BASE_API_PATH,

  DEFAULT_TOKEN_NAME: 'satoken',

  path: {
    FILE_UPLOAD: '/file/upload'
  },

  DEFAULT_SUCCESS_CODE: 200,
  DEFAULT_FAILED_CODE: 200,

  // 已经拥有logo 的文件后缀名
  FILE_SUFFIS_ICON_SET: file_suffixs_icon_set,

  // 上传文件大小限制
  MAX_UPLOAD_FILE_SIZE: {
    limit: 20 * 1024 * 1024 * 1024,
    msg: '文件大小不得超过 20GB'
  },

  // 上传文件夹时文件数量限制
  MAX_FOLDER_UPLOAD_FILE_NUM: {
    limit: 100,
    msg: '文件数量不能超过 100'
  },
}
