package com.mist.cloud.module.file.service.impl;

import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.service.FileServiceSupport;
import com.mist.cloud.module.file.service.IFileStrategy;
import com.mist.cloud.infrastructure.entity.Folder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.dev33.satoken.stp.StpUtil.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:19
 * @Description:
 */
@Service("folderService")
public class FolderServiceImpl extends FileServiceSupport {

    @Override
    public void rename(Long id, String name) {
        folderRepository.renameFolder(id, name);
    }

    @Override
    public void copy(Long id, Long targetFolderId) throws FolderException {
        // 判断目标文件夹内是否已存在同名的文件夹
        List<Folder> subFolders = folderRepository.findSubFolders(targetFolderId);
        Folder folder = folderRepository.findFolder(id);

        if (folder != null) {
            for (Folder subFolder : subFolders) {
                if (subFolder.getName().equals(folder.getName())) {
                    throw new FolderException("文件夹已存在");
                }
            }
        }

        copyFolderRecur(id, targetFolderId);
    }

    @Override
    public void delete(Long id, boolean realDelete) {
        /**
         * 递归删除该文件夹下所有的子文件夹和文件
         * 这个 sql 语句很复杂，用到了自定函数，具体的放在 doc 目录下了
         * 这个 sql 放在 doc 目录下了
         */
        if (realDelete) {
            folderRepository.realDeleteFolderRecursive(id);
        } else {
            folderRepository.deleteFolderRecursive(id);
        }
    }


    @Override
    public String getPath(Long id) {
        List<FolderDetail.FolderPathItem> pathList = super.getPathList(id);
        StringBuffer path = new StringBuffer();
        pathList.forEach(item -> {
            path.append("/" + item.getName());
        });
        return path.toString();
    }


    // TODO 这里递归操作数据库，暂时没想到更好的方法
    private void copyFolderRecur(Long folderId, Long targetFolderId) {
        // 复制当前文件夹
        Long newFolderId = folderRepository.copyFolder(folderId, targetFolderId);

        // 复制当前文件夹的子文件
        List<Long> fileIds = folderRepository.findFilesId(folderId);
        for (Long fileId : fileIds) {
            fileRepository.copyFile(fileId, newFolderId);
        }

        // 递归复制所有子文件夹
        List<Long> childFolderIds = folderRepository.findSubFoldersId(folderId);
        for (Long childFolderId : childFolderIds) {
            copyFolderRecur(childFolderId, newFolderId);
        }
    }


}
