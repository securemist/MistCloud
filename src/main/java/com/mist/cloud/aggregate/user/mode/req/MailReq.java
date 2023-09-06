package com.mist.cloud.aggregate.user.mode.req;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:05
 * @Description:
 */
@Data
@AllArgsConstructor
public class MailReq {

    // 验证码
    private String uid;
    private String code;
    private String email;
}
