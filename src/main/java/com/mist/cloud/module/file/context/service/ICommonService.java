package com.mist.cloud.module.file.context.service;

import com.mist.cloud.core.exception.file.FileException;
import com.mist.cloud.core.exception.file.FolderException;

/**
 * @Author: securemist
 * @Datetime: 2023/9/17 09:44
 * @Description: 文件与文件夹CRUD通用的CRUD接口
 */
public interface ICommonService {
    /**
     * 文件与文件夹的重命名
     *
     * @param id
     * @param name
     */
    void rename(Long id, String name);

    /**
     * 文件与文件夹的复制
     *
     * @param id             文件id
     * @param targetFolderId 目标文件夹id
     * @throws FileException
     * @throws FolderException
     */
    void copy(Long id, Long targetFolderId) ;

    /**
     * 文件与文件夹的删除
     *
     * @param id         文件id
     * @param realDelete 是否真的删除，false：逻辑删除，true：真实删除
     */
    void delete(Long id, boolean realDelete);

    /**
     * 获取文件或文件夹的路径
     *
     * @param id
     * @return
     */
    String getPath(Long id);


}
