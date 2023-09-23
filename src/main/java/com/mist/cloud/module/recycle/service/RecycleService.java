package com.mist.cloud.module.recycle.service;

import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.core.utils.ApplicationFileUtil;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.file.context.IFileContext;
import com.mist.cloud.module.recycle.model.RestoreFileRequest;
import com.mist.cloud.module.user.repository.IUserRepository;
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
    private IUserRepository userRepository;
    @Resource
    private FileConfig fileConfig;
    @Resource
    private ApplicationFileUtil applicationFileUtil;

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
            RecycleFile recycleFolder = RecycleFile.builder().id(folder.getId()).size(0L).isFolder(true)
                    .name(folder.getName()).deletedTime(folder.getDeletedTime())
                    .path(fileServiceContext.getPath(folder.getId())).build();
            recycledFileList.add(recycleFolder);
        }

        for (File file : fileList) {
            RecycleFile recycleFile = RecycleFile.builder().id(file.getId()).size(file.getSize()).name(file.getName())
                    .isFolder(false).deletedTime(file.getDeletedTime()).path(fileServiceContext.getPath(file.getId()))
                    .build();
            recycledFileList.add(recycleFile);
        }
        return recycledFileList;
    }


    /**
     * 从回收站还原文件
     *
     * @param id
     */
    public void restoreFile(Long id) {
        // 恢复到根目录
        Long targetFolderId = userRepository.getRootFolderId(Session.getLoginId());

        if (!fileRepository.isFolder(id)) {
            // 校验恢复的目标文件夹有没有重名文件
            File file = fileRepository.findFile(id);
            String fileName = applicationFileUtil.checkFileName(file.getName(), targetFolderId);

            RestoreFileRequest restoreFileRequest = RestoreFileRequest.builder()
                    .sourceId(id)
                    .fileName(fileName)
                    .targetFolderId(targetFolderId).build();

            fileRepository.restoreFile(restoreFileRequest);
            return;
        }

        String folderName = folderRepository.findFolderContainRecycled(id).getName();

        boolean exist = folderRepository.existSameNameFolder(targetFolderId, folderName);
        if (exist) {
            throw new FolderException("文件夹已存在，无法还原");
        }
        folderRepository.restoreFolder(id);
    }


    /**
     * 永久删除文件
     *
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
        // for (String path : fileRealPathList) {
        // FileUtil.del(path);
        // }
    }

    /**
     * 清空用户回收站
     *
     * @param userId
     */
    public void clearRecycle(Long userId) {
        for (RecycleFile file : listAll(userId)) {
            deleteFile(file.getId());
        }
    }
}
