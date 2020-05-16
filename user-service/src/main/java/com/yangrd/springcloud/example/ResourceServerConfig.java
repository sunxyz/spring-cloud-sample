package com.yangrd.springcloud.example;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * ResourceServerConfig
 *
 * @author yangrd
 * @date 2019/09/19
 */

@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("user-resource");
    }

    /**
     * 用于配置对受保护的资源的访问规则
     * 默认情况下所有不在/oauth/**下的资源都是受保护的资源
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .authorizeRequests().mvcMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        (http.authorizeRequests().and()).addFilterAfter(new JwtTokenInfoExtractFilter(), AbstractPreAuthenticatedProcessingFilter.class);

    }


    public static class JwtTokenInfoExtractFilter extends GenericFilter {

        private CustomJwtTokenExtract customJwtTokenExtract;

        {
            customJwtTokenExtract = new CustomJwtTokenExtract();
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                if (authentication instanceof OAuth2Authentication) {
                    OAuth2Authentication auth = (OAuth2Authentication) authentication;
                    SecurityContextHolder.getContext().setAuthentication(jwtTokenInfoAuthentication(auth));
                }
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }

        private Authentication jwtTokenInfoAuthentication(OAuth2Authentication auth) {
            OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) auth.getDetails();
            UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(
                    auth.getUserAuthentication().getPrincipal(),
                    auth.getUserAuthentication().getCredentials(),
                    auth.getAuthorities()
            );
            String tokenValue = oAuth2AuthenticationDetails.getTokenValue();
            userAuthentication.setDetails(extractJwtInfo(tokenValue));

            OAuth2Request oAuth2Request = auth.getOAuth2Request();

            OAuth2Authentication authentication = new OAuth2Authentication(
                    oAuth2Request,
                    userAuthentication
            );
            authentication.setDetails(auth.getDetails());
            return authentication;
        }

        private Map<String, Object> extractJwtInfo(String jwtToken) {
            return customJwtTokenExtract.decode(jwtToken);
        }


    }

    public static class CustomJwtTokenExtract {

        private JsonParser objectMapper = JsonParserFactory.create();

        /**
         * 参考下面两个方法()
         * OAuth2AuthenticationProcessingFilter JwtAccessTokenConverter decode已做验证 此处不在做验证
         *
         * @param token
         * @return
         * @see JwtTokenStore#readAuthentication(String)
         * @see JwtAccessTokenConverter#decode(String)
         */
        Map<String, Object> decode(String token) {
            try {
                Jwt jwt = JwtHelper.decode(token);
                String claimsStr = jwt.getClaims();
                Map<String, Object> claims = this.objectMapper.parseMap(claimsStr);
                if (claims.containsKey("exp") && claims.get("exp") instanceof Integer) {
                    Integer intValue = (Integer) claims.get("exp");
                    claims.put("exp", new Long((long) intValue));
                }
                return claims;
            } catch (Exception e) {
                throw new InvalidTokenException("Cannot convert access token to JSON", e);
            }
        }

    }
}
