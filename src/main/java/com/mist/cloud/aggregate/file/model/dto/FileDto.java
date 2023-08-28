package com.mist.cloud.aggregate.file.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Author: securemist
 * @Datetime: 2023/7/19 09:22
 * @Description:
 */

public class FileDto {
    private static final long serialVersionUID = 15646841656166L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer id;

    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer parentId;

    private Long size;

    private Integer isFolder;

    private String type;
}
