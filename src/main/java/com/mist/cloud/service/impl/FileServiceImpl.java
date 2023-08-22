package com.mist.cloud.service.impl;

import com.mist.cloud.config.IdGenerator;
import com.mist.cloud.config.context.Task;
import com.mist.cloud.dao.FileMapper;
import com.mist.cloud.dao.FolderMapper;
import com.mist.cloud.exception.file.FileCommonException;
import com.mist.cloud.model.po.File;
import com.mist.cloud.model.pojo.FileSelectReq;
import com.mist.cloud.service.IFileService;
import com.mist.cloud.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void addFile(Task task) {
        String fileName = this.checkFileName(task.getFileName(), task.getFolderId());

        // 添加记录
        File newFile = File.builder()
                .id(IdGenerator.fileId())
                .name(fileName)
                .size(task.getFileSize())
                .type(FileUtils.getFileType(fileName))
                .folderId(task.getFolderId())
                .originName(task.getFileName())
                .md5(task.getMD5())
                .build();


        // 添加记录
        fileMapper.addFile(newFile);
    }

    /**
     * 校验文件名
     * 如果当前文件夹下已经有一个同名的文件，将会赋予新的文件名，根据重名的次数添加后缀
     *
     * @param fileName 原有的文件名
     * @param folderId 所处文件夹 id
     * @return
     */
    private String checkFileName(String fileName, Long folderId) {
        // 查看是否同名
        FileSelectReq fileSelectReq = FileSelectReq
                .builder()
                .fileName(fileName)
                .folderId(folderId)
                .build();

        File rFile = fileMapper.getSingleFile(fileSelectReq);

        // 没有发生重名
        if (rFile == null) {
            return fileName;
        }

        // 新的后缀名 _1  _2 ......
        String suffix = String.valueOf(rFile.getDuplicateTimes() + 1);

        // 得到文件的新名字
        int index = fileName.lastIndexOf(".");
        String name; // 文件名
        String extentionName = ""; // 文件扩展名

        if (index == -1) { // 文件没有后缀
            name = fileName;
        } else {
            name = fileName.substring(0, index);
            extentionName = fileName.substring(index, fileName.length());
        }

        // 添加后缀 _1 / _2 并合并文件名
        fileName = name + "_" + suffix + extentionName;

        // 更新重名次数
        fileMapper.updateFileDuplicateTimes(fileSelectReq);

        return fileName;
    }

//    @Override
//    @Transactional
//    public void insertFile(MultipartFile file, Long folderId) {
//        // 查看是否同名
//        FileSelectReq fileSelectReq = FileSelectReq
//                .builder()
//                .fileName(file.getOriginalFilename())
//                .folderId(folderId)
//                .build();
//
//        String originFileName = file.getOriginalFilename();
//        String fileName = originFileName;
//
//        File rFile = fileMapper.getSingleFile(fileSelectReq);
//        Long baseFileId = 0L;
//        if (rFile != null) { // 重名
//            // 更新文件名
//            String suffix = String.valueOf(rFile.getDuplicateTimes() + 1);
//            // 得到文件的新名字
//            fileName = getFileNewName(file.getOriginalFilename(), suffix);
//            // 更新重名次数
//            fileMapper.updateFileDuplicateTimes(fileSelectReq);
//            baseFileId = rFile.getId();
//        }
//
//        // 真实的文件 ID
//        File newFile = new File()
//                .builder()
//                .id(IdGenerator.fileId())
//                .name(fileName)
//                .size(file.getSize())
//                .type(FileUtils.getFileType(file))
//                .folderId(folderId)
//                .originName(originFileName)
//                .build();
//
//        // 添加一行数据
//        fileMapper.addFile(newFile);
//        // 更新文件夹大小
//        folderMapper.addFolderSize(fileSelectReq);
//    }

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
        File file = getFile(fileId);
        // TODO 添加下载文件的记录
        return file.getOriginName();
    }

    @Override
    public File getFile(Long fileId){
        FileSelectReq fileSelectReq = FileSelectReq
                .builder()
                .id(fileId)
                .build();

        File rFile = fileMapper.getSingleFile(fileSelectReq);
        return rFile;
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

        if (realDelete) {
            fileMapper.realDeleteFile(fileSelectReq);
        } else {
            fileMapper.deleteFile(fileSelectReq);
        }

    }

    @Override
    public void copyFile(Long fileId, Long targetFolderId) throws FileCommonException {
        // 判断移动的目标文件夹是否存在当前文件
        int r = folderMapper.existFile(fileId, targetFolderId);

        if (r != 0) { // 已存在
            throw new FileCommonException("文件已存在");
        }

        Long newFileId = IdGenerator.fileId();
        fileMapper.copyFile(newFileId, fileId, targetFolderId);
    }


}
