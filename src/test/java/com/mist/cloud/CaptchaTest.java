package com.mist.cloud;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import org.junit.Test;

import java.awt.image.BufferedImage;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 14:05
 * @Description:
 */
public class CaptchaTest {

    @Test
    public void code() {
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);

        String imageBase64 = shearCaptcha.getImageBase64Data();
        System.out.println(imageBase64);
    }
}
