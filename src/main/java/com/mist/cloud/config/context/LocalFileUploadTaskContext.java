package com.mist.cloud.config.context;

import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.model.vo.FileInfoVo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.mist.cloud.utils.Session.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 14:17
 * @Description: 所有文件和文件夹的存储采用本地存储
 */
@Component
public class LocalFileUploadTaskContext extends AbstractUploadContext implements UploadTaskContext {
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
    protected void writeChunk(ChunkVo chunkVo) throws IOException {

    }

    @Override
    public void setTaskInfo(FileInfoVo[] fileInfoList) throws FileUploadException {
        Map<String, Task> uploadContext = getUploadContext();

        for (FileInfoVo fileInfo : fileInfoList) {
            // 文件存储的真实路径   base_path/path.../filename  不带有base_path
            StringBuilder path = new StringBuilder("/");

            String relativePath = fileInfo.getRelativePath();
            // 文件夹中的文件
            if (!relativePath.equals(fileInfo.getFileName())) {
                String substring = relativePath.substring(0, relativePath.lastIndexOf('/'));
                path.append(substring);
            }

            // 设置 task 有关信息，如果获取不到 task 会自动创建(线程安全)
            Task task = getTask(fileInfo.getIdentifier());
            task.setFileName(fileInfo.getFileName());
            task.setMD5(fileInfo.getMd5());
            task.setFileType(fileInfo.getType());
            task.setFolderPath(path + "/" + fileInfo.getIdentifier());
            task.setTargetFilePath(path + "/" + fileInfo.getFileName());
            task.setFolderId(fileInfo.getFolderId());
            task.setFileSize(fileInfo.getTotalSize());
            task.setSetInfo(true);
            task.setRelativePath(path.toString());
            if (task.uploadChunks == null) {
                task.uploadChunks = new boolean[fileInfo.getTotalChunks() + 1];
            }

            uploadContext.put(fileInfo.getIdentifier(), task);
        }

        setUploadContext(uploadContext);
    }

//    @Override
    public String generatePath(ChunkVo chunk) throws FileUploadException {
        StringBuilder sb = new StringBuilder();

        String relativePath = chunk.getRelativePath();
        sb.append(fileConfig.getBase_path());
        // 真实路径与文件名不相同，说明是上传的是文件夹中的文件，需要手动拼接文件夹路径
        if(!relativePath.equals(chunk.getFilename())){
            String[] splits = relativePath.split("/");
            for (int i = 0; i < splits.length - 1; i++) {
                sb.append("/").append(splits[i]);
            }
        }

        sb.append("/").append(chunk.getIdentifier());

        //判断路径是否存在，不存在则创建
        if (!Files.isWritable(Paths.get(sb.toString()))) {
            try {
                Files.createDirectories(Paths.get(sb.toString()));
            } catch (IOException e) {
                throw new FileUploadException("File chunk's folder create failed :" + sb , chunk.getIdentifier(), e);
            }
        }
        return sb.append("/")
                .append(chunk.getFilename())
                .append("-")
                .append(chunk.getChunkNumber()).toString();
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

