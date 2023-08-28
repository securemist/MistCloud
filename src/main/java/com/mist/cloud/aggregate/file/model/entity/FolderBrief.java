package com.mist.cloud.aggregate.file.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 16:19
 * @Description:
 */
@Getter
@Setter
@Builder
public class FolderBrief {
    public String name;
    public Long id;
    private Long userId;
    public Long parentId;
}
