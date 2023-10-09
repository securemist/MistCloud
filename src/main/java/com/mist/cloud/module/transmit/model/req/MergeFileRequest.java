package com.mist.cloud.module.transmit.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "合并文件请求类")
public class MergeFileRequest implements Serializable {
    @Schema(description = "所有合并的文件任务标识")
    private IdentifierItem[] identifierList;
    @Schema(description = "目标文件夹id")
    private Long folderId;
}

