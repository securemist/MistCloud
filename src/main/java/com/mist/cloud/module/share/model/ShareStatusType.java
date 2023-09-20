package com.mist.cloud.module.share.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ShareStatusType {
    PERMANENT("永久有效"),
    EXPIRED("分享已过期"),
    OK("未过期"),
    DELETED("分享的文件已被删除");
    private String msg;
}
