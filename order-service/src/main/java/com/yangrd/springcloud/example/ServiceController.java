package com.yangrd.springcloud.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ServiceController
 *
 * @author yangrd
 * @date 2019/09/02
 */
@RestController
public class ServiceController {

    @Autowired
    private LoadBalancerClient loadBalancer;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private Registration registration;
    @Autowired
    private Environment env;

    @Autowired
    private DemoHystrixCommandService demoHystrixCommandService;

    @Autowired
    private UserClient userClient;

    @GetMapping("/me")
    public ServiceInstance me() {
        return this.registration;
    }

    @GetMapping("/env")
    public String env(@RequestParam("prop") String prop) {
        return this.env.getProperty(prop, "Not Found");
    }
    /**
     * 获取所有服务
     */
    @GetMapping("/services")
    public Object services() {
        return discoveryClient.getInstances("user-service");
    }

    /**
     * 从所有服务中选择一个服务（轮询）
     */
    @GetMapping("/discover")
    public Object discover() {
        return loadBalancer.choose("user-service").getUri().toString();
    }

    @GetMapping("/call")

    public String call() {
        return demoHystrixCommandService.getUserInfo();
    }


    @GetMapping("/call2")

    public String call2() {
        return userClient.call();
    }


    @Autowired
    private SampleProperties sampleProperties;


    @GetMapping("/prop")
    public String getProp(){
        return sampleProperties.getProp();
    }

}
