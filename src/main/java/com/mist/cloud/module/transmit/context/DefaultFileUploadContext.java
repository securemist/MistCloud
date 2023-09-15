package com.mist.cloud.module.transmit.context;

import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.mist.cloud.core.utils.Session.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/8/19 14:32
 * @Description: 虚拟文件存储，文件夹的存储关系使用数据库维护，所有文件存在根目录
 */
@Component
public class DefaultFileUploadContext extends AbstractUploadContext implements UploadTaskContext {
    /**
     * 文件上传任务上下文
     * <p>
     * String 用户登录 id
     * Map<String, Task>
     * String 任务唯一标识
     * Task 上传任务对象
     */
    private volatile ConcurrentMap<String, Map<String, Task>> uploadContexts = new ConcurrentHashMap<>();

    @Override
    protected Map<String, Task> getUploadContext() {
        Map<String, Task> taskMap = uploadContexts.get(getLoginId());
        if (taskMap == null) {
            synchronized (LocalFileUploadTaskContext.class) {
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
    protected void writeChunk(ChunkVo chunk) throws IOException {
        StringBuilder path = new StringBuilder(fileConfig.getUploadPath());

        path = path.append("/").append(chunk.getIdentifier());

        // 创建分片文件夹目录
        if (!Files.isWritable(Paths.get(path.toString()))) {
            Files.createDirectories(Paths.get(path.toString()));
        }

        // 创建分片
        path = path.append("/").append(chunk.getFileName())
                .append("-").append(chunk.getChunkNumber());

        Files.deleteIfExists(Paths.get(path.toString()));
        Files.createFile(Paths.get(path.toString()));

        // 写入文件
        byte[] bytes = chunk.getFile().getBytes();
        Files.write(Paths.get(path.toString()), bytes);
    }

    @Override
    public Task getTask(String identifier) throws FileUploadException {
        Map<String, Task> uploadContext = getUploadContext();
        Task task = uploadContext.get(identifier);

        if (task == null) {
            throw new FileUploadException("task is null when get task, check the mergeFile request later than addChunk request", identifier);
        }
        return task;
    }
}
