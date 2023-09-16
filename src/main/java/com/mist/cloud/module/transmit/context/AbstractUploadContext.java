package com.mist.cloud.module.transmit.context;

import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: securemist
 * @Datetime: 2023/8/18 10:20
 * @Description:
 */
@Slf4j
public abstract class AbstractUploadContext implements UploadTaskContext {

    @Resource
    FileConfig fileConfig;

    protected abstract Map<String, Task> getUploadContext();

    protected abstract void setUploadContext(Map<String, Task> taskMap);

    protected abstract void writeChunk(ChunkVo chunkVo) throws IOException;

    @Override
    public void addChunk(ChunkVo chunk) throws FileUploadException {
        try {
            writeChunk(chunk);
        } catch (IOException e) {
            new FileUploadException("File chunk write failed : " + chunk.getFileName()
                    + "-" + chunk.getChunkNumber(), chunk.getIdentifier(), e);
        }

        Map<String, Task> uploadContext = getUploadContext();
        Task task = getTask(chunk.getIdentifier());

        // 在第一次创建 task 的时候并不会创建 uploadChunks 数组，需要在上传分片的时候创建
        if (task == null) {
            synchronized (AbstractUploadContext.class) {
                if (task == null) {
                    task = new Task(chunk.getIdentifier(), chunk.getTotalChunks());
                    task.setFileName(chunk.getFileName());

                    task.setFolderPath("/" + chunk.getIdentifier());
                    task.setTargetFilePath(chunk.getFileName());

                    task.setFolderId(chunk.getFolderId());
                    task.setFileSize(chunk.getTotalSize());
                    task.uploadChunks = new boolean[chunk.getTotalChunks() + 1];
                    task.setRelativePath(generateRealPath(chunk.getRelativePath(), chunk.getIdentifier()));
                }
            }
        }
        task.uploadChunks[chunk.getChunkNumber()] = true;// chunk 的排序从 1 开始

        uploadContext.put(chunk.getIdentifier(), task);
        setUploadContext(uploadContext);
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


    @Override
    public void completeTask(String identifier) throws FileUploadException {
        Map<String, Task> uploadContext = getUploadContext();
        Task task = getTask(identifier);

        uploadContext.remove(identifier);
        setUploadContext(uploadContext);
    }


    @Override
    public void cancelTask(String identifier) throws FileUploadException {

        Map<String, Task> uploadContext = getUploadContext();
        Task task = getTask(identifier);

        uploadContext.remove(identifier);
        setUploadContext(uploadContext);
    }

}
