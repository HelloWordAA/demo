package com.qf.util;

import com.qf.entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * 发送邮件的工具类
 */
@Component
public class MailUtil {
    @Autowired
    private JavaMailSender javaMailSender;
    /**
     * 从配置文件中找到发送方
     */
    @Value("${spring.mail.username}")
    private String from;
    public void sendEmail(Email email){
        //创建一封邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //包装邮件
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        System.out.println("from的值"+from);
        try {
            //设置标题
            mimeMessageHelper.setSubject(email.getSubject());
            //设置发送方，from表示从配置文件中查找的发送方，也可以直接写
            mimeMessageHelper.setFrom(from,"腾讯官方邮件");
            //设置接收方
            mimeMessageHelper.setTo(email.getTo());
//            mimeMessageHelper.setCc("");    //设置抄送方
//            mimeMessageHelper.setBcc("");   //设置密送方
            //设置内容，是否按html格式解析
            mimeMessageHelper.setText(email.getContent(),true);
            //设置发送时间，当前时间
            mimeMessageHelper.setSentDate(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        javaMailSender.send(mimeMessage);
    }
}
