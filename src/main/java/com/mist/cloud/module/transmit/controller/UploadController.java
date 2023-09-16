package com.mist.cloud.module.transmit.controller;

import cn.hutool.core.io.FileUtil;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.file.service.FileContext;
import com.mist.cloud.module.transmit.model.req.IdentifierItem;
import com.mist.cloud.module.transmit.service.IUploadService;
import com.mist.cloud.core.result.Result;
import com.mist.cloud.core.result.SuccessResult;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.module.transmit.context.DefaultFileUploadContext;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.context.UploadTaskContext;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import com.mist.cloud.module.transmit.model.req.MergeFileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 08:56
 * @Description:
 */
@RestController
@Slf4j
public class UploadController {
    @Resource
    private FileConfig fileConfig;
    @Resource
    private IUploadService uploadSevice;

    @Resource(type = DefaultFileUploadContext.class)
    private UploadTaskContext uploadTaskContext;
    @Resource
    private FileContext fileContext;

    // 单文件上传，且不处在文件夹上传之中
    @PostMapping("/upload/file")
    public R singleUpload(@RequestPart("file") MultipartFile file, Long folderId) {
        try {
            uploadSevice.uploadSingleFile(folderId, file);
            FileUtil.writeBytes(file.getBytes(), fileConfig.getBasePath() + "/" + file.getOriginalFilename());
        } catch (IOException e) {
            log.error("write error: {}", e);
            return R.error("文件上传失败");
        }
        return R.success();
    }

    @PostMapping("/upload/chunk")
    public Result uploadChunk(ChunkVo chunk) throws FileUploadException {
        uploadTaskContext.addChunk(chunk);
        return new SuccessResult();
    }

    // 检验给分片是否已经上传 为什么不用 Result 返回，是前端这里只能根据 http 状态码判断结果
    @GetMapping("/chunk")
    public void checkChunk(ChunkVo chunk, HttpServletResponse response) throws FileUploadException {
        Task task = uploadTaskContext.getTask(chunk.getIdentifier());

        if (task == null || (task.uploadChunks != null && !task.uploadChunks[chunk.getChunkNumber()])) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @PostMapping("/upload/mergeFile")
    public Result mergeFile(@RequestBody MergeFileRequest mergeFileRequest) throws FileUploadException, IOException {
        HashMap<String, String> identifierMap = new HashMap<>();
        for (IdentifierItem identifier : mergeFileRequest.getIdentifierList()) {
            identifierMap.put(identifier.getIdentifier(), identifier.getMd5());
        }
        // 创建所有的文件夹，拿到各个路径对应的文件夹 id
        Map<String, Long> idMap = uploadSevice.createSubFolders(identifierMap, mergeFileRequest.getFolderId());
        // 合并文件
        uploadSevice.mergeFiles(identifierMap, idMap);
        return new SuccessResult("上传成功");
    }


    @GetMapping("/upload/cancel")
    public Result cancel(String identifier) throws FileUploadException, IOException {
        // 文件上传之前的取消上传，发生在文件校验时
        if (identifier == null) {
            return new SuccessResult();
        }

        // 文件上传过程中的取消上传
        Task task = uploadTaskContext.getTask(identifier);
        throw new FileUploadException("取消上传", identifier);
    }

}
