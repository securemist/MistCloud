package com.mist.cloud.service;

import com.mist.cloud.exception.file.FolderException;
import com.mist.cloud.model.dto.FolderDto;
import com.mist.cloud.model.dto.UserCapacityDto;
import com.mist.cloud.model.pojo.FolderDetail;
import com.mist.cloud.model.tree.FolderTreeNode;
import com.mist.cloud.service.impl.UploadServiceImpl;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/19 18:46
 * @Description:
 */
public interface IFolderService {
    FolderDto createFolder(Long parentId, String folderName) throws FolderException;

    UserCapacityDto getCapacityInfo();

    void rename(Long folderId, String folderName);

    void deleteFolder(Long folderId, Boolean realDelete);


    FolderTreeNode getFolderTree();

    FolderDetail getFiles(Long folderId);

    void copyFolder(Long folderId, Long targetFolderId) throws FolderException;

    void createFolders(List<UploadServiceImpl.Folder> folderList);

}
