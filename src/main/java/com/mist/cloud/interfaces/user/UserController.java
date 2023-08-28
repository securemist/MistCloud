package com.mist.cloud.interfaces.user;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @Author: securemist
 * @Datetime: 2023/7/21 16:55
 * @Description:
 */
@RestController
@Api(value = "用户操作")
@RequestMapping("/user")
public class UserController {


    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "前端传入用户名密码，这里使用@RequestParm接受失败，改用RequestBody接受整个 json 数据")
    @ApiImplicitParam(name = "jsonParam", value = "用户名 + 密码", dataTypeClass = JSONObject.class)
    public Result login(@RequestBody JSONObject jsonParam) {
        String userName = jsonParam.get("username").toString();
        // TODO 登录逻辑

        Long rootFolderId = 1L;
        Long userId = 1L;
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();


        HashMap<String, Object> map = new HashMap<>();
        map.put("tokenInfo", tokenInfo);
        map.put("rootFolderId", rootFolderId.toString());
        return new SuccessResult(map);
    }

    @GetMapping("/logout")
    @ApiOperation(value = "退出登录")
    public Result login() {
        StpUtil.logout();
        return new SuccessResult();
    }
}
