package com.mist.cloud.service;

import com.mist.cloud.exception.file.FileCommonException;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 18:51
 * @Description:
 */
public interface IFileService {

    /**
     * 用户上传文件
     *
     * @param file     文件
     * @param folderId 文件夹 Id
     */
    void insertFile(MultipartFile file, Long folderId);


    /**
     * 下载文件
     *
     * @param fileId
     * @return 真实的文件名
     */
    String downloadAndGetName(Long fileId);

    /**
     * 文件重命名
     * @param fileId 文件 id
     * @param fileName 新名字
     */
    void renameFile(Long fileId, String fileName);

    /**
     * 删除文件
     *
     * @param fileId
     * @param realDelete
     */
    void deleteFile(Long fileId, Boolean realDelete);

    /**
     * 复制文件
     * @param fileId 文件 id
     * @param targetFolderId 目标文件夹 id
     */
    void copyFile(Long fileId, Long targetFolderId) throws FileCommonException;
}
