package com.yangrd.springcloud.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangrd
 * @date 2019/09/02
 */
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class UserServiceApplication {

    @GetMapping("/")
    public String echo(){
        return "hi i am user service!!!";
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
