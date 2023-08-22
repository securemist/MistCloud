package com.mist.cloud.infrastructure.repository;

import com.mist.cloud.common.config.IdGenerator;
import com.mist.cloud.common.constant.Constants;
import com.mist.cloud.domain.file.model.entity.*;
import com.mist.cloud.domain.file.repository.IFolderRepository;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.infrastructure.DO.Folder;
import com.mist.cloud.infrastructure.dao.FileMapper;
import com.mist.cloud.infrastructure.dao.FolderMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Override
    public Folder findFolder(Long folderId) {
        FolderSelectReq folderSelectReq = FolderSelectReq
                .builder()
                .id(folderId)
                .build();

        Folder folder =  folderMapper.selectFolderById(folderId);
        return folder;
    }


    @Override
    public List<Folder> findSubFolders(Long folderId) {
        FolderSelectReq folderSelectReq = FolderSelectReq.builder()
                .userId(Constants.DEFAULT_USERID)
                .parentId(folderId)
                .build();

        List<Folder> folders = folderMapper.selectSubFolders(folderSelectReq);
        return folders;
    }

    @Override
    public Long createFolder(String folderName, Long parentId) {
        Long id = IdGenerator.fileId();
        FolderSelectReq folderCreateReq = FolderSelectReq.builder()
                .userId(Constants.DEFAULT_USERID)
                .id(id)
                .parentId(parentId)
                .folderName(folderName)
                .build();

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
        FolderSelectReq folderRenametReq = FolderSelectReq.builder()
                .userId(Constants.DEFAULT_USERID)
                .id(folderId)
                .folderName(folderName)
                .build();

        folderMapper.renameFolder(folderRenametReq);
    }

    @Override
    public List<Folder> getFolderTree(Long folderId) {

        LinkedList<Folder> folderTreeList = folderMapper.getFolderTree(folderId);
        return folderTreeList;
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
        FolderCopyReq folderCopyReq = FolderCopyReq.builder()
                .folderId(folderId)
                .newFolderId(newFolderId)
                .targetFolderId(targetFolderId)
                .build();
        folderMapper.copyFolder(folderCopyReq);
        return newFolderId;
    }

    @Override
    public List<Long> findSubFoldersId(Long folderId) {
        List<Folder> subFolders = findSubFolders(folderId);
        List<Long> idList = subFolders.stream()
                .map(folder -> folder.getId())
                .collect(Collectors.toList());
        return idList;
    }

    @Override
    public List<Long> findFilesId(Long folderId) {
        List<File> files = findFiles(folderId);
        List<Long> idList = files.stream()
                .map(file -> file.getId())
                .collect(Collectors.toList());
        return idList;
    }

    @Override
    public void createFolders(List<FolderBrief> folders) {

        folderMapper.createFolders(folders);
    }
}
