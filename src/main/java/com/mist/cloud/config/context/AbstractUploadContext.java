package com.mist.cloud.config.context;

import com.mist.cloud.config.FileConfig;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.model.vo.FileInfoVo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
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

//    protected abstract Task getTask(ChunkVo chunk);

    protected abstract void updateTask(ChunkVo chunk);


    @Override
    public void addChunk(ChunkVo chunk) {
        updateTask(chunk);
    }

    @Override
    public boolean checkChunkUploaded(ChunkVo chunk) {
        // 获取 boolean 数组判断该分片是否上传
        Task task = getTask(chunk.getIdentifier());
        if (task == null || task.uploadChunks == null) {
            return false;
        }
        return task.uploadChunks[chunk.getChunkNumber()];
    }

    @Override
    public boolean completeTask(String identifier, String md5) throws FileUploadException {
        Task task = getUploadContext().get(identifier);

        if (task == null) {
            log.error("file upload conplete error because task is null, file identifier: {}", identifier);
            throw new FileUploadException("请求异常", identifier);
        }

        // md5 不匹配，上传失败 s
        if (!task.getMD5().equals(md5)) {
            return false;
        }

        // 上传成功
        Map<String, Task> uploadContext = getUploadContext();
        uploadContext.remove(identifier);
        setUploadContext(uploadContext);
        return true;
    }


    @Override
    public void cancelTask(String identifier) throws FileUploadException {

        Map<String, Task> uploadContext = getUploadContext();
        Task task = uploadContext.remove(identifier);

        // 取消上传的请求，获取不到上传任务信息
        if (task == null) {
            throw new FileUploadException("请求异常", identifier);
        }

        setUploadContext(uploadContext);
    }

    @Override
    public void setTaskInfo(FileInfoVo fileInfo) throws FileUploadException {
        Map<String, Task> uploadContext = getUploadContext();

        // 设置 task 有关信息，如果获取不到 task 会自动创建(线程安全)
        Task task = getTask(fileInfo.getIdentifier());
        task.setFileName(fileInfo.getFileName());
        task.setMD5(fileInfo.getMd5());
        task.setFileType(fileInfo.getType());
        task.setFolderPath(fileConfig.getBase_path() + "/" + fileInfo.getIdentifier());
        task.setTargetFilePath(fileConfig.getBase_path() + "/" + fileInfo.getFileName());
        task.uploadChunks = new boolean[fileInfo.getTotalChunks() + 1];

        uploadContext.put(fileInfo.getIdentifier(), task);
        setUploadContext(uploadContext);
    }
}
