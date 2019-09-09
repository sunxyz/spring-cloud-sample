package com.yangrd.springcloud.example;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;

/**
 * @author yangrd
 * @date 2019/09/05
 */
@EnableAuthorizationServer
@EnableResourceServer
@SpringCloudApplication
public class Oauth2Application {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Configuration
    public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        AuthenticationManager authenticationManager;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) {
            try {
                clients.inMemory()
                        .withClient("client")
                        .secret(passwordEncoder().encode("secret"))
                        .authorizedGrantTypes("authorization_code", "client_credentials", "password")
                        .scopes("any").redirectUris("http://localhost:8080/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            // @formatter:off
            endpoints
                    .authenticationManager(this.authenticationManager)
                    .accessTokenConverter(accessTokenConverter())
                    .tokenStore(tokenStore());
            // @formatter:on
        }

        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(accessTokenConverter());
        }

        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
            // 配置jks文件
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setKeyPair(keyPair());
            return converter;
        }

        @Bean
        public KeyPair keyPair() {
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("sample-jwt.jks"), "sample".toCharArray());
            return keyStoreKeyFactory.getKeyPair("sample-jwt");
        }
    }

    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Bean
        @Override
        public UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(
                    User.withUserDetails(new User("user", passwordEncoder().encode("password"), Collections.emptyList())).build());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Order(-3)
    @Configuration
    class JwkSetEndpointConfiguration extends AuthorizationServerSecurityConfiguration {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http
                    .requestMatchers()
                    .mvcMatchers("/.well-known/jwks.json")
                    .and()
                    .authorizeRequests()
                    .mvcMatchers("/.well-known/jwks.json").permitAll();
        }
    }

    @FrameworkEndpoint
    class JwkSetEndpoint {
        KeyPair keyPair;

        public JwkSetEndpoint(KeyPair keyPair) {
            this.keyPair = keyPair;
        }

        @GetMapping("/.well-known/jwks.json")
        @ResponseBody
        public Map<String, Object> getKey() {
            RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();
            RSAKey key = new RSAKey.Builder(publicKey).build();
            return new JWKSet(key).toJSONObject();
        }
    }

    @Import(AuthorizationServerEndpointsConfiguration.class)
    @Configuration
    public class JwkSetConfiguration extends AuthorizationServerConfigurerAdapter {

        // ... the rest of the configuration from the previous section
    }

    @Order(-2)
    @Configuration
    public class ActuatorConfig extends AuthorizationServerSecurityConfiguration {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.requestMatchers()
                    .mvcMatchers("/actuator/**")
                    .and().authorizeRequests().mvcMatchers("/actuator/**").permitAll();
        }
    }

    @RestController
    public class MeController {

        @GetMapping("/me")
        public Principal me(Principal principal) {
            return principal;
        }

        @GetMapping("/whoami")
        public Object whoami(@AuthenticationPrincipal Object name) {
            return name;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Oauth2Application.class, args);
    }

}
