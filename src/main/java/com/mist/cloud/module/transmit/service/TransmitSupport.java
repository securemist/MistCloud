package com.mist.cloud.aggregate.tansmit.service;

import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.aggregate.file.repository.IFileRepository;
import com.mist.cloud.aggregate.file.repository.IFolderRepository;
import com.mist.cloud.infrastructure.DO.File;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 13:36
 * @Description:
 */
public class TransmitSupport {
    @Resource
    protected FileConfig fileConfig;
    @Resource
    protected IFileRepository fileRepository;
    @Resource
    protected IFolderRepository folderRepository;

    public File findFile(Long fileId){
        return fileRepository.findFile(fileId);
    }
}
