package com.mist.cloud.domain.tansmit.service;

import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.domain.file.repository.IFileRepository;
import com.mist.cloud.domain.file.repository.IFolderRepository;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.infrastructure.DO.Folder;
import com.mist.cloud.domain.file.model.entity.FolderDetail;
import com.mist.cloud.domain.file.service.IFileService;
import com.mist.cloud.domain.file.service.IFolderService;

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
