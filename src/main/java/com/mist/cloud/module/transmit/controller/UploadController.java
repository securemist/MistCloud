package com.mist.cloud.module.transmit.controller;

import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.transmit.context.UploadTaskContext;
import com.mist.cloud.module.transmit.model.req.IdentifierItem;
import com.mist.cloud.module.transmit.model.req.MergeFileRequest;
import com.mist.cloud.module.transmit.model.req.SimpleUploadRequest;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 08:56
 * @Description:
 */
@RestController
@Slf4j
@Tag(name = "上传文件")
public class UploadController {
    @Resource
    private FileConfig fileConfig;

    @Resource
    private UploadTaskContext uploadTaskContext;

    // 单文件上传，且不处在文件夹上传之中
    @PostMapping("/upload/file")
    @Operation(summary = "简单文件上传")
    @Parameter(name = "simpleUploadRequest")
    public R singleUpload(SimpleUploadRequest simpleUploadRequest) throws IOException {
        uploadTaskContext.simpleUpload(simpleUploadRequest);
        return R.success();
    }

    @PostMapping("/upload/chunk")
    @Operation(summary = "单个分片上传")
    @Parameter(name = "chunk")
    public R uploadChunk(ChunkVo chunk) throws FileUploadException {
        uploadTaskContext.addChunk(chunk);
        return R.success();
    }

    @PostMapping("/upload/mergeFile")
    @Operation(summary = "合并文件",description = "仅限于分片上传的文件")
    @Parameter(name = "mergeFileRequest")
    public R mergeFile(@RequestBody MergeFileRequest mergeFileRequest) throws FileUploadException, IOException {
        HashMap<String, String> identifierMap = new HashMap<>();
        for (IdentifierItem identifier : mergeFileRequest.getIdentifierList()) {
            identifierMap.put(identifier.getIdentifier(), identifier.getMd5());
        }
        uploadTaskContext.mergeFiles(identifierMap, mergeFileRequest.getFolderId());
        return R.success("上传成功");
    }

    @PostMapping("/upload/cancel")
    @Operation(summary = "取消上传")
    @Parameter(name = "identifierList",description = "所有上传任务的标识")
    public R calcel2(@RequestBody List<String> identifierList) throws FileUploadException {
        throw new FileUploadException("取消上传", identifierList);
    }
}
