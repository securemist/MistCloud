package com.mist.cloud.module.share.model.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 09:39
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareLinkResponse {
    // 生成的链接
    private String url;

    // 提取码
    private String code;

    // 唯一标识
    private String uniqueKey;
}
