package com.yangrd.springcloud.example;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Spencer Gibb
 */
@Configuration
@ConfigurationProperties("sample")
@Data
public class SampleProperties {

    private String prop = "default value";

}
