package com.mist.cloud.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 15:11
 * @Description:
 */
@Data
@Component
public class FileConfig {
    @Value("${file.base_path}")
    private String base_path;
}
