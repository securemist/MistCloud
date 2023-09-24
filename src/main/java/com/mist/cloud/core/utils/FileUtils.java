package com.mist.cloud.core.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 19:07
 * @Description:
 */
@Slf4j
public class FileUtils {
    // 获取文件类型
    public static String getFileType(String fileName) {

        return Constants.FileType.IMG.getType();
    }

    /**
     * 获取文件后缀名
     *
     * @param file
     * @return
     */
    public static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !originalFilename.isEmpty()) {
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex + 1);
            }
        }
        return null; // 如果文件名为空或没有后缀名，则返回null
    }

    /**
     * 转换容量大小单位
     * 数据库中存的是单位是 B，前端显示的是单位 MB
     *
     * @param capacity
     * @return
     */
    public static Integer convertCapacityUnit(Long capacity) {
        int capacityInGB = (int) (capacity / (1024 * 1024 * 1024));
        return capacityInGB;
    }



    /**
     * 生成真实文件的文件名， originName_identifier.xxx 防止覆盖上传
     * @param relativePath 存储的相对路径
     * @param identifier 上传时的任务标识
     * @return demo:/folderA/folderB/file_identifier.xxx
     */
    public static String generateRealPath(String relativePath, String identifier) {
        int index = relativePath.lastIndexOf("/");
        // 从路径中截取文件名
        String fileName = "";
        String path = "";
        if (index == -1) { // 单文件上传，文件的 relativePath 为不带有 /
            fileName = relativePath;
        } else {  // 文件夹上传中的文件
            path = relativePath.substring(0, index);
            fileName = relativePath.substring(index + 1, relativePath.length());
        }


        // 拼接新的文件名
        StringBuilder name = new StringBuilder();
        index = fileName.lastIndexOf(".");
        if (index != -1) {
            name = name.append(fileName.substring(0, index))
                    .append("_")
                    .append(identifier)
                    .append(".")
                    .append(fileName.substring(index + 1, fileName.length()));
        } else {
            name = name.append(fileName)
                    .append("_")
                    .append(identifier);
        }

        String finalPath = new StringBuilder(path).append("/")
                .append(name)
                .toString();
        return finalPath;
    }


    public static boolean checkmd5(String fileRealPath, String md5) {
        return DigestUtil.md5Hex(fileRealPath).equals(md5);
    }
}
