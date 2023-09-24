package com.mist.cloud.module.transmit.model.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: securemist
 * @Datetime: 2023/9/24 19:17
 * @Description:
 */
@Data
public class SimpleUploadRequest {
    private MultipartFile file;
    private Long folderId;
    private String path;
    private String identifier;
}
