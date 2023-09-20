package com.mist.cloud.core.result;

import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.core.constant.ResponseCode;
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
    private Integer code;
    private String msg ;
    private T data;

    private static final Integer SUCCESS_CODE = ResponseCode.DEFAULT_SUCCESS.getCode();
    private static final Integer ERROR_CODE = ResponseCode.DEFAULT_ERROR.getCode();

    private static final String SUCCESS_MSG = ResponseCode.DEFAULT_SUCCESS.getMsg();
    private static final String ERROR_MSG = ResponseCode.DEFAULT_ERROR.getMsg();


    private R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R success(ResponseCode responseCode, Object data) {
        return new R<>(responseCode.getCode(), responseCode.getMsg(), data);
    }

    public static R success(String msg, Object data) {
        return new R<>(SUCCESS_CODE, msg, data);
    }

    public static R success(Object data) {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }


    public static R success() {
        return new R<>(SUCCESS_CODE, ERROR_MSG, null);
    }

    public static R error() {
        return new R<>(ERROR_CODE, ERROR_MSG, null);
    }

    public static R error(ResponseCode responseCode, Object data) {
        return new R<>(responseCode.getCode(), responseCode.getMsg(), data);
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


}
