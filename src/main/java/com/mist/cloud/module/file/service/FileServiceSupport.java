package com.mist.cloud.module.file.service;

import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:45
 * @Description:
 */
@Service
public class FileServiceSupport {
    @Resource
    public IFolderRepository folderRepository;
    @Resource
    public IFileRepository fileRepository;
}
