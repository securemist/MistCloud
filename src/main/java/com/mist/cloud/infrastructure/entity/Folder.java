package com.mist.cloud.infrastructure.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/7/19 10:11
 * @Description:
 */
@Builder
@Data
@ApiModel("文件夹实体类")
public class Folder implements Serializable {
    private static final long serialVersionUID = 1L;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String name;

    private Date modifyTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;

    private Date createTime;

    private Integer deleted;

    private Date deletedTime;

}
