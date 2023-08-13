package com.mist.cloud.dao;

import com.mist.cloud.model.po.File;
import com.mist.cloud.model.pojo.FileSelectReq;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 14:23
 * @Description:
 */
public interface FileMapper {
    int addFile(File file);

    List<File> getFiles(FileSelectReq fileSelectReq);


    int fileExist(FileSelectReq fileSelectReq);

    File getSingleFile(FileSelectReq fileSelectReq);

    void updateFileDuplicateTimes(FileSelectReq fileSelectReq);

    void renameFile(FileSelectReq fileSelectReq);

    int deleteFile(FileSelectReq fileSelectReq);

    void copyFile(Long newFileId, Long fileId, Long targetFolderId);

    List<Long> getFileIds(Long folderId);

    void realDeleteFile(FileSelectReq fileSelectReq);
}
