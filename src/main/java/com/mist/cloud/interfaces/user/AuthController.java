package com.mist.cloud.interfaces.user;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import com.mist.cloud.aggregate.user.mode.req.LoginReq;
import com.mist.cloud.aggregate.user.mode.res.LoginResponse;
import com.mist.cloud.aggregate.user.service.UserService;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
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

        if(folderId == null) {
            return new FailedResult("用户名或密码错误");
        }

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
