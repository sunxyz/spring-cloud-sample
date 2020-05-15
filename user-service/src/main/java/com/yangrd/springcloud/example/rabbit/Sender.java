package com.yangrd.springcloud.example.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Sender
 *
 * @author yangrd
 * @date 2019/11/09
 */
@Component
public class Sender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(int i) {
        String context = "hello " + new Date();
//        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("hello", new RabbitMsg("hello-"+i, context).toString());
    }

}
