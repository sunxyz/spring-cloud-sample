package com.yangrd.springcloud.example;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UserClient
 *
 * @author yangrd
 * @date 2019/09/05
 */
//@FeignClient(name = "user-service", fallback = UserClientFallback.class )
@FeignClient(name = "user-service", fallbackFactory = HystrixUserClientFallbackFactory.class)
public interface UserClient {

    /**
     * 呼叫
     * @return
     */
    @GetMapping("/")
    String call();

    /**
     *测试
     * @param params
     * @return
     */
    @GetMapping("/")
    String demo(@SpringQueryMap Params params);

    @GetMapping("/fail")
    String fail();

    @Data
     class Params{

         private String param1;

         private String param2;

    }
}
