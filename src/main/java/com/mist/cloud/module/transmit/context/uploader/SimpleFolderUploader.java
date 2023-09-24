package com.mist.cloud.module.transmit.context.uploader;

import cn.hutool.core.util.StrUtil;
import com.mist.cloud.module.file.repository.IFolderRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;

/**
 * @Author: securemist
 * @Datetime: 2023/9/24 20:42
 * @Description: 文件中的文件同时采用分片上传与简单上传的形式
 */
@Component
public class SimpleFolderUploader {
    @Resource
    private IFolderRepository folderRepository;

    /**
     * 对所有路径，创建对应的文件夹 返回路径底层文件夹id与路径的映射   ["/folderA/folderB" ---  3123121241412 ]
     *
     * @param pathSet
     * @param folderId
     * @return 返回 路径与底层文件夹的映射
     */
    public HashMap<String, Long> createFolders(Set<String> pathSet, Long folderId) {
        HashMap<String, Long> folderIdMap = new HashMap<>();
        for (String path : pathSet) {
            folderId = createPathIfAbsent(folderId, path.substring(0, path.lastIndexOf("/")));
            folderIdMap.put(path, folderId);
        }
        return folderIdMap;
    }

    /**
     * 判断指定文件夹是否存在指定路径，如果存在返回路径底层文件夹id，如果不存在就逐层创建
     *
     * @param folderId 文件夹Id
     * @param path     要创建的路径
     * @return
     */
    public Long createPathIfAbsent(Long folderId, String path) {
        for (String name : path.split("/")) {
            if (!StrUtil.isEmpty(name)) {
                folderId = folderRepository.createFolderIfAbsent(folderId, name);
            }
        }
        return folderId;
    }
}
