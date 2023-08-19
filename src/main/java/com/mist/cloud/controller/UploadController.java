package com.mist.cloud.controller;

import cn.hutool.core.io.FileUtil;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
import com.mist.cloud.config.FileConfig;
import com.mist.cloud.config.context.DefaultFileUploadContext;
import com.mist.cloud.config.context.Task;
import com.mist.cloud.config.context.UploadTaskContext;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.model.vo.FileInfoVo;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.service.IChunkService;
import com.mist.cloud.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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

    @Resource(type = DefaultFileUploadContext.class)
    private UploadTaskContext uploadTaskContext;


    @PostMapping("/chunk")
    public Result uploadChunk(ChunkVo chunk) throws FileUploadException {
        uploadTaskContext.addChunk(chunk);
            return new SuccessResult();

//        MultipartFile file = chunk.getFile();
//        log.debug("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());
//
//        Path path = null;
//        try {
//            byte[] bytes = file.getBytes();
//            path = Paths.get(uploadTaskContext.generatePath(chunk));
//            //文件写入指定路径
//            if(!Files.isWritable(path)){
//                Files.createFile(path );
//            }
//            Files.write(path, bytes);
//            log.debug("文件分片 {} 写入成功, 分片标识:{}" + path.toString(), chunk.getIdentifier());
//            uploadTaskContext.addChunk(chunk);
//
//            return new SuccessResult();
//
//        } catch (IOException e) {
//            throw new FileUploadException("File chunk upload failed " + path.toString(), chunk.getIdentifier(), e);
//        }
    }

    // 检验给分片是否已经上传 为什么不用 Result 返回，是前端这里只能根据 http 状态码判断结果
    @GetMapping("/chunk")
    public void checkChunk(ChunkVo chunk, HttpServletResponse response) {
        Task task = uploadTaskContext.getTask(chunk.getIdentifier());

        if (task == null || (task.uploadChunks != null && !task.uploadChunks[chunk.getChunkNumber()])) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @PostMapping("/mergeFile")
    public Result mergeFile(@RequestBody String[] identifierList) throws FileUploadException, IOException {

        // 保存文件所有的路径，后续根据这个路径在数据库改造文件夹结构
        Set<String> pathSet = new HashSet<>();
        Long folderId = null; // 一次上传文件夹的请求中的所有文件的 foldeId 都是同一个
        for (String identifier : identifierList) {
            Task task = uploadTaskContext.getTask(identifier);
            String relativePath = task.getRelativePath();
            folderId = task.getFolderId();
            pathSet.add(relativePath);
        }
//        uploadServicel.addFolder(pathSet, folderId);


        for (String identifier : identifierList) {
            Task task = uploadTaskContext.getTask(identifier);

            String fileName = task.getFileName();
            String file = fileConfig.getBase_path() + "/" +  task.getTargetFilePath();
            String folder = fileConfig.getBase_path() + "/" + task.getFolderPath();

            String newMD5 = "";
            try {
                newMD5 = merge(file, folder, fileName);
            } catch (IOException e) {
                throw new FileUploadException("file merge error in IO", identifier, e);
            }

            if (!newMD5.equals(task.getMD5())) {
                throw new FileUploadException("file merge error because md5 is not equal", identifier);
            }

            uploadTaskContext.completeTask(identifier);
            log.info("文件上传成功, 文件位置: {}", file);
        }

        return new SuccessResult("上传成功");
    }


    @PostMapping("/info")
    public Result getInfo(@RequestBody FileInfoVo[] fileInfoList) throws FileUploadException {
        uploadTaskContext.setTaskInfo(fileInfoList);
        // 将拿到的 md5 值交给任务队列
        return new SuccessResult();
    }


    @GetMapping("/cancel")
    public Result cancel(String identifier) throws FileUploadException, IOException {
        throw new FileUploadException("取消上传", identifier);
    }
}
