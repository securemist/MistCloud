package com.mist.cloud.config.context;

import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;

/**
 * @Author: securemist
 * @Datetime: 2023/8/16 16:21
 * @Description:
 */
public interface UploadTaskContext {

    /**
     * 添加文件分片
     * @param chunk
     */
    public void addChunk(ChunkVo chunk);

    /**
     * 检验该分片是否已上传
     * @param chunk
     * @return
     */
    public boolean checkChunkUploaded(ChunkVo chunk);

    /**
     * 完成任务
     * @param identifier 任务标识
     * @param md5 文件的 md5 值
     * @return 会校验文件的 md5 值，失败返回 false
     * @throws FileUploadException 这个请求必须确保任务已经建立，否则抛出该异常
     */
    public boolean completeTask(String identifier, String md5) throws FileUploadException;

    /**
     * 设置任务文件的 md5 值
     * @param chunk
     * @param md5
     * @return
     */
    boolean SetMD5(ChunkVo chunk, String md5);
}
