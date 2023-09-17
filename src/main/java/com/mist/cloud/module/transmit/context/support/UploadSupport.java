package com.mist.cloud.module.transmit.context.support;

import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.infrastructure.repository.FolderRepository;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.transmit.context.Task;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 20:38
 * @Description:
 */
public class UploadSupport {
    @Resource
    public FileConfig fileConfig;

    @Resource
    protected IFolderRepository folderRepository;
    @Resource
    protected IFileRepository fileRepository;
}
