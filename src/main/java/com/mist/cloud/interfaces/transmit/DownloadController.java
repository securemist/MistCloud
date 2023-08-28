package com.mist.cloud.interfaces.transmit;

import com.mist.cloud.aggregate.tansmit.service.IDownloadService;
import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.aggregate.file.repository.IFileRepository;
import com.mist.cloud.infrastructure.DO.File;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.ibatis.annotations.Lang;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 13:12
 * @Description:
 */
@RestController
public class DownloadController {
    @Resource
    private FileConfig fileConfig;
    @Resource
    private IDownloadService downloadService;
    @Resource
    private IFileRepository fileRepository;


    /**
     * 使用  StreamingResponseBody 处理异步 IO 流，会把流异步写入到 response 的输出流中返回并且不会占用 Servlet 容器线程
     */

    @GetMapping("/download")
    @ApiImplicitParam(name = "id", value = "文件(夹) id", dataTypeClass = Lang.class)
    public ResponseEntity<StreamingResponseBody> download2(@RequestParam("id") Long fileId, HttpServletResponse response) throws IOException {
        File file = fileRepository.findFile(fileId);
        String filePath = "";
        // 文件下载
        if (file != null) {
            // 获取真实的文件名
            String trueFileName = file.getOriginName();
            // 文件下载
            downloadService.downloadFile(fileId);
            filePath = fileConfig.getBasePath() + "/" + trueFileName;
        } else {
            // 文件夹下载
            filePath = downloadService.downloadFolder(fileId);
        }

        java.io.File fileSource = new java.io.File(filePath);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileSource.getName());

        // 创建 StreamingResponseBody
        StreamingResponseBody responseBody = outputStream -> {
            try {
                // 使用 Files.copy 方法将文件内容写入输出流
                Files.copy(fileSource.toPath(), outputStream);
            } catch (IOException e) {
                // 处理异常
                e.printStackTrace();
            }
        };

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }
}
