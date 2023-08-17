package com.mist.cloud.controller;

import com.mist.cloud.config.FileConfig;
import com.mist.cloud.model.po.Chunk;
import com.mist.cloud.model.po.FileInfo;
import com.mist.cloud.service.IChunkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mist.cloud.utils.FileUtils.generatePath;
import static com.mist.cloud.utils.FileUtils.merge;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 08:56
 * @Description:
 */
@RestController
@RequestMapping("/upload")
@Slf4j
public class UploadController {
    //    @Resource
//    private FileInfoService fileInfoService;
    @Resource
    private IChunkService chunkService;
    @Resource
    private FileConfig fileConfig;

    @PostMapping("/chunk")
    public String uploadChunk(Chunk chunk) {
        MultipartFile file = chunk.getFile();
        log.debug("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(generatePath(fileConfig.getBase_path(), chunk));
            //文件写入指定路径
            Files.write(path, bytes);
            log.debug("文件 {} 写入成功, uuid:{}", chunk.getFilename(), chunk.getIdentifier());
            chunkService.saveChunk(chunk);

            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "后端异常...";
        }
    }

    @GetMapping("/chunk")
    public Object checkChunk(Chunk chunk, HttpServletResponse response) {
        if (chunkService.checkChunk(chunk.getIdentifier(), chunk.getChunkNumber())) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }

        return chunk;
    }

    @PostMapping("/mergeFile")
    public String mergeFile(@RequestBody FileInfo fileInfo) {
        String filename = fileInfo.getFilename();
        String file = fileConfig.getBase_path() + "/" + filename;
        String folder = fileConfig.getBase_path() + "/" + fileInfo.getIdentifier();
        merge(file, folder, filename);
        fileInfo.setLocation(file);
//        fileInfoService.addFileInfo(fileInfo);

        return "合并成功";
    }


}
