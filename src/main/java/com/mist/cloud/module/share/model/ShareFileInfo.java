package com.mist.cloud.module.share.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 11:37
 * @Description: 分享预览
 */
@Data
public class ShareFileInfo {
    private User user; // 分享者信息
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fileId; // 文件id
    private String fileName; // 文件名
    private Boolean isFolder; // 是否是文件夹
    private String description; // 分享的备注
    private String url; // 完整链接，包含提取码
    private ShareStatusType status; // 状态
    private Date createTime; // 分享时间
    private Date expireTime; // 过期时间
    private FolderDetail folderDetail; // 文件夹详细信息

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        @JsonSerialize(using = ToStringSerializer.class)
        Long userId;
        String name;
    }

}
