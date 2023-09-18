package com.mist.cloud.module.share.model.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 08:17
 * @Description:
 */
@Data
public class CreateShareRequest implements Serializable {
    // 分享的文件列表
    private List<Long> idList;

    // 有效期 单位：天
    private Integer effectiveTime;

    // 提取码，可以为空
    private String extractCode;

    // 备注
    private String description;
}
