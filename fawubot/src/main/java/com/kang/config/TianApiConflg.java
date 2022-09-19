package com.kang.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 天行数据配置类
 * @create 2022-09-19 14:08
 **/
@Component
public class TianApiConflg {

    /**
     * 天行数据登录key
     */
    private static String TIAN_KEY;

    /**
     * 配置文件引入天行数据key
     * @param tianKey key
     */
    @Value("${bot.tianapi-key}")
    private void setTianKey(String tianKey) {
        TIAN_KEY = tianKey;
    }

    /**
     * 返回天行数据的key
     * @return TIAN_KEY
     */
    public static String getTianKey(){
        return TIAN_KEY;
    }

    /**
     * 天行数据网址
     */
    public final static String TIAN_URL = "http://api.tianapi.com/";

    /**
     * 对应新闻接口
     */
    public final static String TIAN_TOPNEWS = TIAN_URL + "topnews/index?key=%s";

    /**
     * 智能机器人对话
     */
    public final static String TIAN_ROBOT = TIAN_URL + "robot/index?key=%s&uniqueid=%s&question=%s";
    /**
     * 舔狗日记
     */
    public final static String TIAN_DOG = TIAN_URL + "tiangou/index?key=%s";
}
