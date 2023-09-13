package com.mist.cloud.module.transmit.service;

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
    void uploadSingleFile(Long folderId, MultipartFile file);

    /**
     * 上传文件夹
     * @param parentId
     * @param pathSet
     * @return
     */
    Map<String, Long> uploadFolder(Long parentId, Set<String> pathSet);

}
