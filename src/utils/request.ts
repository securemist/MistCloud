import axios from "axios";
import constants from "@/utils/constants.ts";
import {getToken} from "@/utils/auth.ts";
import {history} from "@/utils/history.ts";

const request = axios.create({
  baseURL: import.meta.env.VITE_BASE_API_PATH,
  timeout: 5000,
})

request.interceptors.request.use(config => {
  config.headers[constants.DEFAULT_TOKEN_NAME] = getToken()
  return config
})

request.interceptors.response.use((response) => {
  // 文件下载
  if(response.data instanceof Blob){
    return response.data
  }

  // 其他的自定义失败返回码
  if (response.data.code !== 200) {
    throw new Error(response.data.msg)
  }

  return response.data
}, (error) => {
  let msg = ''
  const status = error.response.status

  switch (status) {
    case 401:
      msg = "token过期";
      history.push("/#/login")
      break;
    case 403:
      msg = '无权访问';
      break;
    case 404:
      msg = "请求地址错误";
      break;
    case 500:
      msg = "服务器出现问题";
      break;
    default:
      msg = "无网络";
  }

  return Promise.reject(msg);
})

export default request


