package com.yangrd.springcloud.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Arrays;

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
        RestTemplate restTemplate = new RestTemplate();


        ClientHttpRequestInterceptor clientHttpRequestInterceptor = new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                String authorization = attributes.getRequest().getHeader("Authorization");
                HttpHeaders headers = request.getHeaders();
                headers.add("Authorization", authorization);
                return execution.execute(request, body);
            }
        };
        restTemplate.setInterceptors(Arrays.asList(clientHttpRequestInterceptor));
        String callServiceResult = restTemplate.getForObject(serviceInstance.getUri().toString() + "/userinfo", String.class);
        System.out.println(callServiceResult);
        return callServiceResult;
    }

    public String callFallback(){
        return "fail";
    }
}
