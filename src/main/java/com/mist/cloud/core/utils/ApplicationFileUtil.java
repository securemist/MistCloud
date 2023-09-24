package com.mist.cloud.core.utils;

import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/22 18:37
 * @Description:
 */
@Component
public class ApplicationFileUtil {
    @Resource
    private IFolderRepository folderRepository;
    @Resource
    private IFileRepository fileRepository;

    /**
     * 校验文件名
     * 如果当前文件夹下已经有一个同名的文件，将会赋予新的文件名，根据重名的次数添加后缀
     *
     * @param fileName 原有的文件名
     * @param folderId 所处文件夹 id
     * @return 校验之后的文件名
     */
    public String checkFileName(String fileName, Long folderId) {
        File file = folderRepository.findFile(folderId, fileName);
        // 没有发生重名，返回原文件名
        if (file == null) {
            return fileName;
        }

        // 新的后缀名 _1  _2 ......
        String suffix = String.valueOf(file.getDuplicateTimes() + 1);

        // 得到文件的新名字
        int index = fileName.lastIndexOf(".");
        String name; // 文件名
        String extentionName = "";

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


}
