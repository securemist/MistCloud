package com.mist.cloud.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.common.constant.Constants;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.aggregate.tansmit.context.DefaultFileUploadContext;
import com.mist.cloud.aggregate.tansmit.context.Task;
import com.mist.cloud.aggregate.tansmit.context.UploadTaskContext;
import com.mist.cloud.common.exception.file.BaseFileException;
import com.mist.cloud.common.exception.file.FileUploadException;
import com.mist.cloud.common.exception.RequestParmException;
import com.mist.cloud.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        if (!(e instanceof NotLoginException)) {
            log.error(e.getClass() + ": {}", e.getMessage());
        }
        return new FailedResult(Constants.DEFAULT_FAILED_MSG);
    }


    @ExceptionHandler(value = NotLoginException.class)
    public HttpServletResponse authExceptionHandler(Exception e, HttpServletResponse response) {
        log.debug(e.getClass() + ": {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return response;
//        return new FailedResult(Constants.DEFAULT_AUTH_CODE, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = BaseFileException.class)
    public Result FileExceptionHandler(BaseFileException e) throws IOException {
        if (e instanceof FileUploadException) {
            List<String> identifierList = ((FileUploadException) e).getIdentifierList();
            for (String identifier : identifierList) {
                // 删除上传过程产生的所有有关文件
                Task task = null;
                try {
                    task = uploadTaskContext.getTask(identifier);
                } catch (FileUploadException ex) {
                    return new FailedResult();
                }

                FileUtils.deleteDirectoryIfExist(Paths.get(fileConfig.getUploadPath() + "/" + task.getFolderPath()));
                Files.deleteIfExists(Paths.get(fileConfig.getBasePath() + "/" + task.getTargetFilePath()));

                // 从任务列表删除该任务
                try {
                    uploadTaskContext.cancelTask(identifier);
                } catch (FileUploadException ex) {

                }

                log.error("文件上传失败: {} , {}", ((FileUploadException) e).getMsg(), e.getMessage());
            }

            // 构造返回信息
            return new FailedResult("文件上传失败");
        }

        return new FailedResult(e.getMsg());

    }

}
