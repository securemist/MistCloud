package com.mist.cloud.domain.file.service.impl;

import com.mist.cloud.common.config.IdGenerator;
import com.mist.cloud.domain.file.model.entity.FileCopyReq;
import com.mist.cloud.domain.file.model.entity.FolderDetail;
import com.mist.cloud.domain.file.repository.IFileRepository;
import com.mist.cloud.domain.file.repository.IFolderRepository;
import com.mist.cloud.domain.file.service.IFileService;
import com.mist.cloud.domain.tansmit.context.Task;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.infrastructure.dao.FileMapper;
import com.mist.cloud.infrastructure.dao.FolderMapper;
import com.mist.cloud.common.exception.file.FileCommonException;
import com.mist.cloud.domain.file.model.entity.FileSelectReq;
import com.mist.cloud.common.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 18:53
 * @Description:
 */
@Service
public class FileServiceImpl implements IFileService {
    @Resource
    private IFileRepository fileRepository;
    @Resource
    private IFolderRepository folderRepository;


    @Override
    public String downloadAndGetName(Long fileId) {
        File file = fileRepository.findFile(fileId);
        // TODO 添加下载文件的记录
        return file.getOriginName();
    }


    @Override
    public void renameFile(Long fileId, String fileName) {

        fileRepository.renameFile(fileId, fileName);
    }

    @Override
    public void deleteFile(Long fileId, Boolean realDelete) {
        FileSelectReq fileSelectReq = FileSelectReq.builder()
                .id(fileId)
                .build();

        if (realDelete) {
            fileRepository.realDeleteFile(fileId);
        } else {
            fileRepository.deleteFile(fileId);
        }

    }

    @Override
    public void copyFile(Long fileId, Long targetFolderId) throws FileCommonException {
        // 判断移动的目标文件夹是否存在当前文件
        List<File> files = folderRepository.findFiles(targetFolderId);

        File file = fileRepository.findFile(fileId);
        for (File file0 : files) {
            if (file0.getName().equals(file.getName())) {
                throw new FileCommonException("文件已存在");
            }
        }

        fileRepository.copyFile(fileId, targetFolderId);
    }
}
