package com.mist.cloud.domain.tansmit.contoller;

import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.domain.file.repository.IFileRepository;
import com.mist.cloud.domain.file.service.IFileService;
import com.mist.cloud.domain.file.service.IFolderService;
import com.mist.cloud.domain.tansmit.service.IDownloadService;
import com.mist.cloud.domain.tansmit.service.TransmitSupport;
import com.mist.cloud.infrastructure.DO.File;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.ibatis.annotations.Lang;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    // TODO 设计大文件下载，这里必须得使用流式下载
    @GetMapping("/download")
    @ApiImplicitParam(name = "fileId", value = "文件(夹) id", dataTypeClass = Lang.class)
    public ResponseEntity<ByteArrayResource> download(@RequestParam("fileId") Long fileId, HttpServletResponse response) throws IOException {
        File file = fileRepository.findFile(fileId);
        // 文件下载
        if(file != null){
            // 获取真实的文件名
            String trueFileName = file.getOriginName();

            // 并添加下载记录
            downloadService.downloadFile(fileId);

            // 读取文件
            String filePath = fileConfig.getBase_path() + "/" + trueFileName;
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

            // 设置响应头，指定文件名和类型 文件名由前段控制
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", trueFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 返回响应实体
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayResource);
        } else {
            // 文件夹下载
            String zipSource = downloadService.downloadFolder(fileId);

            byte[] bytes = Files.readAllBytes(Paths.get(zipSource));
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

            // 设置响应头，指定文件名和类型 文件名由前段控制
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", zipSource.substring(zipSource.lastIndexOf('/'), zipSource.length()));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 返回响应实体
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayResource);
        }
    }
}
