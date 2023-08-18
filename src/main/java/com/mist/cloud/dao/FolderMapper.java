package com.mist.cloud.dao;

import com.mist.cloud.model.po.Folder;
import com.mist.cloud.model.pojo.FileSelectReq;
import com.mist.cloud.model.pojo.FolderSelectReq;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/19 10:57
 * @Description:
 */
public interface FolderMapper {
    List<Folder> selectFolders(FolderSelectReq folderSelectReq);

    Folder selectSingleFolder(FolderSelectReq folderSelectReq);

    int folderExist(FolderSelectReq folderSelectReq);


    Long getRootDirFolderSize(Long UserId);


    int checkParentIdExist(FolderSelectReq folderSelectReq);

    void renameFolder(FolderSelectReq folderSelectReq);


    void deleteFolderRecursive(Long folderId);

    LinkedList<Folder> getFolderTree(Long rootDirId);

    void createFolder(FolderSelectReq folderSelectReq);

    List<Folder> getFolderPath(Long folderId);

    int existFile(Long fileId, Long folderId);

    int existFolder(Long folderId, Long targetFolderId);

    void copyFolder(Long folderId, Long newFolderId, Long targetFolderId);

    int getChildNums(Long folderId);

    List<Long> getChildFolderIds(Long folderId);

    void realDeleteFolderRecursive(Long folderId);
}
