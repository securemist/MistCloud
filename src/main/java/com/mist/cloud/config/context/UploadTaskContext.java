package com.mist.cloud.config.context;

import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.model.vo.FileInfoVo;

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
     * 完成任务
     * @param identifier 任务标识
     * @throws FileUploadException 这个请求必须确保任务已经建立，否则抛出该异常
     */
    public void completeTask(String identifier) throws FileUploadException;

    /**
     * 取消上传任务
     * @param identifier
     */
    public void cancelTask(String identifier) throws FileUploadException;


    /**
     * 获取任务信息
     * @param identifier
     * @return
     * @throws FileUploadException 通过任务表示获取任务，如果获取不到说明 task 还没建立，这个 http 请求来的不是时候
     */
    public Task getTask(String identifier);


    /**
     * 设置任务文件相关信息
     *
     * @param fileInfoList
     */
    void setTaskInfo(FileInfoVo[] fileInfoList) throws FileUploadException;



}
