package com.mist.cloud.domain.file.repository;

import com.mist.cloud.domain.file.model.entity.FolderBrief;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.infrastructure.DO.Folder;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 15:01
 * @Description:
 */
public interface IFolderRepository {
    Folder findFolder(Long folderId);

    List<Folder> findSubFolders(Long folderId);

    Long createFolder(String folderName, Long parentId);

    void realDeleteFolderRecursive(Long folderId);

    void deleteFolderRecursive(Long folderId);

    void renameFolder(Long folderId, String folderName);

    List<Folder> getFolderTree(Long folderId);

    /**
     * 获取文件夹下所有的文件
     * @param folderId
     * @return
     */
    List<File> findFiles(Long folderId);

    /**
     * 获取文件夹所在路径的所有文件夹
     * @param folderId
     * @return
     */
    List<Folder> getFolderPath(Long folderId);

    /**
     * 文件夹复制, 这里只负责数据库记录虚拟层面的复制，真实的复制由 service 层实现
     *
     * @param folderId       原文件夹 id
     * @param targetFolderId 目标文件夹 id
     * @return
     */
    Long copyFolder(Long folderId, Long targetFolderId);

    /**
     * 查找文件夹下所有子文件夹的 id
     * @param folderId
     * @return
     */
    List<Long> findSubFoldersId(Long folderId);

    /**
     * 查找文件夹下所有文件的 id
     * @param folderId
     * @return
     */
    List<Long> findFilesId(Long folderId);

    /**
     * 批量创建文件夹，主要用于递归创建文件夹
     * @param folders
     */
    void createFolders(List<FolderBrief> folders);

    /**
     * 根据文件夹名称模糊查询
     * @param value 查询关键字
     * @return
     */
    List<Folder> searchByName(String value);
}
