package com.mist.cloud.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 14:39
 * @Description:
 */
public class IdGenerator {
    public static Long fileId() {
        return IdUtil.getSnowflake(1, 1).nextId();
    }

    public static String uploadTaskId() {
        return UUID.fastUUID().toString(true);
    }
}
