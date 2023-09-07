package com.mist.cloud.core.result;

import com.mist.cloud.core.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/9/7 17:48
 * @Description:
 */
@Data
public class R<T> implements Serializable {
    private Integer code = Constants.DEFAULT_FAILED_CODE;
    private String msg = Constants.DEFAULT_FAILED_MSG;
    private T data;

    private static final Integer SUCCESS_CODE = 200;
    private static final Integer ERROR_CODE = 300;

    private static final String SUCCESS_MSG = "请求成功";
    private static final String ERROR_MSG = "请求成功";


    public R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R success(Integer code, String msg, Object data) {
        return new R<>(code, msg, data);
    }

    public static R success(String msg, Object data) {
        return new R<>(SUCCESS_CODE, msg, data);
    }

    public static R success(Object data) {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    public static R error() {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    public static R error(Integer code, String msg, Object data) {
        return new R<>(code, msg, data);
    }

    public static R error(String msg, Object data) {
        return new R<>(ERROR_CODE, msg, data);
    }

    public static R error(Object data) {
        return new R<>(ERROR_CODE, ERROR_MSG, data);
    }

    public static R error(String msg) {
        return new R<>(ERROR_CODE, msg, null);
    }

    public static R success() {
        return new R<>(SUCCESS_CODE, ERROR_MSG, null);
    }

}
