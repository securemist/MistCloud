package com.mist.cloud.config.context;

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
    protected Task getTask(ChunkVo chunk) {
        Map<String, Task> uploadContext = getUploadContext();
        Task task = uploadContext.get(chunk.getIdentifier());
        if (task == null) {
            synchronized (DefaultUploadTaskContext.class) {
                if(task == null){
                    task = new Task(chunk.getIdentifier(), chunk.getTotalChunks());
                    uploadContext.put(chunk.getIdentifier(), task);
                    setUploadContext(uploadContext);
                }
            }
        }

        return task;
    }

    @Override
    protected void updateTask(ChunkVo chunk) {
        Map<String, Task> uploadContext = getUploadContext();
        Task task = getTask(chunk);

        // 更新任务
        task = getTask(chunk);

        task.uploadChunks[chunk.getChunkNumber()] = true;

        uploadContext.put(chunk.getIdentifier(), task);
        setUploadContext(uploadContext);
    }

    @Override
    public boolean SetMD5(ChunkVo chunk, String md5) {
        Map<String, Task> uploadContext = getUploadContext();

        Task task = getTask(chunk);
        task.setMD5(md5);
        uploadContext.put(chunk.getIdentifier(),task);

        setUploadContext(uploadContext);
        return true;
    }

}

