package com.mist.cloud.config.context;

import cn.hutool.core.text.csv.CsvBaseReader;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.model.vo.FileInfoVo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.mist.cloud.utils.Session.getLoginId;

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
        StringBuilder path = new StringBuilder(fileConfig.getBase_path());

        path = path.append("/").append(chunk.getIdentifier());

        // 创建分片文件夹目录
        if (!Files.isWritable(Paths.get(path.toString()))) {
            Files.createDirectories(Paths.get(path.toString()));
        }

        // 创建分片
        path = path.append("/").append(chunk.getFilename())
                .append("-").append(chunk.getChunkNumber());
        Files.deleteIfExists(Paths.get(path.toString())
        );
        Files.createFile(Paths.get(path.toString()));

        // 写入文件
        byte[] bytes = chunk.getFile().getBytes();
        Files.write(Paths.get(path.toString()), bytes);
    }

    @Override
    public void setTaskInfo(FileInfoVo[] fileInfoList) throws FileUploadException {
        Map<String, Task> uploadContext = getUploadContext();

        for (FileInfoVo fileInfo : fileInfoList) {

            // 设置 task 有关信息，如果获取不到 task 会自动创建(线程安全)
            Task task = getTask(fileInfo.getIdentifier());
            task.setFileName(fileInfo.getFileName());
            task.setMD5(fileInfo.getMd5());
            task.setFileType(fileInfo.getType());

            task.setFolderPath(fileInfo.getIdentifier());
            task.setTargetFilePath(fileInfo.getFileName());

            task.setFolderId(fileInfo.getFolderId());
            task.setFileSize(fileInfo.getTotalSize());
            task.setSetInfo(true);
            task.setRelativePath("/" + fileInfo.getRelativePath());
            if (task.uploadChunks == null) {
                task.uploadChunks = new boolean[fileInfo.getTotalChunks() + 1];
            }

            uploadContext.put(fileInfo.getIdentifier(), task);
        }

        setUploadContext(uploadContext);
    }


    @Override
    public Task getTask(String identifier) {
        Map<String, Task> uploadContext = getUploadContext();

        /**
         * 前端在开始发送文件前会将文件md5 等信息发送过来，会在这时候就创建完毕
         */
        Task task = uploadContext.get(identifier);
        if (task == null) {
            synchronized (LocalFileUploadTaskContext.class) {
                if (task == null) {
                    task = new Task(identifier);
                    uploadContext.put(identifier, task);
                }
            }
        }
        return task;
    }
}
