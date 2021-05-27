package com.light.storagemail.receiver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.light.storagemail.entity.MailLog;
import com.light.storagemail.mapper.MailLogMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@RabbitListener(queues = "mail.findPass")
public class FindPassListener {

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    MailProperties mailProperties;
    @Autowired
    MailLogMapper mailLogMapper;


    @RabbitHandler
    public void handler(Channel channel, String msg, Message message) throws IOException {
        Map<String, String> map = new ObjectMapper().readValue(msg, new TypeReference<Map<String, String>>() {});
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setSubject("验证码");
        mailMessage.setText("验证码: "+map.get("code")+"\n"+"此验证码用于重置密码，如不是本人，请忽略");
        mailMessage.setTo(map.get("email"));
        mailMessage.setFrom(mailProperties.getUsername());
        javaMailSender.send(mailMessage);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        MailLog mailLog = new MailLog("findPass", date, map.get("email"));
        mailLogMapper.insert(mailLog);
        channel.basicAck(deliveryTag, false);
    }

}
