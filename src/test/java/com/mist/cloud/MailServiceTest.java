package com.mist.cloud;

import com.mist.cloud.aggregate.user.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 18:26
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class MailServiceTest {
    @Resource
    private MailService mailService;

    @Test
    public void test() {
        mailService.sendMail("securemist.top@gmail.com");
    }
}
