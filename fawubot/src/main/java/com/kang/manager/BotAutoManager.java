package com.kang.manager;

import catcode.CatCodeUtil;
import com.alibaba.fastjson.JSONObject;
import com.kang.Constants;
import com.kang.commons.util.HttpClientUtil;
import com.kang.config.BotConfig;
import com.kang.entity.Msg;
import com.kang.entity.ResultByQq;
import com.kang.web.service.ResultByQqService;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.containers.BotInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.results.GroupList;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.message.results.GroupMemberList;
import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: service
 * @description: 主动发送消息类
 * @author: K.faWu
 * @create: 2022-06-13 14:10
 **/
@Beans
@Service
public class BotAutoManager {
    @Depend
    @Autowired
    private BotManager botManager;
    @Autowired
    private ResultByQqService resultByQqService;

    /**
     * 获取默认登录bot的所有群信息
     * @return
     */
    public List<GroupInfo> getDefaultBotGroups(){
        GroupList groupList = botManager.getDefaultBot().getSender().GETTER.getGroupList();
        List<GroupInfo> groupInfos = new ArrayList<>();
        groupList.forEach(groupInfos::add);
        return groupInfos;
    }

    /**
     * 获取默认登录bot的sender，即信息发送类
     * @return
     */
    public Sender getSender(){
        return botManager.getDefaultBot().getSender().SENDER;
    }

    /**
     * 获取指定登录bot的sender
     * @param code
     * @return
     */
    public Sender getSender(String code){
        return botManager.getBot(code).getSender().SENDER;
    }

    public List<BotInfo> getBotInfo(){
        List<Bot> bots = botManager.getBots();
        List<BotInfo> result = new ArrayList<>();
        for (Bot bot: bots){
            BotInfo botInfo = bot.getBotInfo();
            result.add(botInfo);
        }
        return result;
    }

    public List<GroupInfo> getGroup(String code) {
        Bot bot = botManager.getBot(code);
        BotSender botSender = bot.getSender();
        GroupList groupList = botSender.GETTER.getGroupList();

        List<GroupInfo> groupInfos = new ArrayList<>();
        groupList.forEach(groupInfos::add);
        return groupInfos;
    }

    public ResultByQq<Msg> sendMsg(String message) {
        ResultByQq<Msg> resultByQq = JSONObject.parseObject(message, ResultByQq.class);
        //将消息处理成可发送的消息体
        MessageContent messageContent =resultByQqService.resultByQqToMsg(resultByQq);
        String accountCode = resultByQq.getAccountCode();
        Bot bot = botManager.getBot(accountCode);
        Sender sender = bot.getSender().SENDER;
        sender.sendGroupMsg(resultByQq.getGroupCode(), messageContent);
        resultByQqService.msgToResultByQq(resultByQq, messageContent);
        return resultByQq;
    }

    public List<GroupMemberInfo> getGroupMemberList(String code, String groupCode) {
        Bot bot = botManager.getBot(code);
        Getter getter = bot.getSender().GETTER;
        GroupMemberList groupMemberList = getter.getGroupMemberList(groupCode);
        return groupMemberList.getResults();
    }

    /**
     * 发送新闻
     */
    public void topnews(){
        String url = Constants.TIAN_TOPNEWS;
        url += "?key=" + BotConfig.getTianKey();
        String s = HttpClientUtil.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(s);
        List<JSONObject> newslist = (List<JSONObject>) jsonObject.get("newslist");

        int count = 1;
        StringBuilder text = new StringBuilder();
        for (JSONObject jsonObject1 : newslist) {
            text.append(count++).append(jsonObject1.get("title")).append("\n");
        }

        List<GroupInfo> defaultBotGroups = getDefaultBotGroups();
        defaultBotGroups.forEach(groupInfo -> getSender().sendGroupMsg(groupInfo, text.toString()));

    }
}
