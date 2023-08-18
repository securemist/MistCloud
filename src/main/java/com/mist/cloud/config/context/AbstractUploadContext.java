package com.mist.cloud.config.context;

import com.mist.cloud.config.FileConfig;
import com.mist.cloud.dao.FileMapper;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.model.vo.FileInfoVo;
import com.mist.cloud.service.IFileService;
import com.mist.cloud.service.impl.FileServiceImpl;
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

    @Resource
    IFileService fileService;

    protected abstract Map<String, Task> getUploadContext();

    protected abstract void setUploadContext(Map<String, Task> taskMap);

    @Override
    public void addChunk(ChunkVo chunk) {
        Map<String, Task> uploadContext = getUploadContext();
        Task task = getTask(chunk.getIdentifier());

        // 在第一次创建 task 的时候并不会创建 uploadChunks 数组，需要在上传分片的时候创建
        if(task.uploadChunks == null){
            synchronized (DefaultUploadTaskContext.class){
                if(task.uploadChunks == null){
                    task.uploadChunks = new boolean[chunk.getTotalChunks() + 1];
                }
            }
        }
        task.uploadChunks[chunk.getChunkNumber()] = true;

        uploadContext.put(chunk.getIdentifier(), task);
        setUploadContext(uploadContext);
    }


    @Override
    public boolean completeTask(String identifier, String md5) throws FileUploadException {
        Task task = getUploadContext().get(identifier);

        if (task == null) {
            throw new FileUploadException("file upload complete error because task is null, file identifier ", identifier);
        }

        // md5 不匹配，上传失败 s
        if (!task.getMD5().equals(md5)) {
            return false;
        }

        // 上传成功
        Map<String, Task> uploadContext = getUploadContext();
        uploadContext.remove(identifier);

        // 数据库添加记录
        fileService.addFile(task);

        setUploadContext(uploadContext);
        return true;
    }


    @Override
    public void cancelTask(String identifier) throws FileUploadException {

        Map<String, Task> uploadContext = getUploadContext();
        Task task = uploadContext.remove(identifier);

        // 取消上传的请求，获取不到上传任务信息
        if (task == null) {
            throw new FileUploadException("cancel upload task error beacuse task is null", identifier);
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
        task.setFolderId(fileInfo.getFolderId());
        task.setFileSize(fileInfo.getTotalSize());

        uploadContext.put(fileInfo.getIdentifier(), task);
        setUploadContext(uploadContext);
    }
}
