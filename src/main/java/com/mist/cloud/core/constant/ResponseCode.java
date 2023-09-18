package com.mist.cloud.core.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {
    DEFAULT_SUCCESS(200, "请求成功"),

    DEFAULT_ERROR(100, "服务器异常"),
    LOGIN_FAILED(101, "登陆信息无效");

    private Integer code;
    private String msg;
}
