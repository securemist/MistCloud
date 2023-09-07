package com.mist.cloud.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.Executor;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 20:21
 * @Description:
 */
@Configuration
@EnableWebMvc
public class WebConfig implements AsyncConfigurer {

    /**
     * 执行异步请求的任务执行器，用来实现文件下载，异步分片的形式读取文件并返回给前端
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}
