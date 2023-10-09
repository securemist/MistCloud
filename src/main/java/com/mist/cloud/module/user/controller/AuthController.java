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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:50
 * @Description:
 */
@RestController
@RequestMapping("/user/auth")
@Tag(name = "用户登陆")
public class AuthController {
    @Resource
    private UserService userService;
    @Resource
    private MailService mailService;

    @PostMapping("/login/account")
    @Operation(summary = "账号密码登录")
    @Parameter(name = "loginReq")
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
    @Operation(summary = "邮箱登陆")
    @Parameter(name = "mailLoginRequest")
    public R login(@RequestBody MailLoginRequest mailLoginRequest) {
        mailService.verify(mailLoginRequest.getEmail(), mailLoginRequest.getCaptcha());

        Long folderId = userService.login(mailLoginRequest.getEmail());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginResponse loginResponse = new LoginResponse(folderId, tokenInfo);
        return R.success(loginResponse);
    }

    @GetMapping("/logout")
    @Operation(summary = "退出登录")
    public R login() {
        StpUtil.logout();
        return R.success();
    }

    @GetMapping("/email/getCaptcha")
    @Operation(summary = "获取邮箱验证码")
    @Parameter(name = "email", description = "邮箱账号")
    public R getMailCaptcha(@RequestParam String email) {
        mailService.sendMail(email);
        return R.success();
    }
}
