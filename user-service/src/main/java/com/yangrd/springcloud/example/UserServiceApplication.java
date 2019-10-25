package com.yangrd.springcloud.example;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author yangrd
 * @date 2019/09/02
 */
@Slf4j
@RestController
@SpringCloudApplication
public class UserServiceApplication {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config();
    }

    @GetMapping("/")
    public String echo() {
        return "hi i am user service!!!";
    }

    @GetMapping("/fail")
    public String error() {
        log.error("[{}]用户不存在", 11);
        throw new RuntimeException();
    }

    @RestController
    public class MeController {

        @GetMapping("/me")
        public Principal me(Principal principal) {
            return principal;
        }

        @GetMapping("/whoami")
        public Object whoami(@AuthenticationPrincipal Object name) {
            return name;
        }

        @GetMapping("/userinfo")
        public Principal userInfo(Principal principal) {
            return principal;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


}
