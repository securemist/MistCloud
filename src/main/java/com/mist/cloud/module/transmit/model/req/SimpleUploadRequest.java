package com.mist.cloud.module.transmit.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: securemist
 * @Datetime: 2023/9/24 19:17
 * @Description:
 */
@Data
@Schema(description = "简单文件上传请求类")
public class SimpleUploadRequest {
    @Schema(description = "文件")
    private MultipartFile file;
    @Schema(description = "目标文件夹id")
    private Long folderId;
    @Schema(description = "上传路径")
    private String path;
    @Schema(description = "任务标识")
    private String identifier;
}
