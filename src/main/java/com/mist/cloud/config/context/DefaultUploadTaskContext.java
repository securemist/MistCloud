package com.mist.cloud.config.context;

import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.mist.cloud.utils.Session.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 14:17
 * @Description:
 */
@Component
public class DefaultUploadTaskContext extends AbstractUploadContext implements UploadTaskContext {
    /**
     * 文件上传任务上下文
     *
     * String 用户登录 id
     * Map<String, Task>
     *     String 任务唯一标识
     *     Task 上传任务对象
     */
    private volatile ConcurrentMap<String, Map<String, Task>> uploadContexts = new ConcurrentHashMap<>();

    @Override
    protected Map<String, Task> getUploadContext() {
        Map<String, Task> taskMap = uploadContexts.get(getLoginId());
        if (taskMap == null) {
            synchronized (DefaultUploadTaskContext.class) {
                taskMap = new HashMap<>();
                uploadContexts.put(getLoginId(), taskMap);
            }
        }
        return uploadContexts.get(getLoginId());
    }
    @Override
    protected void setUploadContext(Map<String, Task> taskMap) {
        uploadContexts.put(getLoginId(), taskMap);
    }


    @Override
    public Task getTask(String identifier){
        Map<String, Task> uploadContext = getUploadContext();

        /**
         * 前端在开始发送文件前会将文件md5 等信息发送过来，会在这时候就创建完毕
         */
        Task task = uploadContext.get(identifier);
        if(task == null){
            synchronized (DefaultUploadTaskContext.class){
                if(task == null){
                    task = new Task(identifier);
                    uploadContext.put(identifier,task);
                }
            }
        }
        return task;
    }

    @Override
    protected void updateTask(ChunkVo chunk) {
        Map<String, Task> uploadContext = getUploadContext();
        Task task = getTask(chunk.getIdentifier());

        // 在第一次创建 task 的时候并不会创建 uploadChunks 数组，需要在上传分片的时候创建
        if(task.uploadChunks == null){
            synchronized (DefaultUploadTaskContext.class){
                if(task.uploadChunks == null){
                    task.uploadChunks = new boolean[chunk.getTotalChunks() + 1];
                }
            }
        }
        task.uploadChunks[chunk.getChunkNumber()] = true;

        uploadContext.put(chunk.getIdentifier(), task);
        setUploadContext(uploadContext);
    }


}

