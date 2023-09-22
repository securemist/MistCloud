package com.mist.cloud.infrastructure.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
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
@ApiModel("分享链接")
public class Share {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "唯一标识")
    private String uniqueKey;

    @ApiModelProperty(value = "分享者用户id")
    private Long userId;

    @ApiModelProperty(value = "文件")
    private Long fileId;

    @ApiModelProperty(value = "提取码")
    private String code;

    @ApiModelProperty(value = "访问人数限制")
    private Integer visitLimit;

    @ApiModelProperty(value = "访问次数")
    private Integer visitTimes;

    @ApiModelProperty(value = "下载次数")
    private Integer downloadTimes;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

}
