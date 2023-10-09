package com.mist.cloud.module.user.mode.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:05
 * @Description: 验证码请求
 *
 */
@Data
@Schema(description = "邮箱注册请求类")
public class MailRequest {
    @Schema(description = "用户注册的会话id")
    private String uid;
    @Schema(description = "邮箱账号")
    private String email;
}
