package com.mist.cloud.aggregate.user.mode.res;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:14
 * @Description:
 */
@Data
@AllArgsConstructor
public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 10L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long folderId;

    private SaTokenInfo tokenInfo;
}
