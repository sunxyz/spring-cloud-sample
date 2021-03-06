package com.yangrd.springcloud.example;

import org.springframework.stereotype.Component;

/**
 * UserClientFallback
 *
 * @author yangrd
 * @date 2019/09/05
 */
@Component
public class UserClientFallback implements UserClient {
    @Override
    public String call() {
        return "fail feign !";
    }

    @Override
    public String demo(Params params) {
        return null;
    }

    @Override
    public String fail() {
        return "fail";
    }
}
