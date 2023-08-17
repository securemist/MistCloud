package com.mist.cloud.config;

import cn.dev33.satoken.exception.NotLoginException;
import com.mist.cloud.common.Constants;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.exception.file.FileCommonException;
import com.mist.cloud.exception.file.FileException;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.exception.file.FolderException;
import com.mist.cloud.exception.RequestParmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 13:59
 * @Description: 同一异常处理
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandlerConfig {

    /**
     * 自定义异常处理
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = {RequestParmException.class})
    public Result customExceptionHandler(Exception e) {
        log.error(e.getClass() + ": {}", e.getMessage());
        return new FailedResult(e.getMessage());
    }


    /**
     * 统一异常处理
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result exceptionHandler(Exception e) {
        log.error(e.getClass() + ": {}", e.getMessage());
        return new FailedResult(Constants.DEFAULT_FAILED_MSG);
    }


    @ResponseBody
    @ExceptionHandler(value = NotLoginException.class)
    public Result authExceptionHandler(Exception e) {
        log.error(e.getClass() + ": {}", e.getMessage());
        return new FailedResult(Constants.DEFAULT_AUTH_CODE, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = FileException.class)
    public Result FileExceptionHandler(Exception e){
        // 文件上传失败要返回任务的 uid
        if (e instanceof FileUploadException){
            HashMap<String, String> map = new HashMap<>();
            map.put("uid",((FileUploadException) e).getTaskId());
            return new FailedResult(e.getMessage(), map);
        }

       return new FailedResult(e.getMessage());
    }

}
