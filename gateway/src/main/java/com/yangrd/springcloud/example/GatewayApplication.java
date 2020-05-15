package com.yangrd.springcloud.example;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yangrd
 * @date 2019/09/05
 */
@Slf4j

@RestController
@SpringCloudApplication
public class GatewayApplication {

    @GetMapping("/")
    public Map<String, Object> index(
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> result = new HashMap<>(6);
        result.put("userName", oauth2User.getName());
        result.put("clientName", authorizedClient.getClientRegistration().getClientName());
        result.put("userAttributes", oauth2User.getAttributes());
        return result;
    }


    @RestController
    public class MeController {

        @GetMapping("/me")
        public Principal me(Principal principal) {
            return principal;
        }

//        @GetMapping("/whoami")
//        public Object whoami(@AuthenticationPrincipal Object name) {
//            return name;
//        }

        @GetMapping("/userinfo")
        public Principal userInfo(Principal principal) {
            return principal;
        }
    }


    @Bean
    @Order(-10)
    public GlobalFilter preOauth2SSO() {
        return (exchange, chain) -> {
            log.info("first pre filter");
            return ReactiveSecurityContextHolder.getContext()
                    .filter(Objects::nonNull)
                    .map(securityContext -> securityContext.getAuthentication())
                    .filter(authentication -> authentication instanceof OAuth2AuthenticationToken)
                    .map(authentication -> (OAuth2AuthenticationToken) authentication)
                    .map(oAuth2Authentication -> oAuth2Authentication.getPrincipal())
                    .filter(oAuth2User -> Objects.nonNull(oAuth2User) && oAuth2User instanceof DefaultOAuth2User)
                    .map(o -> (DefaultOAuth2User) o)
                    .map(jwtOAuth2User -> {
                        return ((Map) jwtOAuth2User.getAttributes().get("details")).get("tokenValue");
                    })
                    .map(bearerToken -> {
                        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                        builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
                        ServerHttpRequest request = builder.build();
                        return exchange.mutate().request(request).build();
                    })
                    .defaultIfEmpty(exchange)
                    .flatMap(chain::filter);
        };
    }


    @EnableWebFluxSecurity

    public class WebSecurityConfig {


        @Bean
        SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception {

            return http.authorizeExchange().pathMatchers("/actuator/**").permitAll()
                    .anyExchange().authenticated().and().oauth2Login().and().build();

        }

    }


    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
