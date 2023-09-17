package com.mist.cloud.infrastructure.mapper;

import com.mist.cloud.infrastructure.pojo.FileCopyReq;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.pojo.FileSelectReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 14:23
 * @Description:
 */
@Mapper
public interface FileMapper {
    void insertFile(File file);


    void updateFileDuplicateTimes(FileSelectReq fileSelectReq);

    void renameFile(FileSelectReq fileSelectReq);

    int deleteFile(FileSelectReq fileSelectReq);

    void realDeleteFile(FileSelectReq fileSelectReq);

    File selectFileById(Long fileId);

    void copyFile(FileCopyReq fileCopyReq);

    File selectFileByObj(FileSelectReq fileSelectReq);

    List<File> selectFilesByFolderId(Long folderId);

    List<File> search(String value);

    List<File> selectRecycledFiles(Long userId);

    // 复原文件到指定文件夹
    void restoreFile(Long id, Long folderId);

    // 查找文件夹下的所有文件，包括回收站中的文件
    List<File> findFilesIncludeRecycled(Long id);
}
