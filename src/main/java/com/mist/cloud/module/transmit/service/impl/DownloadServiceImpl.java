package com.mist.cloud.module.transmit.service.impl;

import com.mist.cloud.module.transmit.service.IDownloadService;
import com.mist.cloud.core.utils.FileUtils;
import com.mist.cloud.core.utils.ZipUtils;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.module.transmit.service.TransmitSupport;
import org.springframework.stereotype.Service;

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
public class DownloadServiceImpl extends TransmitSupport implements IDownloadService {

    @Override
    public String downloadFolder(Long folderId) {
        String folderName = folderRepository.findFolder(folderId).getName();
        String source = super.fileConfig.getDownloadPath() + "/" + folderName;
        recur(folderId, "");

        String zipSource = source + ".zip";

        ZipUtils.zipFolder(source, zipSource);
        return zipSource;
    }

    @Override
    public String downloadFile(Long fileId) {
        return null;
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
