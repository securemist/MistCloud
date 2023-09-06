package com.mist.cloud.aggregate.user.mode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:41
 * @Description:
 */
@Builder
@AllArgsConstructor
@Data
public class UserRegisterInfo {
    private String username;
    private String password;
    private String email;
}
