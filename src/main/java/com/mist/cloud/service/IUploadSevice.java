package com.mist.cloud.service;

import com.mist.cloud.config.context.Task;

import java.util.Map;
import java.util.Set;

/**
 * @Author: securemist
 * @Datetime: 2023/8/19 17:00
 * @Description:
 */
public interface IUploadSevice {
    void addFile(Task task);

    Map<String, Long> addFolder(Long parentId, Set<String> pathSet);
}
