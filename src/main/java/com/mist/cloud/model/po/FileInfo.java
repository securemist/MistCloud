package com.mist.cloud.model.po;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 09:40
 * @Description:
 */
@Data
@Getter
@Setter
public class FileInfo implements Serializable {
    @Id
    private Long id;

    private String filename;

    private String identifier;

    private Long totalSize;

    private String type;

    private String location;
}
