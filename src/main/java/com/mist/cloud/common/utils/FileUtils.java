package com.mist.cloud.common.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.mist.cloud.common.constant.Constants;
import com.mist.cloud.common.exception.file.FileUploadException;
import com.mist.cloud.aggregate.tansmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
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
//            throw new FileUploadException();
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


    public static String generatePath(String uploadFolder, ChunkVo chunk) throws FileUploadException {
        StringBuilder sb = new StringBuilder();

        String relativePath = chunk.getRelativePath();
        sb.append(uploadFolder);
        // 真实路径与文件名不相同，说明是上传的是文件夹中的文件，需要手动拼接文件夹路径
        if (!relativePath.equals(chunk.getFilename())) {
            String[] splits = relativePath.split("/");
            for (int i = 0; i < splits.length - 1; i++) {
                sb.append("/").append(splits[i]);
            }
        }

        sb.append("/").append(chunk.getIdentifier());

        //判断路径是否存在，不存在则创建
        if (!Files.isWritable(Paths.get(sb.toString()))) {
            try {
                Files.createDirectories(Paths.get(sb.toString()));
            } catch (IOException e) {
                throw new FileUploadException("File chunk's folder create failed :" + sb, chunk.getIdentifier(), e);
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
     * @param folder     要合并的文件所在的文件夹
     * @param filename   文件名
     * @return md5 文件的 md5
     */
    public static String merge(String targetFile, String folder, String filename) throws IOException {
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
        }
        // 删除原文件夹
        FileUtils.deleteDirectoryIfExist(Paths.get(folder));

        // 读取文件 md5 值
        File file = new File(targetFile);
        return DigestUtil.md5Hex(file);
    }

    /**
     * 创建文件，如果文件存在就覆盖掉
     *
     * @param path
     */
    public static void createFileOrOverwrite(String path) throws IOException {

        //判断路径是否存在，不存在则创建
        if (!Files.isWritable(Paths.get(path))) {
            Files.createDirectories(Paths.get(path));
        }

        // 创建文件，已存在就覆盖
//        Files.deleteIfExists(Paths.get(path));
//        Files.createFile(Paths.get(path));
    }

    /**
     * 删除指定路径
     *
     * @param dir
     */
    public static void deleteDirectoryIfExist(Path dir) throws IOException {
        if (Files.exists(dir)) {
            try (Stream<Path> pathStream = Files.walk(dir)) {
                pathStream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
            }
        }
    }

    /**
     * 创建一个目录
     * @param folderPath
     *
     * 如果目录已存在就全部删除之后在创建
     */
    public static void createDirectory(Path path) throws IOException {
        if(Files.exists(path)){
            deleteDirectory(path);
        }

        Files.createDirectory(path);

    }

    public static void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
                .sorted((p1, p2) -> -p1.compareTo(p2))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
