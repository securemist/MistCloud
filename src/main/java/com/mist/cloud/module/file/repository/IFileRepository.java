package com.mist.cloud.module.file.repository;

import com.mist.cloud.infrastructure.pojo.FileSelectReq;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.module.recycle.model.RestoreFileRequest;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 14:35
 * @Description:
 */
public interface IFileRepository {
    File findFile(Long fileId);

    File findFileByObj(FileSelectReq file);

    /**
     * 添加文件
     * @param file
     */
    void addFile(File file);

    /**
     * 更新文件重名次数
     * @param fileId
     */
    void updateFileDuplicateTimes(Long fileId);

    /**
     * 文件重命名
     * @param fileId
     * @param fileName
     */
    void renameFile(Long fileId, String fileName);

    /**
     * 删除文件，逻辑删除
     * @param fileId
     */
    void deleteFile(Long fileId);

    /**
     * 删除文件，真实删除
     * @param fileId
     */
    void realDeleteFile(Long fileId);

    /**
     * 复制文件
     * @param fileId 原文件 id
     * @param targetFolderId 目标文件夹 id
     */
    void copyFile( Long fileId, Long targetFolderId);

    /**
     * 判断当前id是文件还是文件夹
     * @param fileId
     * @return
     */
    boolean isFolder(Long fileId);

    /**
     * 根据文件名模糊查询
     * @param value 查询关键字
     * @return
     */
    List<File> searchByName(String value);

    /**
     * 获取回收站中的文件，仅为单独删除的文件，排除掉删除文件夹导致的文件删除
     * @param userId
     * @return
     */
    List<File> getRecycledFiles(Long userId);

    /**
     * 从还原文件，还原到根目录
     *
     * @param resaveFileRequest
     */
    void restoreFile(RestoreFileRequest resaveFileRequest);


}
