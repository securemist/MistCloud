package com.mist.cloud.infrastructure.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 15:01
 * @Description:
 */
@Data
@Builder
public class FolderResaveReq {
    private Long userId;
    private Long folderId;
    private Long targetFolderId;
    private Long newFolderId;
}
