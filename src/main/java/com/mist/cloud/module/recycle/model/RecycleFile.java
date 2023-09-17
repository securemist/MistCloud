package com.mist.cloud.module.recycle.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/9/17 08:54
 * @Description:
 */
@Data
@Builder
public class RecycleFile implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Boolean isFolder;

    private String path;

    private String name;

    private Long size;

    private Date deletedTime;
}
