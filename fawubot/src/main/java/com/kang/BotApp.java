package com.kang;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @program: service
 * @description: QQ机器人管理
 * @author: K.faWu
 * @create: 2022-04-29 13:48
 **/
@SpringBootApplication
@EnableSimbot //qq机器人注解
public class BotApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BotApp.class, args);
    }
}
