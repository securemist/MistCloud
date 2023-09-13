package com.mist.cloud.module.user.mode.req;

import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/8 20:50
 * @Description:
 */
@Data
public class MailLoginRequest {
    private String email;
    private String captcha;
}
