package com.mist.cloud.module.transmit.context;

import com.mist.cloud.module.transmit.context.support.UploaderSupport;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mist.cloud.core.utils.FileUtils.generateRealPath;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 21:52
 * @Description:
 */
@Component("uploadTaskExecutor")
@Slf4j
public class UploadTaskExecutor{

    private volatile Map<String, Task> taskExecutoePool = new ConcurrentHashMap();

    public Task createTask(ChunkVo chunk) {
        Task task = new Task(chunk.getIdentifier(), chunk.getTotalChunks());
        task.setFileName(chunk.getFileName());

        task.setFolderPath("/" + chunk.getIdentifier());
        task.setTargetFilePath(chunk.getFileName());

        task.setFolderId(chunk.getFolderId());
        task.setFileSize(chunk.getTotalSize());
        task.uploadChunks = new boolean[chunk.getTotalChunks() + 1];
        task.setRelativePath(generateRealPath(chunk.getRelativePath(), chunk.getIdentifier()));

        taskExecutoePool.put(chunk.getIdentifier(), task);
        return task;
    }

    public Task getTask(String identifier) {
        return taskExecutoePool.get(identifier);
    }

    public void removeTask(String identifier) {
        taskExecutoePool.remove(identifier);
    }

    public void updateTask(Task task) {
        taskExecutoePool.put(task.getIdentifier(), task);
    }



}
