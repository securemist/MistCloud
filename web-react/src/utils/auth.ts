import Cookies from 'js-cookie'
import constants from "@/utils/constants";
import type {TokenInfo} from "@/api/user/type";

const tokenKey = constants.DEFAULT_TOKEN_NAME

export function getToken() :string{
  return Cookies.get(tokenKey)
}

export function saveToken(tokenInfo: TokenInfo) {
  return Cookies.set(tokenInfo.tokenName, tokenInfo.tokenValue, {
    expires: tokenInfo.tokenTimeOut,
    secure: true,
    sameSite: 'None'
  })
}

export function removeToken(){
  return Cookies.remove(tokenKey)
}


