package com.mist.cloud.module.transmit.context.support;

import com.mist.cloud.core.config.IdGenerator;
import com.mist.cloud.core.utils.ApplicationFileUtil;
import com.mist.cloud.core.utils.FileUtils;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.transmit.context.Task;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 20:37
 * @Description:
 */
@Component
public class DatabaseSupport {
    @Resource
    private ApplicationFileUtil applicationFileUtil;
    @Resource
    private IFileRepository fileRepository;

    public void addChunkableFile(Task task) {
        // 校验文件名。防止重名
        String fileName = applicationFileUtil.checkFileName(task.getFileName(), task.getFolderId());

        // 添加记录
        File file = File.builder()
                .id(IdGenerator.fileId())
                .name(fileName)
                .size(task.getFileSize())
                .type(FileUtils.getFileType(fileName))
                .folderId(task.getFolderId())
                .originName(task.getFileName())
                .md5(task.getMD5())
                .userId(Session.getLoginId())
                .relativePath(task.getRelativePath())
                .build();

        // 添加记录
        fileRepository.addFile(file);
    }

    public void addSimpleFile(Long folderId, String readPath, MultipartFile file) {
        String fileName =  applicationFileUtil.checkFileName(file.getOriginalFilename(), folderId);
        // 所有的单文件上传全部上传到根路径
        File newFile = File.builder()
                .id(IdGenerator.fileId())
                .userId(Session.getLoginId())
                .name(fileName)
                .size(file.getSize())
                .type(FileUtils.getFileType(file.getName()))
                .folderId(folderId)
                .relativePath(readPath)
                .originName(file.getOriginalFilename())
                .md5("")
                .build();

        fileRepository.addFile(newFile);
    }



}
