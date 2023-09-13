package com.mist.cloud.module.file.model.req;

import lombok.Data;

/**
 * @Author: securemist
 * @Datetime: 2023/9/13 16:18
 * @Description:
 */
@Data
public class FileDeleteRequest {
    private Long[] idList;
    private Boolean realDelete;
}
