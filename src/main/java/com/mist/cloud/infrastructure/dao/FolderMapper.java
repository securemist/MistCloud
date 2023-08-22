package com.mist.cloud.infrastructure.dao;

import com.mist.cloud.domain.file.model.entity.FolderBrief;
import com.mist.cloud.domain.file.model.entity.FolderCopyReq;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.infrastructure.DO.Folder;
import com.mist.cloud.domain.file.model.entity.FolderSelectReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/19 10:57
 * @Description:
 */
@Mapper
public interface FolderMapper {

    Folder selectFolderById(Long folderId);

    List<Folder> selectSubFolders(FolderSelectReq folderSelectReq);

    void renameFolder(FolderSelectReq folderSelectReq);


    LinkedList<Folder> getFolderTree(Long folderId);

    void createFolder(FolderSelectReq folderSelectReq);

    List<Folder> getFolderPath(Long folderId);


    void deleteFolderRecursive(Long folderId);

    void realDeleteFolderRecursive(Long folderId);

    /**
     * 批量创建文件夹，用于文件夹递归创建
     * @param folders
     */
    void createFolders(List<FolderBrief> folders);



    /**
     * 复制文件夹，数据库记录层面的，实际的逻辑由 service 层完成
     * @param folderCopyReq
     */
    void copyFolder(FolderCopyReq folderCopyReq);


}
