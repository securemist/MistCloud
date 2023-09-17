package com.mist.cloud.module.transmit.controller;

import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.transmit.service.DownloadContext;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.ibatis.annotations.Lang;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    private DownloadContext downloadContext;
    @Resource
    private IFileRepository fileRepository;
    @Resource
    private IFolderRepository folderRepository;


    @GetMapping("/download")
    @ApiImplicitParam(name = "id", value = "文件(夹) id", dataTypeClass = Lang.class)
    public void download(@RequestParam("id") Long fileId, HttpServletResponse response) throws IOException {
        String filePath = "";
        boolean isFolder = fileRepository.isFolder(fileId);
        String fileName = "";
        // 文件下载
        if (!isFolder) {
            filePath = downloadContext.downloadFile(fileId);
            fileName = fileRepository.findFile(fileId).getName();
        } else {
            filePath = downloadContext.downloadFolder(fileId);
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        }

        java.io.File fileSource = new java.io.File(filePath);

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build().toString());

        try (FileInputStream inputStream = new FileInputStream(fileSource);) { // try-with-resources
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
