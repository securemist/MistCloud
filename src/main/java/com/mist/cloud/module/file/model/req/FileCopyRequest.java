package com.mist.cloud.module.file.model.req;

import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/13 16:32
 * @Description:
 */
@Data
public class FileCopyRequest {
    private Long targetFolderId;
    private Long[] idList;
}
