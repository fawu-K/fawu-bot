package com.kang.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kang.Constants;
import com.kang.commons.util.HttpClientUtil;
import com.kang.config.BotConfig;
import com.kang.config.TianApiConflg;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.containers.GroupInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 天行数据服务类
 * @create 2022-09-19 14:11
 **/

@Slf4j
@Component
public class TianApiTool {

    /**
     * 调用天行数据智障机器人接口
     *
     * @return 回复的消息
     */
    public String getTianRobot(String msg, String uin) {
        //调用api
        log.info("{}-聊天-{}", uin, msg);
        String url = String.format(TianApiConflg.TIAN_ROBOT, TianApiConflg.getTianKey(), uin, msg);
        String result = HttpClientUtil.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String str = jsonObject.getJSONArray("newslist").getJSONObject(0).getString("reply");
        log.info("聊天-{}；返回-{}", msg, str);
        return str;
    }

    public String tianGou() {
        log.info("舔狗日记-获取");
        String url = String.format(TianApiConflg.TIAN_DOG, TianApiConflg.getTianKey());
        String s = HttpClientUtil.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(s);

        String msg = jsonObject.getJSONArray("newslist").getJSONObject(0).getString("content");
        String result = "【舔狗日记】\n" + msg;
        log.info(result);
        return result;
    }

    public String topnews() {
        String url = String.format(TianApiConflg.TIAN_TOPNEWS, TianApiConflg.getTianKey());

        String s = HttpClientUtil.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray jsonArray = jsonObject.getJSONArray("newslist");

        int count = 1;
        StringBuilder text = new StringBuilder("【每日新闻】\n");
        for (Object j : jsonArray) {
            JSONObject jsonObject1 = (JSONObject) j;
            String title = jsonObject1.getString("title");
            text.append(count++).append(".").append(title).append("\n");
        }

        return text.toString();
    }
}
