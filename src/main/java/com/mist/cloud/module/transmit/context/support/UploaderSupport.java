package com.mist.cloud.module.transmit.context.support;

import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.context.UploadTaskExecutor;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 20:38
 * @Description:
 */
public class UploaderSupport {
    @Resource
    public FileConfig fileConfig;

    @Resource(name = "uploadTaskExecutor")
    protected UploadTaskExecutor taskExecutor;

    @Resource
    protected IFolderRepository folderRepository;
    @Resource
    protected IFileRepository fileRepository;

    protected Set<String> completedTask = new HashSet<String>();

    protected Set<String> collectPath(Map<String, String> identifierMap) throws FileUploadException {
        Set<String> pathSet = new HashSet<>();
        for (String identifier : identifierMap.keySet()) {
            if (completedTask.contains(identifier)) {
                return pathSet;
            }

            Task task = taskExecutor.getTask(identifier);
            String relativePath = task.getRelativePath();

            // 排除掉单文件上传的情况
            // 这里的 relativePath 总是会带有 / 的，即使是单文件上传，也会是 /filename，需要排除这种情况
            if (relativePath.substring(1, relativePath.length())
                    .contains("/")) {
                pathSet.add(relativePath);
            }
        }
        return pathSet;
    }


}
