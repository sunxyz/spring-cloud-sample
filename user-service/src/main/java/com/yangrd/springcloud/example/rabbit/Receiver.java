package com.yangrd.springcloud.example.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Receiver
 *
 * @author yangrd
 * @date 2019/11/09
 */
@Component
//@RabbitListener(queues = "hello")
public class Receiver {

//    @RabbitHandler
    public void process(String hello) {
        System.out.println("Receiver : " + hello);
    }

}
