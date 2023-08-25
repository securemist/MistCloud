package com.mist.cloud.domain.file.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/8/24 14:12
 * @Description:
 */
@Builder
@Setter
@Getter
public class FolderVo implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    private Date modifyTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;

}
