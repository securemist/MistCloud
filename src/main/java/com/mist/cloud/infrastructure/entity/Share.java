package com.mist.cloud.infrastructure.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 08:14
 * @Description:
 */
@Data
@Builder
public class Share {
    private Long id;

    private String uniqueKey;

    private Long userId;

    private Long fileId;

    private String code;

    private Integer visitLimit;

    private Integer visitTimes;

    private Integer downloadTimes;

    private String description;

    private Date createTime;

    private Date expireTime;

}
