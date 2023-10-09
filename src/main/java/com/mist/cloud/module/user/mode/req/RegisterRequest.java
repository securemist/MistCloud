package com.mist.cloud.module.user.mode.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 15:57
 * @Description:
 */
@Data
@Schema(description = "邮箱注册请求类")
public class RegisterRequest {
    @Schema(description = "会话id")
    private String uid;

    @Schema(description = "验证码")
    private String captcha;

    private String email;
    private String username;
    private String password;
}
