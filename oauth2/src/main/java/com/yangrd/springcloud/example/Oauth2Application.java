package com.yangrd.springcloud.example;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author yangrd
 * @date 2019/09/05
 */
@SpringCloudApplication
public class Oauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2Application.class, args);
    }

}
