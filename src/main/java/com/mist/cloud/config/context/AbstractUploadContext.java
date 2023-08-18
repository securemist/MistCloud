package com.mist.cloud.config.context;

import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: securemist
 * @Datetime: 2023/8/18 10:20
 * @Description:
 */
@Slf4j
public abstract class AbstractUploadContext implements UploadTaskContext {

    protected abstract Map<String, Task> getUploadContext();

    protected abstract void setUploadContext(Map<String, Task> taskMap);

    protected abstract Task getTask(ChunkVo chunk);

    protected abstract void updateTask(ChunkVo chunk);

    @Override
    public abstract boolean SetMD5(ChunkVo chunk, String md5);

    @Override
    public void addChunk(ChunkVo chunk) {
        updateTask(chunk);
    }

    @Override
    public boolean checkChunkUploaded(ChunkVo chunk) {
        // 获取 boolean 数组判断该分片是否上传
        Task task = getTask(chunk);
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

}
