package com.mist.cloud.module.user.controller;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.crypto.digest.MD5;
import com.mist.cloud.core.exception.auth.RegisterException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.user.mode.req.MailRequest;
import com.mist.cloud.module.user.mode.req.RegisterRequest;
import com.mist.cloud.module.user.mode.res.CaptchaResponse;
import com.mist.cloud.module.user.repository.IUserRepository;
import com.mist.cloud.module.user.service.CaptchaService;
import com.mist.cloud.module.user.service.MailService;
import com.mist.cloud.module.user.service.UserService;
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
    @Resource
    private IUserRepository userRepository;

    @PostMapping("/checkUsername")
    @ApiOperation(value = "校验用户名是否可注册")
    public R checkUsername(@RequestBody String username) {
        String reason = userService.checkUsername(username);
        return R.success(reason);
    }

    /**
     * 获取验证码服务
     *
     * @param uid 前端传来表示用户会话的uid
     * @return
     * @throws IOException
     */
    @GetMapping("/code")
    public R code(String uid) throws IOException {
        AbstractCaptcha shearCaptcha = captchaService.createCode(uid);

        String imgBase64 = shearCaptcha.getImageBase64Data(); // 图片验证码base64格式
        String ans = MD5.create().digestHex(shearCaptcha.getCode()); // md5加密过的验证码答案

        CaptchaResponse captchaResponse = new CaptchaResponse(uid, imgBase64, ans);
        return R.success(captchaResponse);
    }


    /**
     * 获取邮箱验证码接口
     * <p>
     * 注册获取邮箱验证码或登陆获取邮箱验证码公用一个接口
     *
     * @param mailReq
     * @return
     */
    @PostMapping("/mail")
    public R mail(@RequestBody MailRequest mailReq) {
        // 校验该邮箱是否已注册
        boolean resigtered = userRepository.checkEmailRegistered(mailReq.getEmail());
        if (resigtered) {
            return R.error("该邮箱已注册");
        }

        mailService.sendMail(mailReq.getEmail());
        return R.success();
    }

    @PostMapping("/exec")
    public R register(@RequestBody RegisterRequest registerReq) {
        // 校验邮箱验证码
        mailService.verify(registerReq.getEmail(), registerReq.getCaptcha());

        // 开始注册
        userService.register(registerReq.getUsername(), registerReq.getPassword(), registerReq.getEmail());

        return R.success();
    }


}
