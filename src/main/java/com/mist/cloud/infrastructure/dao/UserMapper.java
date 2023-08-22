package com.mist.cloud.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 19:29
 * @Description:
 */
@Mapper
public interface UserMapper {
    Long getTotalCapacity(Long userId);
}
