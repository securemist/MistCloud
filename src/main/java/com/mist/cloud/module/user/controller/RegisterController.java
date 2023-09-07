package com.mist.cloud.module.user.controller;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.crypto.digest.MD5;
import com.mist.cloud.core.exception.auth.RegisterException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.user.mode.CaptchaEntity;
import com.mist.cloud.module.user.mode.req.MailReq;
import com.mist.cloud.module.user.mode.req.RegisterReq;
import com.mist.cloud.module.user.mode.res.CaptchaResponse;
import com.mist.cloud.module.user.service.CaptchaService;
import com.mist.cloud.module.user.service.MailService;
import com.mist.cloud.module.user.service.UserService;
import com.mist.cloud.core.result.FailedResult;
import com.mist.cloud.core.result.Result;
import com.mist.cloud.core.result.SuccessResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:50
 * @Description:
 */
@RestController
@RequestMapping("/user/register")
public class RegisterController {
    @Resource
    private UserService userService;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private MailService mailService;

    @PostMapping("/checkUsername")
    @ApiOperation(value = "校验用户名是否可注册")
    public Result checkUsername(@RequestBody String username) {
        String reason = userService.checkUsername(username);
        return new SuccessResult(reason);
    }

    /**
     * 获取验证码服务
     *
     * @param uid 前端传来表示用户会话的uid
     * @return
     * @throws IOException
     */
    @GetMapping("/code")
    public Result code( String uid) throws IOException {
        AbstractCaptcha shearCaptcha = captchaService.createCode(uid);

        String imgBase64 = shearCaptcha.getImageBase64Data(); // 图片验证码base64格式
        String ans = MD5.create().digestHex(shearCaptcha.getCode()); // md5加密过的验证码答案

        CaptchaResponse captchaResponse = new CaptchaResponse(uid, imgBase64, ans);
        return new SuccessResult(captchaResponse);
    }


    /**
     * 获取验证码接口
     *
     * @param mailReq
     * @return
     */
    @PostMapping("/mail")
    public R mail(@RequestBody MailReq mailReq) {
        mailService.sendMail(mailReq.getEmail());
        return R.success();
    }

    @PostMapping("/exec")
    public Result register(@RequestBody RegisterReq registerReq) {
        // 校验邮箱验证码
        mailService.verify(registerReq.getEmail(), registerReq.getMailCode());

        // 开始注册
        userService.register(registerReq.getUsername(), registerReq.getPassword(), registerReq.getEmail());

        return new SuccessResult();
    }


}
