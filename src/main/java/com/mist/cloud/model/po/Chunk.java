package com.mist.cloud.model.po;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 09:01
 * @Description:
 */
@Data
@Getter
@Setter
public class Chunk implements Serializable {

    @Id
    private Long id;
    /**
     * 当前文件块，从1开始
     */
    private Integer chunkNumber;
    /**
     * 分块大小
     */
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    private Long currentChunkSize;
    /**
     * 总大小
     */
    private Long totalSize;
    /**
     * 文件标识
     */
    private String identifier;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 总块数
     */
    private Integer totalChunks;
    /**
     * 文件类型
     */
    private String type;

    private MultipartFile file;
}
