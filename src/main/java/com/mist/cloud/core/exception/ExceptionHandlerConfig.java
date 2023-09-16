package com.mist.cloud.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.io.FileUtil;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.core.exception.auth.RegisterException;
import com.mist.cloud.core.result.FailedResult;
import com.mist.cloud.core.result.R;
import com.mist.cloud.core.result.Result;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.context.UploadTaskContext;
import com.mist.cloud.core.exception.file.BaseFileException;
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
        e.printStackTrace();
        return new FailedResult(Constants.DEFAULT_FAILED_MSG);
    }


    @ExceptionHandler(value = NotLoginException.class)
    public HttpServletResponse authExceptionHandler(Exception e, HttpServletResponse response) {
        log.debug("未登陆: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return response;
    }

    @ExceptionHandler(value = RegisterException.class)
    @ResponseBody
    public R RegisterExceptionHandler(RegisterException e) {
        return R.error(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = BaseFileException.class)
    public R FileExceptionHandler(BaseFileException e) throws IOException {
        if (e instanceof FileUploadException) {
            List<String> identifierList = ((FileUploadException) e).getIdentifierList();
            // 文件上传失败，清除所有的残余文件
            uploadTaskContext.cancelTask(identifierList);
        }
        return R.error(e.getMsg());
    }

}
