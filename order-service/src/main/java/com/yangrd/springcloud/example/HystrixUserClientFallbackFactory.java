package com.yangrd.springcloud.example;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * HystrixUserClientFallbackFactory
 *
 * @author yangrd
 * @date 2019/09/24
 */
@Slf4j
@Component
public class HystrixUserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        if (cause instanceof FeignException){
            log.error("[{} , {}]", cause.getMessage() ,((FeignException)cause).contentUTF8() );
        }else {
            log.error("[{}]", cause.getMessage() );
        }

        return new UserClient() {
            @Override
            public String call() {
                return "user-service fail !! " +cause.getMessage();
            }

            @Override
            public String demo(Params params) {
                return "user-service fail !! " +cause.getMessage();
            }

            @Override
            public String fail() {

                return "fail";
            }
        };


    }
}
