package com.yangrd.springcloud.example;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UserClient
 *
 * @author yangrd
 * @date 2019/09/05
 */
@FeignClient(name = "user-service", fallback = UserClientFallback.class )
public interface UserClient {

    /**
     * 呼叫
     * @return
     */
    @GetMapping("/")
    String call();
}
