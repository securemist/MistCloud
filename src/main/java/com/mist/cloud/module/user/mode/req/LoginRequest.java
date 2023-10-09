package com.mist.cloud.module.user.mode.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 15:19
 * @Description:
 */
@Data
@Schema(description = "账号密码登陆请求类")
public class LoginRequest {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
}
