package com.mist.cloud.aggregate.file.model.tree;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 11:01
 * @Description:
 */

public class FolderTreeNode implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    public Long id;
    public String name;
    public List<FolderTreeNode> children = new ArrayList<>();

    public FolderTreeNode(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
