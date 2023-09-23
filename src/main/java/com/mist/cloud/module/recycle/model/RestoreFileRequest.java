package com.mist.cloud.module.recycle.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/22 18:50
 * @Description:
 */
@Data
@Builder
public class RestoreFileRequest {
    private Long sourceId;
    private String fileName;
    private Long targetFolderId;
}
