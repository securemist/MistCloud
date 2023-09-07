package com.mist.cloud.aggregate.user.mode;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.ICaptcha;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 14:40
 * @Description:
 */
@Data
@AllArgsConstructor
public class CaptchaEntity {
    private String uid;
    private String imgBase64Data;
}
