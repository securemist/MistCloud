package com.mist.cloud.module.user.mode.req;

import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:05
 * @Description: 验证码请求
 *
 */
@Data
public class MailRequest {
    // 用户注册的会话id
    private String uid;
    private String email;
}
