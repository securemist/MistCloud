package com.mist.cloud.module.transmit.context.support;

import com.mist.cloud.core.config.IdGenerator;
import com.mist.cloud.core.utils.FileUtils;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.context.support.UploadSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 20:37
 * @Description:
 */
@Component
public class DatabaseSupport extends UploadSupport{


    public void addChunkableFile(Task task) {
        // 校验文件名。防止重名
        String fileName = checkFileName(task.getFileName(), task.getFolderId());

        // 添加记录
        File file = File.builder()
                .id(IdGenerator.fileId())
                .name(fileName)
                .size(task.getFileSize())
                .type(FileUtils.getFileType(fileName))
                .folderId(task.getFolderId())
                .originName(task.getFileName())
                .md5(task.getMD5())
                .userId(Session.getLoginId())
                .relativePath(task.getRelativePath())
                .build();

        // 添加记录
        fileRepository.addFile(file);
    }

    public void addSimpleFile(Long folderId, MultipartFile file) {
        String fileName = checkFileName(file.getOriginalFilename(), folderId);
        // 所有的单文件上传全部上传到根路径
        File newFile = File.builder()
                .id(IdGenerator.fileId())
                .userId(Session.getLoginId())
                .name(fileName)
                .size(file.getSize())
                .type(FileUtils.getFileType(file.getName()))
                .folderId(folderId)
                .relativePath("/" + file.getOriginalFilename())
                .originName(file.getOriginalFilename())
                .md5("")
                .build();

        fileRepository.addFile(newFile);
    }


    /**
     * 校验文件名
     * 如果当前文件夹下已经有一个同名的文件，将会赋予新的文件名，根据重名的次数添加后缀
     *
     * @param fileName 原有的文件名
     * @param folderId 所处文件夹 id
     * @return
     */
    public String checkFileName(String fileName, Long folderId) {
        // 查看是否同名
        List<File> files = folderRepository.findFiles(folderId);
        File file = null;
        for (File file0 : files) {
            if (file0.getName().equals(fileName)) {
                file = file0;
            }
        }

        // 没有发生重名
        if (file == null) {
            return fileName;
        }

        // 新的后缀名 _1  _2 ......
        String suffix = String.valueOf(file.getDuplicateTimes() + 1);

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
        fileRepository.updateFileDuplicateTimes(file.getId());

        return fileName;
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
}
