package com.mist.cloud.domain.tansmit.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: securemist
 * @Datetime: 2023/8/21 20:52
 * @Description:
 */
@Builder
@Getter
@Setter
public class MergeFileRequestVo {
    private String identifier;
    private String md5;
}
