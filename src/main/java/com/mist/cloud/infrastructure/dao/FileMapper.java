package com.mist.cloud.infrastructure.dao;

import com.mist.cloud.domain.file.model.entity.FileCopyReq;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.domain.file.model.entity.FileSelectReq;
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
}
