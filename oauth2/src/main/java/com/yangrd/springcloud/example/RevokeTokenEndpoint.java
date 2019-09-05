package com.yangrd.springcloud.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * RevokeTokenEndpoint
 *
 * @author yangrd
 * @date 2019/09/05
 */
@FrameworkEndpoint
public class RevokeTokenEndpoint {

    @Autowired
    @Qualifier("consumerTokenServices")
    ConsumerTokenServices consumerTokenServices;

    @DeleteMapping("/oauth/token/{accessToken}")
    @ResponseBody
    public String revokeToken(@PathVariable String accessToken) {
        if (consumerTokenServices.revokeToken(accessToken)){
            return "注销成功";
        }else{
            return "注销失败";
        }
    }
}
