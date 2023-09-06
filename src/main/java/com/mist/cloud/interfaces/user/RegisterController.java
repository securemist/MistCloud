package com.mist.cloud.interfaces.user;

import com.mist.cloud.aggregate.user.mode.CaptchaEntity;
import com.mist.cloud.aggregate.user.mode.req.MailReq;
import com.mist.cloud.aggregate.user.mode.req.RegisterReq;
import com.mist.cloud.aggregate.user.mode.res.CaptchaResponse;
import com.mist.cloud.aggregate.user.service.CaptchaService;
import com.mist.cloud.aggregate.user.service.MailService;
import com.mist.cloud.aggregate.user.service.UserService;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
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

    @GetMapping("/code")
    @ApiOperation(value = "获取图片验证码")
    public Result code(@RequestParam String uid) throws IOException {
        // 创建验证码
        CaptchaEntity captchaEntity = captchaService.createCode(uid);

        uid = captchaEntity.getUid();
        String imgBase64 = captchaEntity.getImgBase64Data();

        CaptchaResponse captchaResponse = new CaptchaResponse(uid, imgBase64);
        return new SuccessResult(captchaResponse);
    }


    @PostMapping("/mail")
    @ApiOperation(value = "校验图片验证码同时发送邮件验证码")
    public Result mail(@RequestBody MailReq mailReq) {
        // 校验验证码
        boolean ok = captchaService.verify(mailReq.getUid(), mailReq.getCode());

        if (!ok) {
            return new FailedResult("验证码错误");
        }

        // 发送邮件 TODO
        mailService.sendMail(mailReq.getEmail());

        return new SuccessResult();
    }

    @PostMapping("/exec")
    public Result register(@RequestBody RegisterReq registerReq) {
        // 校验邮箱验证码
        String reason = mailService.verify(registerReq.getEmail(), registerReq.getMailCode());
        if (!"".equals(reason)) {
            return new FailedResult(reason);
        }

        // 开始注册
        userService.register(registerReq.getUsername(), registerReq.getPassword(), registerReq.getEmail());

        return new SuccessResult();
    }


}
