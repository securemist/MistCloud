package com.mist.cloud.infrastructure.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 15:11
 * @Description: 复制文件
 */
@Getter
@Setter
@Builder
public class FileCopyReq {
    private Long newFileId;

    private Long fileId;

    private Long targetFolderId;
}
