package com.mist.cloud.config.context;

import com.mist.cloud.config.FileConfig;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.model.vo.FileInfoVo;
import com.mist.cloud.service.IFileService;
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
        Task task = getTask(chunk.getIdentifier());

        // 在第一次创建 task 的时候并不会创建 uploadChunks 数组，需要在上传分片的时候创建
        if (task.uploadChunks == null) {
            synchronized (AbstractUploadContext.class) {
                if (task.uploadChunks == null) {
                    task.uploadChunks = new boolean[chunk.getTotalChunks() + 1];
                }
            }
        }
        task.uploadChunks[chunk.getChunkNumber()] = true;

        uploadContext.put(chunk.getIdentifier(), task);
        setUploadContext(uploadContext);
    }


    @Override
    public void completeTask(String identifier) throws FileUploadException {
        Task task = getUploadContext().get(identifier);

        if (task == null) {
            throw new FileUploadException("file upload complete error because task is null, file identifier ", identifier);
        }

        // 上传成功
        Map<String, Task> uploadContext = getUploadContext();
        uploadContext.remove(identifier);

        // 数据库添加记录
        fileService.addFile(task);

        setUploadContext(uploadContext);
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
    public abstract void setTaskInfo(FileInfoVo[] fileInfoList) throws FileUploadException;

//    @Override
//    public void setTaskInfo(FileInfoVo[] fileInfoList) throws FileUploadException {
//        Map<String, Task> uploadContext = getUploadContext();
//
//        for (FileInfoVo fileInfo : fileInfoList) {
//            // 文件存储的真实路径   base_path/path.../filename  不带有base_path
//            StringBuilder path = new StringBuilder("/");
//
//            String relativePath = fileInfo.getRelativePath();
//            // 文件夹中的文件
//            if (!relativePath.equals(fileInfo.getFileName())) {
//                String substring = relativePath.substring(0, relativePath.lastIndexOf('/'));
//                path.append(substring);
//            }
//
//            // 设置 task 有关信息，如果获取不到 task 会自动创建(线程安全)
//            Task task = getTask(fileInfo.getIdentifier());
//            task.setFileName(fileInfo.getFileName());
//            task.setMD5(fileInfo.getMd5());
//            task.setFileType(fileInfo.getType());
//            task.setFolderPath(path + "/" +  fileInfo.getIdentifier());
//            task.setTargetFilePath(path + "/" + fileInfo.getFileName());
//            task.setFolderId(fileInfo.getFolderId());
//            task.setFileSize(fileInfo.getTotalSize());
//            task.setSetInfo(true);
//            task.setRelativePath(path.toString());
//            if (task.uploadChunks == null) {
//                task.uploadChunks = new boolean[fileInfo.getTotalChunks() + 1];
//            }
//
//            uploadContext.put(fileInfo.getIdentifier(), task);
//        }
//
//        setUploadContext(uploadContext);
//    }
}
