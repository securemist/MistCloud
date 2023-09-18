package com.mist.cloud.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 15:00
 * @Description:
 */
public class Constants {

    // 用户的默认云盘容量20G
    public static final Long DEFAULT_USER_CAPACITY = 20 * 1024 * 1024 * 1024L;





    @Getter
    @AllArgsConstructor
    public enum FileType {
        DOCUMENT("文档"),
        IMG("图片"),
        VIDEO("视频"),
        BINARY("二进制文件");

        String type;
    }
}
