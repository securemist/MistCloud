package com.mist.cloud.common.exception.auth;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:24
 * @Description:
 */
public class RegisterException extends RuntimeException{
    public RegisterException(String msg) {
        super(msg);
    }
}
