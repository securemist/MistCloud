package com.mist.cloud.aggregate.tansmit.model.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 09:40
 * @Description:
 */
@Data
@Getter
@Setter
public class FileInfoVo implements Serializable {

    private String fileName;

    private String identifier;

    private Long totalSize;

    private String type;

    private String md5;

    private Integer totalChunks;

    private Long folderId;

    private String relativePath;
}
