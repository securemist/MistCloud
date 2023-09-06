package com.mist.cloud.aggregate.user.service;

import cn.dev33.satoken.stp.StpUtil;
import com.mist.cloud.aggregate.user.mode.UserRegisterInfo;
import com.mist.cloud.aggregate.user.repository.IUserRepository;
import com.mist.cloud.infrastructure.DO.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 19:29
 * @Description:
 */
@Service
public class UserService {
    @Resource
    private IUserRepository userRepository;

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 用户根目录id
     */
    public void register(String username, String password, String email) {
        // 二次校验用户名
        UserRegisterInfo userRegisterInfo = new UserRegisterInfo(username, password, email);

        userRepository.addUser(userRegisterInfo);
    }


    /**
     * 用户登陆
     * @param username
     * @param password
     * @return 返回根目录id NULL表示用户名密码错误
     */
    public Long login(String username, String password) {
        User user = userRepository.getUser(username);

        if(user == null || !user.getPassword().equals(password)) {
            return null;
        }

        // 添加登陆信息
        StpUtil.login(user.getId());

        return user.getRootFolderId();
    }

    /**
     * 校验用户名是否可注册
     * @param username
     * @return
     */
    public String checkUsername(String username) {
        User user = userRepository.getUser(username);
        if(user != null) {
            return "该用户名已注册";
        } else {
            return "";
        }
    }
}
