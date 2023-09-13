package com.mist.cloud.module.user.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import com.mist.cloud.core.exception.auth.RegisterException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.user.mode.req.LoginRequest;
import com.mist.cloud.module.user.mode.req.MailLoginRequest;
import com.mist.cloud.module.user.mode.res.LoginResponse;
import com.mist.cloud.module.user.service.MailService;
import com.mist.cloud.module.user.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:50
 * @Description:
 */
@RestController
@RequestMapping("/user/auth")
public class AuthController {
    @Resource
    private UserService userService;
    @Resource
    private MailService mailService;

    @PostMapping("/login/account")
    @ApiOperation(value = "账号密码登录")
    @ApiImplicitParam(name = "jsonParam", value = "用户名 + 密码", dataTypeClass = JSONObject.class)
    public R login(@RequestBody LoginRequest loginReq) {

        Long folderId = userService.login(loginReq.getUsername(), loginReq.getPassword());
        if (folderId == null) {
            return R.error("用户名或密码错误");
        }
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginResponse loginResponse = new LoginResponse(folderId, tokenInfo);
        return R.success(loginResponse);
    }


    @PostMapping("/login/email")
    @ApiOperation(value = "邮箱登陆")
    public R login(@RequestBody MailLoginRequest mailLoginRequest) {
        mailService.verify(mailLoginRequest.getEmail(), mailLoginRequest.getCaptcha());

        Long folderId = userService.login(mailLoginRequest.getEmail());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginResponse loginResponse = new LoginResponse(folderId, tokenInfo);
        return R.success(loginResponse);
    }

    @GetMapping("/logout")
    @ApiOperation(value = "退出登录")
    public R login() {
        StpUtil.logout();
        return R.success();
    }

    @GetMapping("/email/getCaptcha")
    public R getMailCaptcha(@RequestParam String email) {
        mailService.sendMail(email);
        return R.success();
    }
}
