package com.mist.cloud.module.transmit.model.vo;

import lombok.*;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/8/21 20:52
 * @Description:
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MergeFileRequestVo implements Serializable {
    private String identifier;
    private String md5;
}
