package com.mist.cloud.module.user.mode.req;

import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 15:57
 * @Description:
 */
@Data
public class RegisterRequest {
    // 登陆id
    private String uid;
    // 邮箱验证码
    private String captcha;

    private String email;
    private String username;
    private String password;
}
