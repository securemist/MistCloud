package com.mist.cloud.infrastructure.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 16:41
void copyFolder(Long folderId, Long newFolderId, Long targetFolderId);
 * @Description:
 */
@Getter
@Setter
@Builder
public class FolderCopyReq {
    private Long folderId;
    private Long newFolderId;
    private Long targetFolderId;
}
