package com.mist.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@MapperScan("com.mist.cloud.infrastructure.mapper")
@EnableOpenApi
public class MistCloudApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(MistCloudApplication.class, args);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

}
