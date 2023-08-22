package com.mist.cloud.domain.tansmit.context;

import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.common.exception.file.FileUploadException;
import com.mist.cloud.domain.tansmit.model.vo.ChunkVo;
import com.mist.cloud.domain.file.service.IFileService;
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

    @Resource
    IFileService fileService;

    protected abstract Map<String, Task> getUploadContext();

    protected abstract void setUploadContext(Map<String, Task> taskMap);

    protected abstract void writeChunk(ChunkVo chunkVo) throws IOException;

    @Override
    public void addChunk(ChunkVo chunk) {
        try {
            writeChunk(chunk);
        } catch (IOException e) {
            new FileUploadException("File chunk write failed : " + chunk.getFilename()
                    + "-" + chunk.getChunkNumber(), chunk.getIdentifier(), e);
        }

        Map<String, Task> uploadContext = getUploadContext();

        Task task = null;
        try {
            task = getTask(chunk.getIdentifier());
        } catch (FileUploadException e) {
        }

        // 在第一次创建 task 的时候并不会创建 uploadChunks 数组，需要在上传分片的时候创建
        if (task == null) {
            synchronized (AbstractUploadContext.class) {
                if (task == null) {
                    task = new Task(chunk.getIdentifier(), chunk.getTotalChunks());
                    task.setFileName(chunk.getFilename());

                    task.setFolderPath(chunk.getIdentifier());
                    task.setTargetFilePath(chunk.getFilename());

                    task.setFolderId(chunk.getFolderId());
                    task.setFileSize(chunk.getTotalSize());
                    task.setRelativePath("/" + chunk.getRelativePath());
                }
            }
        }
        task.uploadChunks[chunk.getChunkNumber()] = true;

        uploadContext.put(chunk.getIdentifier(), task);
        setUploadContext(uploadContext);
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
