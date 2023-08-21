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
import com.mist.cloud.service.IFileService;
import com.mist.cloud.service.IFolderService;
import com.mist.cloud.service.IUploadSevice;
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
import java.util.Map;
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
    @Resource
    private IUploadSevice uploadSevice;


    @Resource(type = DefaultFileUploadContext.class)
    private UploadTaskContext uploadTaskContext;

    @PostMapping("/chunk")
    public Result uploadChunk(ChunkVo chunk) throws FileUploadException {
        uploadTaskContext.addChunk(chunk);
        return new SuccessResult();
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
        Map<String, Long> idMap = null;
        // 上传文件夹的情况
        if (identifierList.length != 1) {
            // 保存文件所有的路径，后续根据这个路径在数据库构造文件夹结构
            Set<String> pathSet = new HashSet<>();
            Long parentId = null; // 一次上传文件夹的请求中的所有文件的 foldeId 都是同一个
            for (String identifier : identifierList) {
                Task task = uploadTaskContext.getTask(identifier);
                String relativePath = task.getRelativePath();
                parentId = task.getFolderId();
                pathSet.add(relativePath);
            }

            // 创建所有的文件夹，拿到各个路径对应的文件夹 id
            idMap = uploadSevice.addFolder(parentId, pathSet);
        }


        for (String identifier : identifierList) {
            Task task = uploadTaskContext.getTask(identifier);

            /**
             * 更新 task 列表中每个文件的 folderId
             * 排除只上传文件的情况
             * task.setRelativePath("/" + fileInfo.getRelativePath());
             *
             * idMap 中的路径是不带有文件名的，relativePath带有文件名，获取生成的文件夹 id 需要适当调整
             * 全部文件/javad   |  全部文件/java/java.md
             * relativePath.substring(0, relativePath.lastIndexOf('/'))
             */
            String relativePath = task.getRelativePath();
            if (relativePath.substring(1, relativePath.length()).contains("/")) {
                task.setFolderId(idMap.get(relativePath.substring(0, relativePath.lastIndexOf('/'))));
            }

            String fileName = task.getFileName();
            String file = fileConfig.getBase_path() + "/" + task.getTargetFilePath();
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
            // 数据库添加记录
            uploadSevice.addFile(task);
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
