package com.mist.cloud.module.share.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: securemist
 * @Datetime: 2023/9/19 08:18
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareItem {
    private Long fileId;
    private String name;
    private Boolean isFolder;

    private String description;
    private Integer visitTime;

    private String url;
    private String code;
}
