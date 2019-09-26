package com.yangrd.springcloud.example;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * HystrixUserClientFallbackFactory
 *
 * @author yangrd
 * @date 2019/09/24
 */
@Component
public class HystrixUserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public String call() {
                return "user-service fail !! " +cause.getMessage();
            }

            @Override
            public String demo(Params params) {
                return "user-service fail !! " +cause.getMessage();
            }
        };


    }
}
