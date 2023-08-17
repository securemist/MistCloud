package com.mist.cloud;

import com.mist.cloud.config.FileConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
class MistCloudApplicationTests {
    @Resource
    private FileConfig fileConfig;
    @Test
    void contextLoads() {
        System.out.println(fileConfig.getBase_path());
    }

}
