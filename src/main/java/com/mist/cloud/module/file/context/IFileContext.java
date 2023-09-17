package com.mist.cloud.module.file.context;

import com.mist.cloud.core.exception.file.BaseFileException;
import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.module.file.model.dto.FolderDto;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.model.tree.FolderTreeNode;
import com.mist.cloud.module.file.context.service.ICommonService;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:17
 * @Description: 包含一些文件与文件夹不通用的操作，例如创建文件夹等操作
 */
public interface IFileContext extends ICommonService {


    /**
     * 对于文件和文件夹的CRUD，获取到各自的service实现类
     *
     * @param id
     * @return
     */
    ICommonService getService(Long id);

    /**
     * 获取文件下的所有文件和文件夹信息
     *
     * @param id
     * @return
     */
    FolderDetail getFolderDetail(Long id);

    /**
     * 搜索文件
     *
     * @param value
     * @return
     */
    FolderDetail searchFile(String value);

    /**
     * 创建文件夹
     *
     * @param parentId   创建文件夹的所在文件夹id
     * @param folderName 名称
     * @return
     * @throws FolderException 文件加下已存在同名文件夹
     */
    FolderDto createFolder(Long parentId, String folderName) throws FolderException;

    /**
     * 获取文件树
     *
     * @return
     */
    FolderTreeNode getFolderTree();

    @Override
    default void rename(Long id, String name) {
        getService(id).rename(id, name);
    }

    @Override
    default void copy(Long id, Long targetFolderId) throws BaseFileException {
        getService(id).copy(id, targetFolderId);
    }

    @Override
    default void delete(Long id, boolean realDelete) {
        getService(id).delete(id, realDelete);
    }

    @Override
    default String getPath(Long id) {
        return getService(id).getPath(id);
    }

}
