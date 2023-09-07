package com.mist.cloud.module.user.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import com.mist.cloud.module.user.mode.req.LoginReq;
import com.mist.cloud.module.user.mode.res.LoginResponse;
import com.mist.cloud.module.user.service.UserService;
import com.mist.cloud.core.result.Result;
import com.mist.cloud.core.result.SuccessResult;
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

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    @ApiImplicitParam(name = "jsonParam", value = "用户名 + 密码", dataTypeClass = JSONObject.class)
    public Result login(@RequestBody LoginReq loginReq) {

        Long folderId = userService.login(loginReq.getUsername(), loginReq.getPassword());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginResponse loginResponse = new LoginResponse(folderId, tokenInfo);
        return new SuccessResult(loginResponse);
    }

    @GetMapping("/logout")
    @ApiOperation(value = "退出登录")
    public Result login() {
        StpUtil.logout();
        return new SuccessResult();
    }

}
