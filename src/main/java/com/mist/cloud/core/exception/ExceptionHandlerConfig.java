package com.mist.cloud.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.core.constant.ResponseCode;
import com.mist.cloud.core.exception.auth.RegisterException;
import com.mist.cloud.core.exception.file.FileException;
import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.transmit.context.UploadTaskContext;
import com.mist.cloud.core.exception.file.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 13:59
 * @Description: 同一异常处理
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandlerConfig {
    @Resource
    private UploadTaskContext uploadTaskContext;
    @Resource
    private FileConfig fileConfig;

    /**
     * 自定义异常处理
     */
    @ResponseBody
    @ExceptionHandler(value = {RequestParmException.class})
    public R customExceptionHandler(Exception e) {
        log.error(e.getClass() + ": {}", e.getMessage());
        return R.error(e.getMessage());
    }


    /**
     * 统一异常处理
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public R exceptionHandler(Exception e) {
        e.printStackTrace();
        return R.error();
    }


    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public R authExceptionHandler(Exception e, HttpServletResponse response) {
        return R.error(ResponseCode.LOGIN_FAILED);
    }

    @ExceptionHandler(value = {RegisterException.class, ShareException.class, FolderException.class, FileException.class})
    @ResponseBody
    public R RegisterExceptionHandler(Exception e) {
        return R.error(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = FileUploadException.class)
    public R FileExceptionHandler(FileUploadException e) throws IOException {
        List<String> identifierList = e.getIdentifierList();
        // 文件上传失败，清除所有的残余文件
        uploadTaskContext.cancelTask(identifierList);
        return R.error(e.getMessage());
    }

}
