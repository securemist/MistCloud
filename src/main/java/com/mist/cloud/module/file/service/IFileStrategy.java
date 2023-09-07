package com.mist.cloud.aggregate.file.service;

import com.mist.cloud.common.exception.file.BaseFileException;
import com.mist.cloud.common.exception.file.FileException;
import com.mist.cloud.common.exception.file.FolderException;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:17
 * @Description:
 *
 * 文件与文件夹操作的 修改，删除，复制 接口
 */
public interface IFileStrategy {

    /**
     * 文件与文件夹的重命名
     * @param id
     * @param name
     */
    void rename(Long id, String name);

    /**
     * 文件与文件夹的复制
     * @param id 文件id
     * @param targetFolderId 目标文件夹id
     * @throws FileException
     * @throws FolderException
     */
    void copy(Long id, Long targetFolderId) throws BaseFileException;

    /**
     * 文件与文件的删除
     * @param id 文件id
     * @param realDelete 是否真的删除，false：逻辑删除，true：真实删除
     */
    void delete(Long id, boolean realDelete);
}
