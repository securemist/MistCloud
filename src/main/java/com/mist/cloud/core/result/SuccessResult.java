package com.mist.cloud.core.result;

import com.mist.cloud.core.constant.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 22:44
 * @Description:
 */
@Setter
@Getter
public class SuccessResult implements Result{
    private Integer code = Constants.DEFAULT_SUCCESS_CODE;
    private String msg = Constants.DEFAULT_SUCCESS_MSG;
    private Object data;

    public SuccessResult(String msg, Object data) {
        this.msg = msg;
        this.data = data;
    }

    public SuccessResult(String msg) {
        this.msg = msg;
    }
    public SuccessResult() {
        this.msg = Constants.DEFAULT_SUCCESS_MSG;
    }


    public SuccessResult(Object data) {
        this.data = data;
    }


}
