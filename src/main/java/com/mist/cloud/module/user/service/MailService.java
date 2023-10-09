package com.mist.cloud.module.user.service;

import com.mist.cloud.core.exception.auth.RegisterException;
import com.mist.cloud.module.user.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: securemist
 * @Datetime: 2023/9/6 16:01
 * @Description:
 */
@Service
@Slf4j
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Resource
    private IUserRepository userRepository;
    private ConcurrentHashMap<String, String> mailCodeMap = new ConcurrentHashMap<>();

    public void verify(String email, String captcha) {
        String code = mailCodeMap.get(email);

        if (code == null || !code.equals(captcha)) {
            throw new RegisterException("验证码错误");
        }

        mailCodeMap.remove(captcha);
    }

    public void sendMail(String email) {

        String code = generateCode();
        // 存入map TODO 这里需要设置过期时间

        mailCodeMap.put(email, code);
        String text = "这是您的验证码(5分钟有效)：" + code;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setCc(email);
            message.setSubject("验证码");
            message.setText(text);
            mailSender.send(message);
        } catch (MailSendException e) {
            throw new RegisterException("该邮箱不存在!");
        }

    }

    // 随机生成五位数邮箱验证码
    private String generateCode() {
        // 生成随机数
        Random random = new Random();
        int code = random.nextInt(90000) + 10000; // 生成 10000 到 99999 之间的随机数

        return String.valueOf(code);
    }
}
