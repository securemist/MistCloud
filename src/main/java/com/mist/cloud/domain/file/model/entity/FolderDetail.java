package com.mist.cloud.domain.file.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.infrastructure.DO.Folder;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
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
//    private List<FolderDetail.Folder> folderList;

    /**
     * 文件列表
     */
    private List<FolderDetail.File> fileList;

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    public static class FolderPathItem implements Serializable{
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;
        private String name;
    }

    @Setter
    @Getter
    @Builder
    public static class Folder implements Serializable {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;

        private String name;

        private Date modifyTime;
    }

    @Setter
    @Getter
    @Builder
    public static class File {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;

        private String name;

        private Long size;

        private Boolean isFolder;

        private Date modifyTime;
    }
}
