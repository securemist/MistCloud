package com.mist.cloud.module.file.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/7/20 19:57
 * @Description:
 */
@Builder
@ToString
@Getter
@Setter
public class UserCapacityDto implements Serializable {
    private static final long serialVersionUID = 1564621456166L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private Long usedCapacity;
    private Long totalCapacity;
}
