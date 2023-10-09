package com.mist.cloud.module.share.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 08:17
 * @Description:
 */
@Data
@Schema(description = "创建分享请求类")
public class CreateShareRequest implements Serializable {
    @Schema(description = "分享的文件id")
    private Long fileId;

    @Schema(description = " 有效期 单位：天")
    private Integer timeLimit;

    @Schema(description = "提取码，可以为空")
    private String code;

    @Schema(description = "备注")
    private String description;

    @Schema(description = "访问人数限制")
    private Integer visitLimit;
}
