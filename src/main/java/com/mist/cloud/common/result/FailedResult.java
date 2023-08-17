package com.mist.cloud.common.result;

import com.mist.cloud.common.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 22:44
 * @Description:
 */
@Getter
@Setter
public class FailedResult implements Result{
    private Integer code  = Constants.DEFAULT_FAILED_CODE;
    private String msg = Constants.DEFAULT_FAILED_MSG;
    private Object data;

    public FailedResult(String msg, Object data) {
        this.msg = msg;
        this.data = data;
    }

    public FailedResult(String msg) {
        this.msg = msg;
    }

    public FailedResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public FailedResult(Object data) {
        this.data = data;
    }


}
