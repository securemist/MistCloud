package com.mist.cloud.module.user.mode.req;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:05
 * @Description:
 */
@Data
public class MailReq {

    // 验证码
    private String uid;
    private String email;
}
