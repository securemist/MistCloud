package com.mist.cloud.controller;

import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
import com.mist.cloud.config.FileConfig;
import com.mist.cloud.config.context.Task;
import com.mist.cloud.config.context.UploadTaskContext;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.FileInfoVo;
import com.mist.cloud.model.vo.ChunkVo;
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
    @Resource
    private UploadTaskContext uploadTaskContext;

    @PostMapping("/chunk")
    public Result uploadChunk(ChunkVo chunk) {

        MultipartFile file = chunk.getFile();
        log.debug("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(generatePath(fileConfig.getBase_path(), chunk));
            //文件写入指定路径
            Files.write(path, bytes);
            log.debug("文件分片 {} 写入成功, 分片标识:{}", chunk.getFilename(), chunk.getIdentifier());
            uploadTaskContext.addChunk(chunk);

            return new SuccessResult();

        } catch (IOException e) {
            log.info("文件分片 {} 写入失败, 分片标识:{} , 原因: ", chunk.getFilename(), chunk.getIdentifier(), e.getMessage());
            return new FailedResult("分片上传失败");
        }
    }

    // 这个方法不是必须的，无非是一次多余的重传，建议不要谨慎修改
    @GetMapping("/chunk")
    public void checkChunk(ChunkVo chunk, HttpServletResponse response) {
        // 该分片已上传
        if (uploadTaskContext.checkChunkUploaded(chunk)) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }

    @GetMapping("/mergeFile")
    public Result mergeFile(String identifier) throws FileUploadException, IOException {
        Task task = uploadTaskContext.getTask(identifier);

        String fileName = task.getFileName();
        String file = task.getTargetFilePath();
        String folder = task.getFolderPath();

        String md5 = null;
        try {
            md5 = merge(file, folder, fileName);
        } catch (IOException e) {
            return new FailedResult("文件上传失败");
        }

        boolean res = uploadTaskContext.completeTask(identifier, md5);
        if (res) {
            log.info("文件上传成功, 文件位置: {}", file);
            return new SuccessResult("文件上传成功");
        } else {
            Files.deleteIfExists(Paths.get(folder));
            Files.deleteIfExists(Paths.get(file));
            log.info("文件上传失败, 文件名: {}，已删除原有分片", fileName);
            return new FailedResult("文件上传失败");
        }
    }


    @PostMapping("/info")
    public Result getInfo(@RequestBody FileInfoVo fileInfo) throws FileUploadException {
        uploadTaskContext.setTaskInfo(fileInfo);

        // 将拿到的 md5 值交给任务队列
        return new SuccessResult();
    }


    @GetMapping("/cancel")
    public Result cancel(String identifier) throws FileUploadException, IOException {
        uploadTaskContext.cancelTask(identifier);

        Task task = uploadTaskContext.getTask(identifier);

        Files.deleteIfExists(Paths.get(task.getFolderPath()));
        Files.deleteIfExists(Paths.get(task.getTargetFilePath()));
        log.info("取消文件上传任务, 已删除相关文件 {}", identifier);

        return new SuccessResult();
    }
}
