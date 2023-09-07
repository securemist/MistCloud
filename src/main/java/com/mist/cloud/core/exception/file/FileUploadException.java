package com.mist.cloud.common.exception.file;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/8/16 16:29
 * @Description:
 */
public class FileUploadException extends BaseFileException {
    private List<String>  identifierList = new ArrayList<String>();

    private Exception e;

    public FileUploadException(String msg, String identifier, Exception e) {
        super(msg);
        this.identifierList.add(identifier);
        this.e = e;
    }

    public FileUploadException(String msg, String identifier) {
        super(msg);
        this.identifierList.add(identifier);
        this.e = new RuntimeException();
    }

    public FileUploadException(String msg, List<String> identifierList) {
        super(msg);
        this.identifierList = identifierList;
        this.e = new RuntimeException();
    }

    public List<String> getIdentifierList() {
        return identifierList;
    }
}
