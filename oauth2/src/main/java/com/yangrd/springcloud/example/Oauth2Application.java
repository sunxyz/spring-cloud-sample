package com.yangrd.springcloud.example;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author yangrd
 * @date 2019/09/05
 */
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
    @EnableResourceServer
    public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            super.configure(resources);
        }

        /**
         * 用于配置对受保护的资源的访问规则
         * 默认情况下所有不在/oauth/**下的资源都是受保护的资源
         * {@link OAuth2WebSecurityExpressionHandler}
         */
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers("/userinfo")
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated();
        }
    }

    @EnableAuthorizationServer
    @Configuration
    public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        AuthenticationManager authenticationManager;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) {
            try {
                clients.inMemory()
                        .withClient("gateway")
                        .secret(passwordEncoder().encode("secret"))
                        .authorizedGrantTypes("authorization_code", "client_credentials", "password")
                        .scopes("any", "resource.read").resourceIds("oauth2-resource").redirectUris("http://127.0.0.1:8086/login/oauth2/code/gateway");
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
            return new JwtCustomHeadersAccessTokenConverter(keyPair());
        }

        @Bean
        public KeyPair keyPair() {
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("sample-jwt.jks"), "sample".toCharArray());
            return keyStoreKeyFactory.getKeyPair("sample-jwt");
        }

        @Bean
        public JWKSet jwkSet() {
            RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair().getPublic())
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyID("sample");
            return new JWKSet(builder.build());
        }
    }


    public class JwtCustomHeadersAccessTokenConverter extends JwtAccessTokenConverter {

        private JsonParser objectMapper = JsonParserFactory.create();
        final RsaSigner signer;

        public JwtCustomHeadersAccessTokenConverter(KeyPair keyPair) {
            super();
            super.setKeyPair(keyPair);
            this.signer = new RsaSigner((RSAPrivateKey) keyPair.getPrivate());
        }

        @Override
        protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            String content;
            try {
                content = this.objectMapper.formatMap(getAccessTokenConverter().convertAccessToken(accessToken, authentication));
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot convert access token to JSON", ex);
            }
            Map<String, String> customHeaders = Collections.singletonMap("kid", "sample");
            String token = JwtHelper.encode(content, this.signer, customHeaders)
                    .getEncoded();
            return token;
        }
    }

    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Bean
        @Override
        public UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(
                    User.withUserDetails(new User("user", passwordEncoder().encode("password"), Arrays.asList(new SimpleGrantedAuthority("user")))).build());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }


    }

    @Order(-3)
    @Configuration
    class OpenEndpointConfiguration extends AuthorizationServerSecurityConfiguration {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers()
                    .mvcMatchers("/.well-known/jwks.json", "/actuator/**")
                    .and()
                    .authorizeRequests()
                    .mvcMatchers("/.well-known/jwks.json", "/actuator/**").permitAll();
        }
    }

    @FrameworkEndpoint
    @Configuration
    class JwkSetEndpoint {
        JWKSet jwkSet;

        public JwkSetEndpoint(JWKSet jwkSet) {
            this.jwkSet = jwkSet;
        }

        @GetMapping("/.well-known/jwks.json")
        @ResponseBody
        public Map<String, Object> getKey() {
            return jwkSet.toJSONObject();
        }
    }


    @RestController
    public class MeController {

        @GetMapping("/userinfo")
        public Principal userInfo(Principal principal) {
            return principal;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Oauth2Application.class, args);
    }

}
