package com.mist.cloud.module.transmit.controller;

import com.mist.cloud.module.transmit.service.IUploadService;
import com.mist.cloud.core.result.Result;
import com.mist.cloud.core.result.SuccessResult;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.module.transmit.context.DefaultFileUploadContext;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.context.UploadTaskContext;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import com.mist.cloud.module.transmit.model.vo.MergeFileRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.mist.cloud.core.utils.FileUtils.*;

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
    public Result mergeFile(@RequestBody List<MergeFileRequestVo> mergeInfoList) throws FileUploadException, IOException {

        HashMap<String, String> identifierMap = new HashMap<>();
        for (MergeFileRequestVo mergeFileRequestVo : mergeInfoList) {
            identifierMap.put(mergeFileRequestVo.getIdentifier(), mergeFileRequestVo.getMd5());
        }

        Map<String, Long> idMap = null;
        // 上传文件夹的情况
        // 保存文件所有的路径，后续根据这个路径在数据库构造文件夹结构
        Set<String> pathSet = new HashSet<>();
        Long parentId = null; // 一次上传文件夹的请求中的所有文件的 foldeId 都是同一个
        for (String identifier : identifierMap.keySet()) {
            Task task = uploadTaskContext.getTask(identifier);
            String relativePath = task.getRelativePath();
            parentId = task.getFolderId();

            // 排除掉单文件上传的情况
            // 这里的 relativePath 总是会带有 / 的，即使是单文件上传，也会是 /filename，需要排除这种情况
            if(relativePath.substring(1, relativePath.length()).contains("/")){
                pathSet.add(relativePath);
            }
        }

        // 创建所有的文件夹，拿到各个路径对应的文件夹 id
        idMap = uploadSevice.uploadFolder(parentId, pathSet);


        for (String identifier : identifierMap.keySet()) {
            try {
                Task task = uploadTaskContext.getTask(identifier);

                /**
                 * 在创建文件夹的过程中已经创建了文件夹，生成了新的文件夹 id，需要替换掉 task 中原油的 folderId
                 * 上传文件不需要考虑这种情况
                 * task.setRelativePath("/" + fileInfo.getRelativePath());
                 *
                 * idMap 中的路径是不带有文件名的，relativePath带有文件名，获取生成的文件夹 id 需要适当调整
                 * 全部文件/java   |  全部文件/java/java.md
                 * relativePath.substring(0, relativePath.lastIndexOf('/'))
                 */
                if(idMap.size() != 0){
                    String relativePath = task.getRelativePath();
                    if (relativePath.substring(1, relativePath.length()).contains("/")) {
                        task.setFolderId(idMap.get(relativePath.substring(0, relativePath.lastIndexOf('/'))));
                    }
                }


                String fileName = task.getFileName();
                String file = fileConfig.getBasePath() + "/" + task.getTargetFilePath();
                String folder = fileConfig.getUploadPath() + "/" + task.getFolderPath();

                // 合并文件并且算出 md5 值
                String newMD5 = "";
                try {
                    newMD5 = merge(file, folder, fileName);
                } catch (IOException e) {
                    throw new FileUploadException("file merge error in IO", identifier, e);
                }

                // 校验 md5 值
                String md5 = identifierMap.get(identifier);

                if (!md5.equals(newMD5)) {
                    throw new FileUploadException("file merge error because md5 is not equal", identifier);
                }

                uploadTaskContext.completeTask(identifier);
                // 数据库添加记录
                task.setMD5(md5);
                uploadSevice.uploadFile(task);
                log.info("文件上传成功, 文件位置: {}", file);
            } catch (FileUploadException e) {
                throw new FileUploadException(e.getMsg(), identifier);
            } catch (Exception e) {
                throw new FileUploadException("file upload failed in controller", identifier, e);
            }

        }

        return new SuccessResult("上传成功");
    }


    @GetMapping("/cancel")
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
