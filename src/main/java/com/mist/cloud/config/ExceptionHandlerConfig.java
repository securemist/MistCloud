package com.mist.cloud.config;

import cn.dev33.satoken.exception.NotLoginException;
import com.mist.cloud.common.Constants;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.config.context.DefaultFileUploadContext;
import com.mist.cloud.config.context.Task;
import com.mist.cloud.config.context.UploadTaskContext;
import com.mist.cloud.exception.file.FileCommonException;
import com.mist.cloud.exception.file.FileException;
import com.mist.cloud.exception.file.FileUploadException;
import com.mist.cloud.exception.file.FolderException;
import com.mist.cloud.exception.RequestParmException;
import com.mist.cloud.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @Author: securemist
 * @Datetime: 2023/7/24 13:59
 * @Description: 同一异常处理
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandlerConfig {
    @Resource(type = DefaultFileUploadContext.class)
    private UploadTaskContext uploadTaskContext;

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
        if (!(e instanceof NotLoginException)) {
            log.error(e.getClass() + ": {}", e.getMessage());
        }
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
    public Result FileExceptionHandler(Exception e) throws IOException {
        // 文件上传失败要返回任务的 uid
        if (e instanceof FileUploadException) {
            String identifier = ((FileUploadException) e).getIdentifier();


            // 删除上传过程产生的所有文件
            Task task = uploadTaskContext.getTask(identifier);
            FileUtils.deleteDirectoryIfExist(Paths.get(task.getFolderPath()));
            Files.deleteIfExists(Paths.get(task.getTargetFilePath()));

            // 从任务列表删除该任务
            try {
                uploadTaskContext.cancelTask(identifier);
            } catch (FileUploadException ex) {

            }

            log.error("文件上传失败: {} , {}", ((FileUploadException) e).getMsg(), e.getMessage());

            // 构造返回信息
            HashMap<String, String> map = new HashMap<>();
            map.put("identifier", identifier);
            return new FailedResult(e.getMessage(), map);
        }

        return new FailedResult(e.getMessage());

    }

}
