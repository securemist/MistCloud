package com.mist.cloud.controller;

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

import static com.mist.cloud.utils.FileUtils.*;

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
    public Result uploadChunk(ChunkVo chunk) throws FileUploadException {

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
            throw new FileUploadException("this chunk upload failed", chunk.getIdentifier(), e);
        }
    }

    // 检验给分片是否已经上传 为什么不用 Result 返回，是前端这里只能根据 http 状态码判断结果
    @GetMapping("/chunk")
    public void checkChunk(ChunkVo chunk, HttpServletResponse response) {
        Task task = uploadTaskContext.getTask(chunk.getIdentifier());

        if(task == null || (task.uploadChunks != null && !task.uploadChunks[chunk.getChunkNumber()])){
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @GetMapping("/mergeFile")
    public Result mergeFile(String identifier) throws FileUploadException, IOException {
        // setInfo 请求可能会在合并请求之后到达。这里需要等待
        // 正常情况下不会，本机测试的时候文件发送太快了会发生这种情况
        while (true){
            if(uploadTaskContext.getTask(identifier).isSetInfo()){
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Task task = uploadTaskContext.getTask(identifier);
        String fileName = task.getFileName();
        String file = task.getTargetFilePath();
        String folder = task.getFolderPath();

        String md5 = null;
        try {
            md5 = merge(file, folder, fileName);
        } catch (IOException e) {
            throw new FileUploadException("file merge error in IO", identifier, e);
        }

        boolean res = uploadTaskContext.completeTask(identifier, md5);
        if (res) {
            log.info("文件上传成功, 文件位置: {}", file);
            return new SuccessResult("文件上传成功");
        } else {
            throw new FileUploadException("file merge error because md5 is not equal", identifier);
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
        throw new FileUploadException("取消上传",identifier);
    }
}
