package com.mist.cloud.module.file.context;

import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.file.context.service.FileService;
import com.mist.cloud.module.file.context.service.FolderService;
import com.mist.cloud.module.file.context.service.ICommonService;
import com.mist.cloud.module.user.repository.IUserRepository;

import javax.annotation.Resource;

import static cn.dev33.satoken.stp.StpUtil.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:45
 * @Description:
 */
public abstract class AbstractFileServiceSupport extends FileContextSupport implements IFileContext {
    @Resource
    protected FileService fileService;
    @Resource
    protected FolderService folderService;

    @Override
    public ICommonService getService(Long id) {
        if (fileRepository.isFolder(id)) {
            return folderService;
        } else {
            return fileService;
        }
    }
}
