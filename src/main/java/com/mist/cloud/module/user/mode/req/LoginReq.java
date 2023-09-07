package com.mist.cloud.module.user.mode.req;

import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 15:19
 * @Description:
 */
@Data
public class LoginReq {
    private String username;
    private String password;
}
