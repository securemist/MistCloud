package com.mist.cloud.core.utils;

import cn.dev33.satoken.stp.StpUtil;

/**
 * @Author: securemist
 * @Datetime: 2023/8/18 10:28
 * @Description:
 */
public class Session {
    public static Long getLoginId(){
        return Long.valueOf(StpUtil.getLoginId().toString());
    }
}
