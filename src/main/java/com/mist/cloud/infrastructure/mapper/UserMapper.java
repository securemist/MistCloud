package com.mist.cloud.infrastructure.mapper;

import com.mist.cloud.infrastructure.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 19:29
 * @Description:
 */
@Mapper
public interface UserMapper {
    Long getTotalCapacity(Long userId);

    User selectUserByName(String username);

    Long insertUser(User user);

    User selectUserById(Long userId);
}
