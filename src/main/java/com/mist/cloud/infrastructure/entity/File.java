package com.mist.cloud.infrastructure.entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 14:20
 * @Description:
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("文件实体类")
public class File {

    @ApiModelProperty(value = "全局唯一 Id")
    @JsonSerialize(using = ToStringSerializer.class) // 解决前端64 位数精度丢失问题，序列化为字符串
    private Long id;

    @ApiModelProperty(value = "所在文件夹 ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long folderId;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "文件大小")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "真实文件名")
    private String originName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否删除", notes = "0为正常，1 为已删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间")
    private Date deletedTime;

    @ApiModelProperty(value = "重名次数")
    private Integer duplicateTimes;

    @ApiModelProperty(value = "文件 md5 值")
    private String md5;

}
