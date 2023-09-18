package com.mist.cloud.module.share.model.req;

import lombok.Data;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 14:48
 * @Description:
 */
@Data
public class ResaveFileRequest {
    private List<Long> idList;
    private Long targetFolderId;
}
