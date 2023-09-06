package com.mist.cloud.aggregate.user.service;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:01
 * @Description:
 */
@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    private ConcurrentHashMap<String, String> mailCodeMap = new ConcurrentHashMap<>();

    public boolean verify(String email, String mailCode) {
        String code = mailCodeMap.get(email);
        mailCodeMap.remove(mailCode);

        if (code == null || !code.equals(mailCode)) {
            return false;
        }

        return true;
    }

    public void sendMail(String email) {
        String code = generateCode();
        // 存入map TODO 这里需要设置过期时间

        mailCodeMap.put(email, code);
        String text = "这是您的验证码(5分钟有效)：" + code;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setCc(email);
        message.setSubject("验证码");
        message.setText(text);
        mailSender.send(message);
    }

    // 随机生成五位数邮箱验证码
    private String generateCode() {
        // 生成随机数
        Random random = new Random();
        int code = random.nextInt(90000) + 10000; // 生成 10000 到 99999 之间的随机数

        return String.valueOf(code);
    }
}
