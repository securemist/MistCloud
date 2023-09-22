package com.mist.cloud.module.share.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/9/19 08:18
 * @Description: 分享管理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareItem {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fileId;
    private String fileName;
    private Boolean isFolder;
    private String uniqueKey;

    private Integer visitLimit;
    private Integer visitTimes;
    private Integer downloadTimes;

    private String description;

    private String url;
    private String code;

    private Date createTime;
    private Date expireTime;
}
