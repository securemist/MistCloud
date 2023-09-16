package com.mist.cloud.module.transmit.context;

import cn.hutool.core.io.FileUtil;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.transmit.context.support.DatabaseSupport;
import com.mist.cloud.module.transmit.context.support.IOsupport;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @Author: securemist
 * @Datetime: 2023/8/18 10:20
 * @Description:
 */
@Slf4j
public abstract class AbstractUploadContext implements UploadTaskContext {
    @Resource
    protected IOsupport iOsupport;
    @Resource
    protected IFolderRepository folderRepository;
    @Resource
    protected DatabaseSupport databaseSupport;
    @Resource
    FileConfig fileConfig;

    @Resource(name = "uploadTaskExecutor")
    protected UploadTaskExecutor taskExecutor;

    protected Set<String> completedTask = new HashSet<String>();

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
        // 给定的所有路径中，递归创建需要的所有子文件夹，返回文件夹与id的映射  [/全部文件/.. => folderId]
        Map<String, Long> folderIdMap = createSubFolders(identifierMap, folderId);
        // 开始合并
        doMergeFiles(identifierMap, folderIdMap);
    }

    public void completeTask(String identifier) {
        taskExecutor.removeTask(identifier);
        completedTask.add(identifier);
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
    public void simpleUpload(Long folderId, MultipartFile file) throws IOException {
        iOsupport.writeSimpleFile(file);
        databaseSupport.addSimpleFile(folderId, file);
    }

    public void doMergeFiles(HashMap<String, String> identifierMap, Map<String, Long> idMap) throws FileUploadException {
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
                completeTask(identifier);
                // 数据库添加记录
                databaseSupport.addChunkableFile(task);
                log.info("文件上传成功, 文件位置: {}", fileConfig.getBasePath() + task.getRelativePath());
            } catch (Exception e) {
                e.printStackTrace();
                throw new FileUploadException("file upload failed in controller", new ArrayList<>(identifierMap.keySet()), e);
            }

        }
    }

    protected abstract Map<String, Long> createSubFolders(Map<String, String> identifierMap, Long parentId) throws FileUploadException;


}
