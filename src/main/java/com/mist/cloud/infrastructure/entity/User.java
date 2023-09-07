package com.mist.cloud.core.infrastructure.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:30
 * @Description:
 */
@Data
@Builder
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Long usedCapacity;
    private Long totalCapacity;
    private Long rootFolderId;
}
