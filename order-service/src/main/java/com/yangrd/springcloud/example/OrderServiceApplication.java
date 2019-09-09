package com.yangrd.springcloud.example;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangrd
 * @date 2019/09/02
 */
@RestController
@EnableConfigurationProperties
@EnableFeignClients
@SpringCloudApplication
public class OrderServiceApplication {


    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config();
    }

    @GetMapping("/")
    public String echo(){
        return "hi i am order service!!!";
    }


    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
