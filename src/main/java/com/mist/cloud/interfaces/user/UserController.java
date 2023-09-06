package com.mist.cloud.interfaces.user;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import com.mist.cloud.aggregate.user.service.CaptchaService;
import com.mist.cloud.aggregate.user.mode.CaptchaEntity;
import com.mist.cloud.aggregate.user.mode.req.MailReq;
import com.mist.cloud.aggregate.user.mode.req.RegisterReq;
import com.mist.cloud.aggregate.user.mode.res.CaptchaResponse;
import com.mist.cloud.aggregate.user.mode.req.LoginReq;
import com.mist.cloud.aggregate.user.mode.res.LoginResponse;
import com.mist.cloud.aggregate.user.service.MailService;
import com.mist.cloud.aggregate.user.service.UserService;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: securemist
 * @Datetime: 2023/7/21 16:55
 * @Description:
 */
@Api(value = "用户操作")
@RequestMapping("/user")
@RestController
public class UserController {


}
