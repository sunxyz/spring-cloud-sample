package com.yangrd.springcloud.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * DemoHystrixCommandService
 *
 * @author yangrd
 * @date 2019/09/05
 */
@Service
public class DemoHystrixCommandService {

    @Autowired
    private LoadBalancerClient loadBalancer;

    @HystrixCommand(fallbackMethod = "callFallback")
    public String getUserInfo() {
        ServiceInstance serviceInstance = loadBalancer.choose("user-service");
        System.out.println("服务地址：" + serviceInstance.getUri());
        System.out.println("服务名称：" + serviceInstance.getServiceId());

        String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/", String.class);
        System.out.println(callServiceResult);
        return callServiceResult;
    }

    public String callFallback(){
        return "fail";
    }
}
