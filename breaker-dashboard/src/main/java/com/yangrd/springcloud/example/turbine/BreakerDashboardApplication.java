package com.yangrd.springcloud.example.turbine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author yangrd
 * @date 2019/09/28
 */
@Controller
@EnableHystrixDashboard
@EnableTurbine
@SpringCloudApplication
public class BreakerDashboardApplication {


    @Value("${turbineUrl}")
    private String turbineUrl;

    @GetMapping
    public String index(){
        return "redirect:"+turbineUrl;
    }

    public static void main(String[] args) {
        SpringApplication.run(BreakerDashboardApplication.class, args);
    }

}
