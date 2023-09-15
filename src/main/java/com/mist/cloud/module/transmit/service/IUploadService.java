package com.mist.cloud.module.transmit.service;

import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.module.transmit.context.Task;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

/**
 * @Author: securemist
 * @Datetime: 2023/8/27 10:23
 * @Description:
 */
public interface IUploadService {
    /**
     * 上传传文件
     * @param task
     */
    void uploadFile(Task task);


    /**
     * 上传非分片上传的文件
     */
    void uploadSingleFile(Long folderId, MultipartFile file) ;

    /**
     * 上传文件夹
     * @param parentId 目标文件夹
     * @param pathSet 文件夹下所有文件的路径set集合，依次创建需要的文件夹
     * @return
     */
    Map<String, Long> uploadFolder(Long parentId, Set<String> pathSet);

}
