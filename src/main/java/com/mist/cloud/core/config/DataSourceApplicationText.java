package com.mist.cloud.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;


/**
 * @Author: securemist
 * @Datetime: 2023/8/28 13:42
 * @Description: 在springboot启动的时候进行一些初始化工作
 */
@Component
@Slf4j
public class DataSourceApplicationText implements ApplicationContextAware {

    private static ApplicationContext context;

    @Value("${file.base_path}")
    private String fileBasePath;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            // 检验数据库连接
            context = applicationContext;
            DataSource dataSource = (DataSource) context.getBean("dataSource");
            dataSource.getConnection().close();
            log.info("database connect success");

            // 创建文件存储目录
            File dir = new File(fileBasePath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    Files.createDirectory(Paths.get(fileBasePath + "/file"));
                    Files.createDirectory(Paths.get(fileBasePath + "/download"));
                    Files.createDirectory(Paths.get(fileBasePath + "/upload"));
                } else {
                    throw new IOException();
                }
            }

            log.info("The mist-cloud base file path create success : {}", fileBasePath);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("database connect failed, {}", e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("The mist-cloud  file base path {} create failed {}", fileBasePath, e.getMessage());
        }
    }


    public static ApplicationContext getContext() {
        return context;
    }
}
