package com.mist.cloud.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 15:00
 * @Description:
 */
public class Constants {

    public static final Long DEFAULT_USERID = 1L;
    // 用户的默认云盘容量20G
    public static final Long DEFAULT_USER_CAPACITY = 20 * 1024 * 1024 * 1024L;

    // 通用请求返回数据
    public static final int DEFAULT_SUCCESS_CODE = 200;
    public static final int DEFAULT_FAILED_CODE = 300;
    public static final int DEFAULT_AUTH_CODE = 400;
    public static final String DEFAULT_SUCCESS_MSG = "请求成功";
    public static final String DEFAULT_FAILED_MSG = "服务器异常";


    @Getter
    @AllArgsConstructor
    public enum Response {
        DEFAULT_SUCCESS_MSG("请求成功"),
        FILE_UPLOAD_SUCCESS("文件上传成功"),

        PARAMS_ERREO("请检查参数是否正确"),
        FILE_UPLOAD_FAILED("文件上传失败"),
        FILE_EMPTY("上传的文件为空"),
        FOLDER_DUPLICATE_NAME("文件夹重名"),

        LOGIN_ERROR("未登录");
        String msg;
    }


    @Getter
    @AllArgsConstructor
    public enum FileType {
        DOCUMENT("文档"),
        IMG("图片"),
        VIDEO("视频"),
        BINARY("二进制文件");

        String type;
    }
}
