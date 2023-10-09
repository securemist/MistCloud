package com.mist.cloud.module.share.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 14:48
 * @Description:
 */
@Data
@Schema(description = "转存文件请求类")
public class ResaveFileRequest {
    @Schema(description = "文件id集合")
    private List<Long> idList;
    @Schema(description = "目标文件夹id")
    private Long targetFolderId;
}
