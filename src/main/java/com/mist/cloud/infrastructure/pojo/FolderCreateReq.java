package com.mist.cloud.infrastructure.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 15:31
 * @Description:
 */
@Getter
@Setter
@Builder
public class FolderCreateReq {
    private Long folderId;
    private String folderName;
    private Long parentId;
    private Long userId;
}
