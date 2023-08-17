package com.mist.cloud.model.pojo;

import com.mist.cloud.model.po.File;
import com.mist.cloud.model.po.Folder;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/19 10:28
 * @Description:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FolderDetail {

    /**
     * 文件名
     */
    private String name;
    /**
     * 文件夹的所处路径，数组最后一项为当前文件夹，每一项为[id, name]
     */
    private List<FolderDetail.FolderPathItem> path;

    /**
     * 子文件夹列表
     */
    private List<Folder> folderList;


    /**
     * 文件列表
     */
    private List<File> fileList;

    @AllArgsConstructor
    @Setter
    @Getter
    @ToString
    public static
    class FolderPathItem {
        private String id;
        private String name;
    }
}
