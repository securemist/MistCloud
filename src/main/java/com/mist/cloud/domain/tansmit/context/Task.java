package com.mist.cloud.domain.tansmit.context;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 15:02
 * @Description:
 */
@Setter
@Getter
public class Task {
    // 文件标识，同时为临时的分片文件夹名称
    private String identifier;

    // 已经上传的分片
    public volatile boolean[] uploadChunks;

    private Date startTime;

    private Date completeTime;

    // 文件md5值，用来验证文件传输之后是否完好
    private String MD5 = null;

    private String fileName;

    private String fileType;

    // 文件分片的所在文件夹路径
    private String folderPath;

    // 目标文件的路径
    private String targetFilePath;

    // 上传的文件夹 id
    private Long folderId;

    // 文件大小
    private Long fileSize;

    // 文件真实路径，主要适用于文件夹上传
    private String relativePath;

    public Task(String identifier, Integer chunkSize) {
        this.identifier = identifier;
        this.uploadChunks = new boolean[chunkSize + 1];
        this.startTime = new Date();
    }

}
