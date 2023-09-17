package com.mist.cloud.module.transmit.service;

import cn.hutool.core.io.FileUtil;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.utils.FileUtils;
import com.mist.cloud.core.utils.ZipUtils;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.file.context.service.FileService;
import com.mist.cloud.module.file.context.service.FolderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * @Author: securemist
 * @Datetime: 2023/8/22 13:32
 * @Description:
 */
@Service
public class DownloadContext {
    @Resource
    protected FileConfig fileConfig;
    @Resource
    protected IFileRepository fileRepository;
    @Resource
    protected IFolderRepository folderRepository;
    @Resource
    protected FolderService folderService;
    @Resource
    protected FileService fileService;
    
    public String downloadFolder(Long folderId) {
        // 文件夹名称
        String folderName = folderRepository.findFolder(folderId).getName();
        // 文件下所有的文件
        List<File> fileList = folderRepository.getAllFilesRecursive(folderId);
        // 文件夹详细路径信息
        String folderPath = folderService.getPath(folderId);

        // 在临时目录创建所有的文件夹，将对应的文件从存储区拷贝到下载区
        for (File file : fileList) {
            // 排除掉回收站中的文件
            if(file.getDeleted() == 1) {
                continue;
            }

            String relativePath = file.getRelativePath();
            // 文件在数据库中的逻辑路径 /全部文件/PDF/C++_1.pdf 需要去掉 /全部文件
            String path = fileService.getPath(file.getId());
            String tempPath = fileConfig.getDownloadPath() + path.substring(5, path.length());

            String soucePath = fileConfig.getBasePath() + file.getRelativePath();
            FileUtil.mkParentDirs(tempPath);
            byte[] bytes = FileUtil.readBytes(soucePath); // TODO 这里无法处理超大文件
            FileUtil.writeBytes(bytes, tempPath);
        }

        // 要压缩的原文件夹
        String folderPathWithoutPrefix = folderPath.substring(5, folderPath.length()); // 去掉  `/全部文件`
        String sourcePath = fileConfig.getDownloadPath() + folderPathWithoutPrefix;
        // 压缩文件的目标路径
        String targetZipPath = fileConfig.getDownloadPath() + "/" + folderName + ".zip";
        ZipUtils.zipFolder(sourcePath, targetZipPath);
        // 删除临时文件 /a/b/c ==> /a
        String deletePath = "/" + folderPathWithoutPrefix.split("/")[1];
        FileUtil.del(fileConfig.getDownloadPath() + deletePath);
        return targetZipPath;
    }


    public String downloadFile(Long fileId) {
        File file = fileRepository.findFile(fileId);
        return fileConfig.getBasePath() + file.getRelativePath();
    }


    // 递归创建子文件夹
    public void recur(Long folderId, String currentPath) {
        List<Folder> subFolders = folderRepository.findSubFolders(folderId);
        List<File> files = folderRepository.findFiles(folderId);
        Folder folder = folderRepository.findFolder(folderId);

        String folderPath = fileConfig.getDownloadPath() + currentPath + "/" + folder.getName();
        try {
            // 创建文件夹
            FileUtils.createDirectory(Paths.get(folderPath));
            // 遍历文件列表，创建文件
            for (File file : files) {
                String filePath = folderPath + "/" + file.getName();
                String sourcePath = fileConfig.getBasePath() + "/" + fileRepository.findFile(file.getId()).getOriginName();

                Files.deleteIfExists(Paths.get(filePath));
                // copy资源
                Files.copy(Paths.get(sourcePath), Paths.get(filePath));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //递归创建子文件夹
        for (Folder folder0 : subFolders) {
            recur(folder0.getId(), currentPath + "/" + folder.getName());
        }
    }
}
