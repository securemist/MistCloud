package com.mist.cloud.aggregate.user.mode.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 15:57
 * @Description:
 */
@Data
public class RegisterReq implements Serializable {
    private static final long serialVersionUID = 101L;
    // 登陆id
    private String uid;
    // 邮箱验证码
    private String mailCode;
    private String email;
    private String username;
    private String password;
}
