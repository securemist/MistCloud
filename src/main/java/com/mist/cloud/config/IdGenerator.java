package com.mist.cloud.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 14:39
 * @Description:
 */
@Configuration
@Component
public class IdGenerator {

    public Long nextId() {
        return IdUtil.getSnowflake(1, 1).nextId();
    }
}
