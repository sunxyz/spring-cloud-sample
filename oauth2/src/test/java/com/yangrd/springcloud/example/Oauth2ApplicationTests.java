package com.yangrd.springcloud.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Oauth2ApplicationTests {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void print(){
        System.out.println(passwordEncoder.encode("admin"));
    }

    @Test
    public void contextLoads() {
        System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("admin"));
    }


}
