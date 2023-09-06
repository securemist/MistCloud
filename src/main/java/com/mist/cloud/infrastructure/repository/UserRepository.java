package com.mist.cloud.infrastructure.repository;

import com.mist.cloud.aggregate.user.mode.UserRegisterInfo;
import com.mist.cloud.aggregate.user.repository.IUserRepository;
import com.mist.cloud.common.config.IdGenerator;
import com.mist.cloud.common.constant.Constants;
import com.mist.cloud.infrastructure.DO.User;
import com.mist.cloud.infrastructure.dao.UserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 17:02
 * @Description:
 */
@Component
public class UserRepository implements IUserRepository {
    @Resource
    private UserMapper userMapper;

    @Override
    public User getUser(String username) {
        return userMapper.selectUserByName(username);
    }

    public User getUser(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public User addUser(UserRegisterInfo userRegisterInfo) {
        Long defaultUserCapacity = Constants.DEFAULT_USER_CAPACITY;

        Long rootFolderId = IdGenerator.fileId();
        User user = User.builder()
                .rootFolderId(rootFolderId)
                .email(userRegisterInfo.getEmail())
                .username(userRegisterInfo.getUsername())
                .password(userRegisterInfo.getPassword())
                .usedCapacity(0L)
                .totalCapacity(defaultUserCapacity)
                .build();

        Long userId = userMapper.insertUser(user);

        return getUser(userId);
    }
}
