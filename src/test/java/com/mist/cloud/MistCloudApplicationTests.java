package com.mist.cloud;

import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.domain.tansmit.service.IUploadSevice;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
class MistCloudApplicationTests {
    @Resource
    private FileConfig fileConfig;
    @Test
    void contextLoads() {
        System.out.println(fileConfig.getBase_path());
    }

    @Resource
    private IUploadSevice uploadSevice;

    @Test
    void test_addFolder(){
        Set<String> set = new HashSet<>();
        set.add("/全部文件/照片");
        set.add("/全部文件/视频/满江红");
        set.add("/全部文件/文件");
        set.add("/Redis");
        set.add("/docker/mysql");

        uploadSevice.addFolder(1L,set);


    }
}
