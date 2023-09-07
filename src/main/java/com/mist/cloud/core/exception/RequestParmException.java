package com.mist.cloud.common.exception;

import com.mist.cloud.common.constant.Constants;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 14:03
 * @Description:
 */
public class RequestParmException extends RuntimeException{

    public RequestParmException() {
        super(Constants.Response.PARAMS_ERREO.getMsg());
    }

}
