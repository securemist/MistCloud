package com.mist.cloud.common.exception.file;

/**
 * @Author: securemist
 * @Datetime: 2023/7/23 13:53
 * @Description:
 */
public  class FileException extends Exception{
    private String msg;

    public FileException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
