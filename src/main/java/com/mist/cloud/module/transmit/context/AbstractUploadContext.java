package com.mist.cloud.module.transmit.context;

import cn.hutool.core.io.FileUtil;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.context.support.DatabaseSupport;
import com.mist.cloud.module.transmit.context.support.IOsupport;
import com.mist.cloud.module.transmit.context.support.UploaderSupport;
import com.mist.cloud.module.transmit.context.uploader.CompleteChunkFolderUploader;
import com.mist.cloud.module.transmit.context.uploader.SimpleFolderUploader;
import com.mist.cloud.module.transmit.model.req.SimpleUploadRequest;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.mist.cloud.core.utils.FileUtils.generateRealPath;

/**
 * @Author: securemist
 * @Datetime: 2023/8/18 10:20
 * @Description:
 */
@Slf4j
@Component
public class AbstractUploadContext extends UploaderSupport implements UploadTaskContext {
    @Resource
    protected IOsupport iOsupport;
    @Resource
    protected DatabaseSupport databaseSupport;

    @Resource
    private CompleteChunkFolderUploader completeChunkFolderUploadContext;
    @Resource
    private SimpleFolderUploader simpleFolderUploadContext;


    @Override
    public void addChunk(ChunkVo chunk) throws FileUploadException {
        if (completedTask.contains(chunk.getIdentifier())) {
            return;
        }

        iOsupport.writeChunk(chunk);
        Task task = taskExecutor.getTask(chunk.getIdentifier());
        if (task == null) {
            task = taskExecutor.createTask(chunk);
        }

        task.uploadChunks[chunk.getChunkNumber()] = true;// chunk 的排序从 1 开始
        taskExecutor.updateTask(task);
    }


    @Override
    public void mergeFiles(HashMap<String, String> identifierMap, Long folderId) throws FileUploadException {
        // 收集文件中所有文件路径进行去重
        Set<String> pathSet = collectPath(identifierMap);

        // 对所有路径，创建对应的文件夹 返回路径底层文件夹id与路径的映射   ["/folderA/folderB" ---  3123121241412 ]
        HashMap<String, Long> folderIdMap = simpleFolderUploadContext.createFolders(pathSet, folderId);

        // 开始合并
        doMergeFiles(identifierMap, folderIdMap);
    }


    public void doMergeFiles(HashMap<String, String> identifierMap, Map<String, Long> folderIdMap) throws FileUploadException {
        for (String identifier : identifierMap.keySet()) {
            try {
                if (completedTask.contains(identifier)) {
                    return;
                }
                Task task = taskExecutor.getTask(identifier);

                // 合并文件
                iOsupport.mergeFile(task);

                // 校验md5
                iOsupport.checkmd5(task, identifierMap.get(identifier));

                // 从任务队列溢出
                taskExecutor.removeTask(identifier);
                completedTask.add(identifier);

                // 数据库添加记录
                task.setFolderId(folderIdMap.get(task.getRelativePath()));
                databaseSupport.addChunkableFile(task);
                log.info("文件上传成功, 文件位置: {}", fileConfig.getBasePath() + task.getRelativePath());
            } catch (Exception e) {
                e.printStackTrace();
                throw new FileUploadException("file upload failed in PreviewController", new ArrayList<>(identifierMap.keySet()), e);
            }

        }
    }

    @Override
    public void cancelTask(List<String> identifierList) {
        for (String identifier : identifierList) {
            completedTask.add(identifier);
            // 删除上传过程产生的所有有关文件
            Task task = taskExecutor.getTask(identifier);
            if (task == null) {
                continue;
            }

            FileUtil.del(fileConfig.getUploadPath() + task.getFolderPath());
            FileUtil.del(fileConfig.getBasePath() + task.getRelativePath());
            log.error("文件上传失败: {} , {}, 已删除参与文件", identifier, task.getRelativePath());
        }

    }

    @Override
    public void simpleUpload(SimpleUploadRequest simpleUploadRequest) throws IOException {
        String path = simpleUploadRequest.getPath();
        Long folderId = simpleUploadRequest.getFolderId();
        MultipartFile file = simpleUploadRequest.getFile();
        String identifier = simpleUploadRequest.getIdentifier();

        // 判断是不是文件夹中的文件
        if (isFilePath(path)) {
            path = "/" + path;
        } else {
            // 位于文件夹中简单上传需要考虑到路径问题
            folderId = simpleFolderUploadContext.createPathIfAbsent(folderId, path.substring(0, path.lastIndexOf("/")));
        }

        path = generateRealPath(path, identifier);
        iOsupport.writeSimpleFile(path, file);
        databaseSupport.addSimpleFile(folderId, path, file);
    }


    /**
     * 判断是不是文件夹中的文件路径
     * folder: /foldeA/code.java
     * file: code.java
     *
     * @param path
     * @return
     */
    private boolean isFilePath(String path) {
        return path.indexOf("/") == -1;
    }
}
