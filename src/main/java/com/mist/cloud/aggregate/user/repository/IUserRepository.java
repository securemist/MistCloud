package com.mist.cloud.aggregate.user.repository;

import com.mist.cloud.aggregate.user.mode.UserRegisterInfo;
import com.mist.cloud.infrastructure.entity.User;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:20
 * @Description:
 */
public interface IUserRepository {


    /**
     * 获取用户信息
     * @param username
     */
    User getUser(String username);


    /**
     * 注册用户
     *
     * @param userRegisterInfo
     * @return
     */
    User addUser(UserRegisterInfo userRegisterInfo);
}
