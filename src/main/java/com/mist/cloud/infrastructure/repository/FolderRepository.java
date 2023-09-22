package com.mist.cloud.infrastructure.repository;

import com.mist.cloud.core.config.IdGenerator;
import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.infrastructure.mapper.UserMapper;
import com.mist.cloud.infrastructure.pojo.FolderCopyReq;
import com.mist.cloud.infrastructure.pojo.FolderResaveReq;
import com.mist.cloud.infrastructure.pojo.FolderSelectReq;
import com.mist.cloud.module.file.model.pojo.*;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.infrastructure.mapper.FileMapper;
import com.mist.cloud.infrastructure.mapper.FolderMapper;
import com.mist.cloud.module.user.repository.IUserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 16:21
 * @Description:
 */
@Component
public class FolderRepository implements IFolderRepository {
    @Resource
    private FolderMapper folderMapper;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private IUserRepository userRepository;

    @Cacheable(value = "folder", key = "#folderId")
    @Override
    public Folder findFolder(Long folderId) {
        FolderSelectReq folderSelectReq = FolderSelectReq.builder().id(folderId).build();

        Folder folder = folderMapper.selectFolderById(folderId);
        return folder;
    }

    @Override
    public List<Folder> findSubFolders(Long folderId) {
        List<Folder> folders = folderMapper.selectSubFolders(folderId);
        return folders;
    }

    @Override
    public Long createFolder(String folderName, Long parentId) {
        Long id = IdGenerator.fileId();
        FolderSelectReq folderCreateReq = FolderSelectReq.builder().userId(Session.getLoginId()).id(id)
                .parentId(parentId).folderName(folderName).build();

        folderMapper.createFolder(folderCreateReq);

        return id;
    }


    @Override
    public void realDeleteFolderRecursive(Long folderId) {
        folderMapper.realDeleteFolderRecursive(folderId);
    }

    @Override
    public void deleteFolderRecursive(Long folderId) {
        folderMapper.deleteFolderRecursive(folderId);
    }

    @Override
    public void renameFolder(Long folderId, String folderName) {
        FolderSelectReq folderRenametReq = FolderSelectReq.builder().userId(Session.getLoginId()).id(folderId)
                .folderName(folderName).build();

        folderMapper.renameFolder(folderRenametReq);
    }

    @Override
    public List<Folder> getFolderTree(Long userId) {
        Long rootFolderId = userMapper.selectUserById(userId).getRootFolderId();
        List<Folder> folderTreeList = folderMapper.getFolderTree(rootFolderId);
        return folderTreeList;
    }

    @Override
    public List<File> getAllFilesRecursive(Long folderId) {
        // 递归获取文件夹所有的子文件夹
        List<Folder> folderList = folderMapper.getFolderTree(folderId);
        List<File> fileList = new ArrayList<>();
        // 遍历所有子文件夹，找到各自所有的文件
        for (Folder folder : folderList) {
            // 忽略已经删除了的文件
            List<File> files = fileMapper.findFilesIncludeRecycled(folder.getId());
            fileList.addAll(files);
        }

        return fileList;
    }


    @Override
    public List<Folder> getRecycledFolders(Long userId) {
        return folderMapper.selectRecycleFolders(userId);
    }

    @Override
    public void restoreFolder(Long id) {
        Long rootFolderId = userRepository.getRootFolderId(Session.getLoginId());
        folderMapper.restoreFolderRecursive(id, rootFolderId);
    }

    @Override
    public List<File> findFiles(Long folderId) {
        List<File> files = fileMapper.selectFilesByFolderId(folderId);
        return files;
    }

    @Override
    public List<Folder> getFolderPath(Long folderId) {
        List<Folder> folderPath = folderMapper.getFolderPath(folderId);
        return folderPath;
    }

    @Override
    public Long copyFolder(Long folderId, Long targetFolderId) {
        Long newFolderId = IdGenerator.fileId();
        FolderCopyReq folderCopyReq = FolderCopyReq.builder().folderId(folderId).newFolderId(newFolderId)
                .targetFolderId(targetFolderId).build();
        folderMapper.copyFolder(folderCopyReq);
        return newFolderId;
    }

    @Override
    public List<Long> findSubFoldersId(Long folderId) {
        List<Folder> subFolders = findSubFolders(folderId);
        List<Long> idList = subFolders.stream().map(folder -> folder.getId()).collect(Collectors.toList());
        return idList;
    }

    @Override
    public List<Long> findFilesId(Long folderId) {
        List<File> files = findFiles(folderId);
        List<Long> idList = files.stream().map(file -> file.getId()).collect(Collectors.toList());
        return idList;
    }

    @Override
    public void createFolders(List<FolderBrief> folders) {
        folderMapper.createFolders(folders);
    }

    @Override
    public List<Folder> searchByName(String value) {
        return folderMapper.search(value);
    }

    @Override
    public Long resaveFolder(Long folderId, Long targetFolderId, Long userId) {
        Long newFolderId = IdGenerator.fileId();
        FolderResaveReq folder = FolderResaveReq.builder().userId(userId).folderId(folderId)
                .targetFolderId(targetFolderId).newFolderId(newFolderId).build();
        folderMapper.resaveFolder(folder);
        return newFolderId;
    }

    @Override
    public File findFile(Long folderId, String fileName) {
        List<File> files = findFiles(folderId);
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    @Override
    public boolean existSameNameFolder(Long folderId, String name) {
        List<Folder> folders = findSubFolders(folderId);
        for (Folder folder : folders) {
            if (folder.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Folder findFolderContainRecycled(Long folderId) {
        return folderMapper.selectFolderContainRecycled(folderId);
    }
}
