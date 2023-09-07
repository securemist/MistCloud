package com.mist.cloud.aggregate.user.service;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.core.lang.UUID;
import com.mist.cloud.aggregate.user.mode.CaptchaEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 14:09
 * @Description:
 *
 * 验证码服务
 */
@Service
public class CaptchaService {

    /**
     * 创建一个验证码
     * <p>
     * 每次创建验证码会生成一个UUID进行表示，用户登陆的时候需要携带UUID
     * 后续用户每次刷新验证码这个UUID都不会变
     *
     * @return UUID 用户登陆表示  Icaptcha验证码
     */

    private Map<String, String> captcharMap;

    @PostConstruct
    public void init() {
        captcharMap = new ConcurrentHashMap<>();
    }

    public CaptchaEntity createCode(String lastUid) {
        AbstractCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);
        String code = shearCaptcha.getCode();

        // 刷新验证码
        if (lastUid != null) {
            captcharMap.put(lastUid, code);
            return new CaptchaEntity(lastUid, shearCaptcha.getImageBase64Data());
        }

        // 第一次创建验证码
        String uuid = UUID.randomUUID().toString();
        captcharMap.put(uuid, code);

        String imageBase64Data = shearCaptcha.getImageBase64Data();
        return new CaptchaEntity(uuid, shearCaptcha.getImageBase64Data());
    }

    public boolean verify(String uuid, String userInputCode) {
        String code = captcharMap.get(uuid);

        // 获取不到用户uid信息
        if (code == null) {
            return false;
        }

        // 不管成功与否都删除验证码
        captcharMap.remove(uuid);

        return code.equals(userInputCode);
    }
}
