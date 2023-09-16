package com.mist.cloud.module.transmit.controller;

import com.mist.cloud.core.result.R;
import com.mist.cloud.module.transmit.model.req.IdentifierItem;
import com.mist.cloud.core.result.Result;
import com.mist.cloud.core.result.SuccessResult;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.module.transmit.context.UploadTaskContext;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import com.mist.cloud.module.transmit.model.req.MergeFileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    private UploadTaskContext uploadTaskContext;

    // 单文件上传，且不处在文件夹上传之中
    @PostMapping("/upload/file")
    public R singleUpload(@RequestPart("file") MultipartFile file, Long folderId) throws IOException {
        uploadTaskContext.simpleUpload(folderId, file);
        return R.success();
    }

    @PostMapping("/upload/chunk")
    public Result uploadChunk(ChunkVo chunk) throws FileUploadException {
        uploadTaskContext.addChunk(chunk);
        return new SuccessResult();
    }

    @PostMapping("/upload/mergeFile")
    public Result mergeFile(@RequestBody MergeFileRequest mergeFileRequest) throws FileUploadException, IOException {
        HashMap<String, String> identifierMap = new HashMap<>();
        for (IdentifierItem identifier : mergeFileRequest.getIdentifierList()) {
            identifierMap.put(identifier.getIdentifier(), identifier.getMd5());
        }
        uploadTaskContext.mergeFiles(identifierMap, mergeFileRequest.getFolderId());
        return new SuccessResult("上传成功");
    }

    @PostMapping("/upload/cancel")
    public R calcel2(@RequestBody List<String> identifierList) throws FileUploadException {
        throw new FileUploadException("取消上传", identifierList);
    }
}
