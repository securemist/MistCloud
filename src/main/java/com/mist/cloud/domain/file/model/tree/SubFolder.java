package com.mist.cloud.domain.file.model.tree;

import lombok.*;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 11:01
 * @Description:
 */
@Getter
@Setter
@Builder
@ToString
public class SubFolder {
    private Long id;
    private String name;
    private Long parentId;
}
