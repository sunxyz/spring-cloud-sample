package com.yangrd.springcloud.example.rabbit;

import lombok.Value;

import java.io.Serializable;

/**
 * RabbitMsg
 *
 * @author yangrd
 * @date 2019/11/09
 */
@Value
public class RabbitMsg implements Serializable {

    private String name;

    private String data;
}
