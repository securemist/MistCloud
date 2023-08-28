package com.mist.cloud.aggregate.tansmit.service;

/**
 * @Author: securemist
 * @Datetime: 2023/8/27 10:22
 * @Description:
 */
public interface IDownloadService {
    /**
     * 下载文件夹
     * @param folderId
     * @return
     */
    String downloadFolder(Long folderId);

    /**
     * 下载文件
     * @param fileId
     * @return
     */
    String downloadFile(Long fileId);
}
