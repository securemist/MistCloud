package com.mist.cloud.core.exception;

/**
 * @Author: securemist
 * @Datetime: 2023/9/22 17:33
 * @Description:
 */
public class ShareInvalidException extends RuntimeException{
    public ShareInvalidException(String message) {
        super(message);
    }
}
