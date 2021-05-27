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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@RabbitListener(queues = "mail.insert")
public class InsertListener {
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    MailProperties mailProperties;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    MailLogMapper mailLogMapper;

    @RabbitHandler
    public void handler(Channel channel, String msg, Message message) throws IOException {
        Map<String, String> map = new ObjectMapper().readValue(msg, new TypeReference<Map<String, String>>() {
        });
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, false);
        //收到消息，发送邮件
        MimeMessage msg1 = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg1);
        try {
            helper.setTo(map.get("email"));
            helper.setFrom(mailProperties.getUsername());
            helper.setSubject("欢迎使用郭友光的资料仓库");
            helper.setSentDate(new Date());
            Context context = new Context();
            context.setVariable("name", map.get("username"));
            context.setVariable("password", map.get("password"));
            context.setVariable("phone", map.get("phone"));
            context.setVariable("email", map.get("email"));
            String mail = templateEngine.process("mail", context);
            helper.setText(mail, true);
            javaMailSender.send(msg1);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        MailLog mailLog = new MailLog("insert", date, map.get("email"));
        mailLogMapper.insert(mailLog);
    }
}
