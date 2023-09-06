import request from "@/utils/request.ts";
import type {LoginForm, LoginResponse} from "@/api/user/type.ts";


enum API {
    LOGIN_URL = "/user/login",
    LOGOUT_URL = "/user/logout"
}

export const login = (data: LoginForm) => {
    return request.post<any, LoginResponse>(API.LOGIN_URL, data)
}

export const logout = () => {
    return request.get(API.LOGOUT_URL)
}
