package com.mist.cloud.domain.file.service;

import com.mist.cloud.common.exception.file.FolderException;
import com.mist.cloud.domain.file.model.tree.FolderTreeNode;
import com.mist.cloud.domain.file.model.dto.FolderDto;
import com.mist.cloud.domain.file.model.dto.UserCapacityDto;
import com.mist.cloud.infrastructure.DO.Folder;
import com.mist.cloud.domain.file.model.entity.FolderDetail;
import com.mist.cloud.domain.tansmit.service.impl.UploadServiceImpl;

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

}
