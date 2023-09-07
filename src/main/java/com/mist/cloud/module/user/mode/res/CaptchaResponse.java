package com.mist.cloud.module.user.mode.res;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 14:53
 * @Description:
 */
@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String uid;
    private String imgBase64;
    // md5加密过的答案
    private String answer;

}
