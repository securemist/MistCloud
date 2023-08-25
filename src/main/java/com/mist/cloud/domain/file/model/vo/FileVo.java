package com.mist.cloud.domain.file.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/8/24 14:13
 * @Description:
 */
@Builder
@Setter
@Getter
public class FileVo implements Serializable {

    private Long id;

    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;

    private Date createTime;
}
