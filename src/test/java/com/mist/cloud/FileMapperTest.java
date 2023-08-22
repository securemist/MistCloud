package com.mist.cloud;

import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.infrastructure.dao.FileMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 14:43
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FileMapperTest {
    @Resource
    private FileMapper fileMapper;
    @Resource
    private FileConfig fileConfig;

    @Test
    public void test_insert() {

        System.out.println(fileConfig.getBase_path());
        System.out.println(2);
    }


    @Test
    public void test_string(){
        String a = "/application/set/asdd";
        System.out.println(a.substring(0,a.lastIndexOf("/")));
    }
}
