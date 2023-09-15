package com.mist.cloud.module.transmit.model.req;

import lombok.*;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/8/21 20:52
 * @Description:
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MergeFileRequest implements Serializable {
    private IdentifierItem[] identifierList;
    private Long folderId;
}

