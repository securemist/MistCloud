package com.mist.cloud.module.user.mode.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/8 20:50
 * @Description:
 */
@Data
@Schema(description = "邮箱登陆请求")
public class MailLoginRequest {
    @Schema(description = "邮箱账号")
    private String email;
    @Schema(description = "验证码")
    private String captcha;
}
