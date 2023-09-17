package com.mist.cloud.module.transmit.context.support;

import cn.hutool.core.io.FileUtil;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.core.utils.FileUtils;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 21:03
 * @Description:
 */
@Slf4j
@Component
public class IOsupport extends UploadSupport{

    public void mergeFile(Task task) throws IOException {
        String fileName = task.getFileName(); // 文件名
        String targetFilePath = fileConfig.getBasePath() + task.getRelativePath(); // 合并之后的目标文件
        String folderPath = fileConfig.getUploadPath() + task.getFolderPath(); // 合并的原文件夹
        FileUtil.touch(targetFilePath);
        // 合并文件
        Stream<Path> stream = Files.list(Paths.get(folderPath));
        List<Path> paths = stream.filter(path -> !path.getFileName().toString().equals(fileName))
                .sorted((o1, o2) -> {
                    String p1 = o1.getFileName().toString();
                    String p2 = o2.getFileName().toString();
                    int i1 = p1.lastIndexOf("-");
                    int i2 = p2.lastIndexOf("-");
                    return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                }).collect(Collectors.toList());

        paths.forEach(path -> {
            try {
                //以追加的形式写入文件
                Files.write(Paths.get(targetFilePath), Files.readAllBytes(path), StandardOpenOption.APPEND);
                //合并后删除该块
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("合并文件失败:{}, {}",task.getIdentifier(), targetFilePath);
                throw new RuntimeException(e);
            }
        });
        FileUtil.del(folderPath);
    }

    public void writeChunk(ChunkVo chunk) {
        StringBuilder path = new StringBuilder(fileConfig.getUploadPath());
        path = path.append("/").append(chunk.getIdentifier());

        // 创建分片
        path = path.append("/").append(chunk.getFileName())
                .append("-").append(chunk.getChunkNumber());

        // 创建文件
        FileUtil.touch(path.toString());

        // 写入数据
        try {
            FileUtil.writeBytes(chunk.getFile().getBytes(), path.toString());
        } catch (IOException e) {
            e.printStackTrace(); // TODO 处在文件夹任务中的文件上传如何清除残余文件
            new FileUploadException("File chunk write failed : " + chunk.getFileName()
                    + "-" + chunk.getChunkNumber(), chunk.getIdentifier(), e);
        }
    }


    public void writeSimpleFile(MultipartFile file) throws IOException {
        FileUtil.writeBytes(file.getBytes(), fileConfig.getBasePath() + "/" + file.getOriginalFilename());
    }

    public boolean checkmd5(Task task, String md5) {
        if (fileConfig.checkmd5) {
            // 合并文件并且算出 md5 值
            boolean ok = FileUtils.checkmd5(task.getTargetFilePath(), md5);
            task.setMD5(md5);
            return ok;
        }
        return true;
    }
}
