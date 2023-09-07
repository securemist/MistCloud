package com.mist.cloud.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 15:11
 * @Description:
 */
@Component
public class FileConfig {
    @Value("${file.base_path}")
    private String path;

    /**
     * 文件上传之后最终存储的位置
     * @return
     */
    public String getBasePath() {
        return path + "/file";
    }

    /**
     * 文件下载时的临时存储位置
     * @return
     */
    public String getDownloadPath() {
        return path + "/download";
    }

    /**
     * 文件上传时的临时存储位置
     * @return
     */
    public String getUploadPath() {
        return path + "/upload";
    }
}
