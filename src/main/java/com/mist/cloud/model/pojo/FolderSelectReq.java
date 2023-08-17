package com.mist.cloud.model.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/7/23 15:01
 * @Description:
 */
@Getter
@Setter
@Builder
public class FolderSelectReq {
    private Long id;

    private Long userId;

    private Long parentId;

    private String folderName;
}
