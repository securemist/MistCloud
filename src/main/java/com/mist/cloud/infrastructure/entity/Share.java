package com.mist.cloud.infrastructure.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "主键 ID")
    private Long id;

    @ApiModelProperty(value = "分享者用户id")
    private Long userId;

    @ApiModelProperty(value = "文件列表 JSON形式")
    private String fileIds;

    @ApiModelProperty(value = "链接表示")
    private String identifier;

    @ApiModelProperty(value = "提取码")
    private String extreactCode;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "过期时间")
    private Date expireTime;
}
