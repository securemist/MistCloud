package com.mist.cloud.module.transmit.service;

import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.context.Task;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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
     * 上传文件夹中创建所有的子文件夹
     *
     * @param strings  文件夹下所有的文件的路径
     * @param folderId
     * @return
     */
    Map<String, Long> createSubFolders(Map<String, String> strings, Long folderId) throws FileUploadException;

    void mergeFiles(HashMap<String, String> identifierMap, Map<String, Long> idMap) throws FileUploadException;

}
