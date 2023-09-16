package com.mist.cloud.module.transmit.context.support;

import com.mist.cloud.module.transmit.context.Task;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 20:38
 * @Description:
 */
public interface UploadSupport {

    void addChunkableFile(Task task);

    void addSimpleFile(Long folderId, MultipartFile file);
}
