package com.mist.cloud.infrastructure.repository;

import com.mist.cloud.module.user.mode.UserRegisterInfo;
import com.mist.cloud.module.user.repository.IUserRepository;
import com.mist.cloud.core.config.IdGenerator;
import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.infrastructure.entity.User;
import com.mist.cloud.infrastructure.mapper.UserMapper;
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

    public User getUserByEmail(String email) {
        return userMapper.selectUserByEmail( email);
    }

    @Override
    public Long getRootFolderId(Long userId) {
        return getUser(userId).getRootFolderId();
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

    @Override
    public boolean checkEmailRegistered(String email) {
        User user = getUserByEmail(email);
        return user != null;
    }
}
