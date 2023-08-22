package com.mist.cloud.domain.file.model.entity;

import lombok.*;

/**
 * @Author: securemist
 * @Datetime: 2023/7/19 09:29
 * @Description:
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSelectReq {

    private Long id;
    private Long folderId;
    private Long userId;
    private String fileName;
    private Long fileSize;
}
