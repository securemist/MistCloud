package com.mist.cloud.module.recycle.service;

import cn.hutool.core.io.FileUtil;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.file.context.IFileContext;
import org.springframework.stereotype.Service;
import com.mist.cloud.module.recycle.model.RecycleFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/17 08:52
 * @Description:
 */
@Service
public class RecycleService {
    @Resource
    private IFolderRepository folderRepository;
    @Resource
    private IFileRepository fileRepository;
    @Resource
    private IFileContext fileServiceContext;
    @Resource
    private FileConfig fileConfig;

    /**
     * 列出用户回收站的所有文件
     *
     * @param userId 用户id
     * @return
     */
    public List<RecycleFile> listAll(Long userId) {
        ArrayList<RecycleFile> recycledFileList = new ArrayList<>();

        List<Folder> folderList = folderRepository.getRecycledFolders(userId);
        List<File> fileList = fileRepository.getRecycledFiles(userId);

        for (Folder folder : folderList) {
            RecycleFile recycleFolder = RecycleFile.builder()
                    .id(folder.getId())
                    .size(0L)
                    .isFolder(true)
                    .name(folder.getName())
                    .deletedTime(folder.getDeletedTime())
                    .path(fileServiceContext.getPath(folder.getId())).build();
            recycledFileList.add(recycleFolder);
        }

        for (File file : fileList) {
            RecycleFile recycleFile = RecycleFile.builder()
                    .id(file.getId())
                    .size(file.getSize())
                    .name(file.getName())
                    .isFolder(false)
                    .deletedTime(file.getDeletedTime())
                    .path(fileServiceContext.getPath(file.getId())).build();
            recycledFileList.add(recycleFile);
        }
        return recycledFileList;
    }


    /**
     * 从回收站还原文件
     * @param id
     */
    public void restoreFile(Long id) {
        if (!fileRepository.isFolder(id)) {
            fileRepository.restoreFile(id);
            return;
        }
        folderRepository.restoreFolder(id);
    }


    /**
     * 永久删除文件
     * @param id
     */
    public void deleteFile(Long id) {
        ArrayList<String> fileRealPathList = new ArrayList<>();
        if (!fileRepository.isFolder(id)) {
            File file = fileRepository.findFile(id);
            fileRealPathList.add(fileConfig.getBasePath() + file.getRelativePath());
            fileRepository.realDeleteFile(id);
        } else {
            List<File> fileList = folderRepository.getAllFilesRecursive(id);
            for (File file : fileList) {
                fileRealPathList.add(fileConfig.getBasePath() + file.getRelativePath());
            }
            folderRepository.realDeleteFolderRecursive(id);
        }

        // 真实删除文件
        for (String path : fileRealPathList) {
            FileUtil.del(path);
        }
    }

    /**
     * 清空用户回收站
     * @param userId
     */
    public void clearRecycle(Long userId) {
        for (RecycleFile file : listAll(userId)) {
            deleteFile(file.getId());
        }
    }
}
