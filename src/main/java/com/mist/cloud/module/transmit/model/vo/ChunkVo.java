package com.mist.cloud.module.transmit.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 13:43
 * @Description:
 */
@Data
@Getter
@Setter
@Builder
@Schema(description = "文件分片类")
public class ChunkVo  {

    @Schema(description = "当前文件块，从1开始")
    private Integer chunkNumber;

    @Schema(description = "分块大小")
    private Long chunkSize;

    @Schema(description = "当前分块大小")
    private Long currentChunkSize;

    @Schema(description = "总大小")
    private Long totalSize;

    @Schema(description = "任务标识")
    private String identifier;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "总块数")
    private Integer totalChunks;

    @Schema(description = "相对路径")
    private String relativePath;

    @Schema(description = "文件夹 id")
    private Long folderId;

    @Schema(description = "文件file")
    private MultipartFile file;
}
