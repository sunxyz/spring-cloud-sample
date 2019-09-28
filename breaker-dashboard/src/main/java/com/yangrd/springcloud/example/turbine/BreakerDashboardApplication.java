package com.yangrd.springcloud.example.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * @author yangrd
 * @date 2019/09/28
 */
@EnableHystrixDashboard
@EnableTurbine
@SpringCloudApplication
public class BreakerDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(BreakerDashboardApplication.class, args);
    }

}
