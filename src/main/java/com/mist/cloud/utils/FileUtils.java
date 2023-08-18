package com.mist.cloud.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.mist.cloud.common.Constants;
import com.mist.cloud.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 19:07
 * @Description:
 */
@Slf4j
public class FileUtils {
    // 获取文件类型
    public static String getFileType(MultipartFile file) {

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
     * 上传文件
     *
     * @param file      文件
     * @param base_path 存放路径
     * @throws IOException
     */
    public static void upload(MultipartFile file, String base_path) throws IOException {

        // 路径名
        Path dir = Paths.get(base_path);
        // 路径+文件名
        Path path = Paths.get(base_path + "/" + file.getOriginalFilename());

        try {
            file.transferTo(new File(String.valueOf(path)));
        } catch (IOException e) {
            log.error("upload file error when tranfer to local, fileName: {}", file.getOriginalFilename());
            throw new FileUploadException(e.getMessage());
        }
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

    public static String generatePath(String uploadFolder, ChunkVo chunk) {
        StringBuilder sb = new StringBuilder();
        sb.append(uploadFolder).append("/").append(chunk.getIdentifier());
        //判断uploadFolder/identifier 路径是否存在，不存在则创建
        if (!Files.isWritable(Paths.get(sb.toString()))) {
            log.debug("path not exist,create path: {}", sb.toString());
            try {
                Files.createDirectories(Paths.get(sb.toString()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return sb.append("/")
                .append(chunk.getFilename())
                .append("-")
                .append(chunk.getChunkNumber()).toString();
    }

    /**
     * 文件合并
     *
     * @param targetFile 目标文件位置
     * @param folder 要合并的文件所在的文件夹
     * @param filename 文件名
     * @return md5 文件的 md5
     */
    public static String merge(String targetFile, String folder, String filename) throws IOException {
        // 创建文件，已存在就覆盖
        Files.deleteIfExists(Paths.get(targetFile));
        Files.createFile(Paths.get(targetFile));

        // 合并文件
        try {
            Stream<Path> stream = Files.list(Paths.get(folder));
            stream.filter(path -> !path.getFileName().toString().equals(filename))
                    .sorted((o1, o2) -> {
                        String p1 = o1.getFileName().toString();
                        String p2 = o2.getFileName().toString();
                        int i1 = p1.lastIndexOf("-");
                        int i2 = p2.lastIndexOf("-");
                        return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                    })
                    .forEach(path -> {
                        try {
                            //以追加的形式写入文件
                            Files.write(Paths.get(targetFile), Files.readAllBytes(path), StandardOpenOption.APPEND);
                            //合并后删除该块
                            Files.delete(path);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    });
        } catch (IOException e) {
            log.debug("合并文件出错，已删除原文件 {} , {}", folder, e.getMessage());
            Files.deleteIfExists(Paths.get(targetFile));
            throw new IOException(e);
        } finally {
            // 删除原文件夹
            Files.deleteIfExists(Paths.get(folder));
        }


        // 读取文件 md5 值
        File file = new File(targetFile);
        return DigestUtil.md5Hex(file);
    }
}
