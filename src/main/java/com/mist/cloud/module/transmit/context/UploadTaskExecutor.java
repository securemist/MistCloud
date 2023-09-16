package com.mist.cloud.module.transmit.context;

import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 21:52
 * @Description:
 */
@Component("uploadTaskExecutor")
@Slf4j
public class UploadTaskExecutor {

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


    // 生成真实文件的文件名， originName_identifier.xxx 防止覆盖上传
    private String generateRealPath(String relativePath, String identifier) {
        int index = relativePath.lastIndexOf("/");
        // 从路径中截取文件名
        String fileName = "";
        String path = "";
        if (index == -1) { // 单文件上传，文件的 relativePath 为不带有 /
            fileName = relativePath;
        } else {  // 文件夹上传中的文件
            path = relativePath.substring(0, index);
            fileName = relativePath.substring(index + 1, relativePath.length());
        }


        // 拼接新的文件名
        StringBuilder name = new StringBuilder();
        index = fileName.lastIndexOf(".");
        if (index != -1) {
            name = name.append(fileName.substring(0, index)).append("_").append(identifier).append(".").append(fileName.substring(index + 1, fileName.length()));
        } else {
            name = name.append(fileName).append("_").append(identifier);
        }

        String finalPath = new StringBuilder(path).append("/").append(name).toString();
        return finalPath;

    }
}
