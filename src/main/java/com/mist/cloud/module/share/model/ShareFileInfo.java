package com.mist.cloud.module.share.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 11:37
 * @Description:
 */
@Data
public class ShareFileInfo {
    private User user; // 分享者信息
    private List<File> fileList; // 文件列表
    private String description; //分享的备注
    private String link; // 完整链接，包含提取码
    private ShareStatusType status; // 状态
    private Date createTime; // 分享时间
    private Date expireTime; // 过期时间


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        @JsonSerialize(using = ToStringSerializer.class)
        Long userId;
        String name;
    }

    @Data
    public static class File{
        @JsonSerialize(using = ToStringSerializer.class)
        Long id;
        String name;
        Boolean isFolder;
        Long size;
        Date modifyTime;
    }
}
