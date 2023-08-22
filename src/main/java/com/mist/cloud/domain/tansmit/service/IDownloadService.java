package com.mist.cloud.domain.tansmit.service;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 13:31
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
