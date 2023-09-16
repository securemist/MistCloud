package com.mist.cloud.module.transmit.context;

import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.model.vo.ChunkVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @Author: securemist
 * @Datetime: 2023/8/16 16:21
 * @Description:
 */
public interface UploadTaskContext {

    /**
     * 非分片的单文件上传
     *
     * @param folderId
     * @param file
     */
    void simpleUpload(Long folderId, MultipartFile file) throws IOException;

    /**
     * 添加文件分片
     *
     * @param chunk
     */
    void addChunk(ChunkVo chunk) throws FileUploadException;

    /**
     * 合并文件
     *
     * @param identifierMap 所有的task的identifier，包括单文件的合并与一个文件夹内所有文件的合并
     * @param folderId      父文件夹id
     * @throws FileUploadException
     */
    void mergeFiles(HashMap<String, String> identifierMap, Long folderId) throws FileUploadException;

    /**
     * 取消上传任务
     *
     * @param identifierList
     * @throws FileUploadException 这个请求必须确保任务已经建立，否则抛出该异常
     */
    public void cancelTask(List<String> identifierList);

}
