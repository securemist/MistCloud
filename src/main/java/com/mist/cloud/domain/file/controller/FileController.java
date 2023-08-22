package com.mist.cloud.domain.file.controller;

import com.mist.cloud.common.constant.Constants;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.common.exception.file.FileCommonException;
import com.mist.cloud.common.exception.file.FileUploadException;
import com.mist.cloud.domain.file.service.IFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Lang;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;


/**
 * @Author: securemist
 * @Datetime: 2023/7/18 14:41
 * @Description:
 */
@RestController
@Slf4j
@Api(description = "文件操作")
public class FileController {
    @Resource
    private FileConfig fileConfig;
    @Resource
    private IFileService fileService;
//    @Resource
//    private UploadTaskContext uploadTaskContext;

    @PostMapping(value = "/file/upload")
    @ApiOperation(value = "上传单个文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "上传的文件", dataTypeClass = MultipartFile.class),
            @ApiImplicitParam(name = "folderId", value = "文件所在的文件夹 id", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "uid", value = "上传任务的 uid", dataTypeClass = String.class)
    })
    public Result singleFileUpload(@RequestPart("file") MultipartFile file,
                                   @RequestParam("folderId") Long folderId,
                                   @RequestParam("uid") String uid) throws FileUploadException {
        ThreadLocal<String> uploadTask = new ThreadLocal<>();

        uploadTask.set(uid);
        HashMap<String, String> res = new HashMap<>();

        if (file.isEmpty()) {
            return new FailedResult(Constants.Response.FILE_EMPTY.getMsg());
        }

        log.info("收到文件：" + file.getOriginalFilename() + "  上传文件夹的id为:" + folderId);

        try {
            // 文件上传
//            FileUtils.upload(file, fileConfig.getBase_path());
            // 数据库写入数据
//            fileService.insertFile(file, folderId);
        } catch (Exception e) {
            if (!(e instanceof FileUploadException)) {
                log.info("file upload error when insert data to database",e.getMessage());
                throw new FileUploadException(e.getMessage(), uid, e);
            }
        }

        res.put("uid",uid);
        return new SuccessResult(Constants.Response.FILE_UPLOAD_SUCCESS.getMsg(), res);
    }



    @GetMapping(value = "/file/rename")
    @ApiOperation(value = "文件重命名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "要修改的文件名 id", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "fileName", value = "修改后的文件名", dataTypeClass = String.class)})
    public Result rename(@RequestParam("fileId") Long fileId, @RequestParam("fileName") String fileName) {
        fileService.renameFile(fileId, fileName);
        return new SuccessResult();
    }

    @GetMapping(value = "/file/copy")
    @ApiOperation(value = "文件复制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetFolderId", value = "目标文件夹 id", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "fileId", value = "当前文件 id", dataTypeClass = Lang.class)})
    public Result copy(@RequestParam("targetFolderId") Long targetFolderId, @RequestParam("fileId") Long fileId) throws FileCommonException {
        fileService.copyFile(fileId, targetFolderId);
        return new SuccessResult();
    }

    @GetMapping(value = "/file/delete")
    @ApiOperation(value = "文件删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "folderId", value = "要删除的文件 id", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "realDelete", value = "是否真实删除", dataTypeClass = Boolean.class)
    })
    public Result delete(@RequestParam("fileId") Long fileId, @RequestParam("realDelete") Boolean realDelete) {
        fileService.deleteFile(fileId, realDelete);
        return new SuccessResult();
    }

}
