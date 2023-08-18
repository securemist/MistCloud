package com.mist.cloud.service.impl;

import com.mist.cloud.config.IdGenerator;
import com.mist.cloud.dao.FileMapper;
import com.mist.cloud.dao.FolderMapper;
import com.mist.cloud.exception.file.FileCommonException;
import com.mist.cloud.exception.file.FileException;
import com.mist.cloud.exception.RequestParmException;
import com.mist.cloud.model.po.File;
import com.mist.cloud.model.pojo.FileSelectReq;
import com.mist.cloud.service.IFileService;
import com.mist.cloud.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 18:53
 * @Description:
 */
@Service
public class FileServiceImpl implements IFileService {
    @Resource
    private FileMapper fileMapper;

    @Resource
    private FolderMapper folderMapper;


    @Override
    @Transactional
    public void insertFile(MultipartFile file, Long folderId) {
        // 查看是否同名
        FileSelectReq fileSelectReq = FileSelectReq
                .builder()
                .fileName(file.getOriginalFilename())
                .folderId(folderId)
                .build();

        String originFileName = file.getOriginalFilename();
        String fileName = originFileName;

        File rFile = fileMapper.getSingleFile(fileSelectReq);
        Long baseFileId = 0L;
        if (rFile != null) { // 重名
            // 更新文件名
            String suffix = String.valueOf(rFile.getDuplicateTimes() + 1);
            // 得到文件的新名字
            fileName = getFileNewName(file.getOriginalFilename(), suffix);
            // 更新重名次数
            fileMapper.updateFileDuplicateTimes(fileSelectReq);
            baseFileId = rFile.getId();
        }

        // 真实的文件 ID
        File newFile = new File()
                .builder()
                .id(IdGenerator.fileId())
                .name(fileName)
                .size(file.getSize())
                .type(FileUtils.getFileType(file))
                .folderId(folderId)
                .originName(originFileName)
                .build();

        // 添加一行数据
        fileMapper.addFile(newFile);
        // 更新文件夹大小
        folderMapper.addFolderSize(fileSelectReq);
    }

    private String getFileNewName(String originalName, String suffix) {
        // 判断文件名有没有后缀
        int index = originalName.lastIndexOf(".");
        String name; // 文件名
        String suffixName = ""; // 后缀

        if (index == -1) { // 文件没有后缀
            name = originalName;
        } else {
            name = originalName.substring(0, index);
            suffixName = originalName.substring(index, originalName.length());
        }

        // 添加后缀 _1 / _2 并合并文件名
        return name + "_" + suffix + suffixName;
    }


    @Override
    public String downloadAndGetName(Long fileId) {
        FileSelectReq fileSelectReq = FileSelectReq
                .builder()
                .id(fileId)
                .build();

        File rFile = fileMapper.getSingleFile(fileSelectReq);
        if(rFile == null) { // 传入的文件 id 有误
            throw new RequestParmException();
        }

        // TODO 添加下载文件的记录

        return rFile.getOriginName();
    }

    @Override
    public void renameFile(Long fileId, String fileName) {
        FileSelectReq fileSelectReq = FileSelectReq.builder()
                .fileName(fileName)
                .id(fileId)
                .build();

        fileMapper.renameFile(fileSelectReq);
    }

    @Override
    public void deleteFile(Long fileId, Boolean realDelete) {
        FileSelectReq fileSelectReq = FileSelectReq.builder()
                .id(fileId)
                .build();

        if(realDelete){
            fileMapper.realDeleteFile(fileSelectReq);
        } else {
            fileMapper.deleteFile(fileSelectReq);
        }

    }

    @Override
    public void copyFile(Long fileId, Long targetFolderId) throws FileCommonException {
        // 判断移动的目标文件夹是否存在当前文件
        int r = folderMapper.existFile(fileId, targetFolderId);

        if( r != 0) { // 已存在
            throw new FileCommonException("文件已存在");
        }

        Long newFileId = IdGenerator.fileId();
        fileMapper.copyFile(newFileId, fileId, targetFolderId);
    }
}
