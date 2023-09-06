import CommonResponse from "@/api/type.ts";

export interface LoginForm {
  username: string,
  password: string
}

export interface TokenInfo {
  tokenName: string,
  tokenValue: string,
  tokenTimeOut: string,
}

export interface LoginResponse extends CommonResponse {
  data: {
    rootFolderId: string,
    tokenInfo: TokenInfo
  }
}
