package com.qf.listener;

import com.qf.entity.Email;
import com.qf.util.MailUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {
    @Autowired
    private MailUtil mailUtil;
    @RabbitListener(queues = "email_queue")
    public void emailHandler(Email email){
        System.out.println("监听到的消息"+email);
        try{
            mailUtil.sendEmail(email);
        }catch (Exception e){
            System.out.println("邮件发送出错");
            e.printStackTrace();
        }

    }
}
