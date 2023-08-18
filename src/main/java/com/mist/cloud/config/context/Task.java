package com.mist.cloud.config.context;

import com.mist.cloud.model.vo.ChunkVo;

import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 15:02
 * @Description:
 */
public class Task {

    private String identifier;

    // 已经上传的分片
    public boolean[] uploadChunks;

    private Date startTime;

    private Date completeTime;

    // 文件md5值，用来验证文件传输之后是否完好
    private String MD5 = null;

    public Task(String identifier, Integer chunkSize) {
        this.identifier = identifier;
        this.uploadChunks = new boolean[chunkSize + 1];
        this.startTime = new Date();
    }


    public void completeTask() {
        this.completeTime = new Date();
    }

    public void setMD5(String md5) {
        this.MD5 = md5;
    }

    public String getMD5() {
        return this.MD5;
    }
}
