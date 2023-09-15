package com.mist.cloud.module.file.service.impl;

import com.mist.cloud.core.exception.file.FileException;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.service.FileServiceSupport;
import com.mist.cloud.module.file.service.IFileStrategy;
import com.mist.cloud.infrastructure.entity.File;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:18
 * @Description:
 */
@Service("fileService")
public class FileServiceImpl extends FileServiceSupport {

    @Override
    public void rename(Long id, String name) {
        super.fileRepository.renameFile(id, name);
    }

    @Override
    public void copy(Long id, Long targetFolderId) throws FileException {
        // 判断移动的目标文件夹是否存在当前文件
        List<File> files = super.folderRepository.findFiles(targetFolderId);

        File file = fileRepository.findFile(id);
        for (File file0 : files) {
            if (file0.getName().equals(file.getName())) {
                throw new FileException("文件已存在");
            }
        }

        fileRepository.copyFile(id, targetFolderId);
    }

    @Override
    public void delete(Long id, boolean realDelete) {

        if (realDelete) {
            fileRepository.realDeleteFile(id);
        } else {
            fileRepository.deleteFile(id);
        }
    }

    @Override
    public String getPath(Long id) {
        File file = fileRepository.findFile(id);
        List<FolderDetail.FolderPathItem> pathList = getPathList(file.getFolderId());
        StringBuffer path = new StringBuffer();

        pathList.forEach(item -> {
            path.append("/" + item.getName());
        });
        path.append("/" + file.getName());
        return path.toString();
    }
}
