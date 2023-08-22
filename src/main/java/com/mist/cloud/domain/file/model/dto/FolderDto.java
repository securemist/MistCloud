package com.mist.cloud.domain.file.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 20:21
 * @Description:
 *
 * 返回给前端的文件夹信息
 */
@Builder
@Setter
@Getter
public class FolderDto implements Serializable {
    private static final long serialVersionUID = 15646841656166L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String name;

    private Long size;

    private String createTime;
}
