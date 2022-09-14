package com.kang.listener;

import com.kang.manager.BotAutoManager;
import com.kang.service.BotService;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @program: service
 * @description: 群聊监听器
 * @author: K.faWu
 * @create: 2022-04-28 16:34
 **/
@Beans
@Component
public class GroupListener {
    /**
     * 通过依赖注入获取一个 "消息正文构建器工厂"。
     *
     */
    @Depend
    @Autowired
    private MessageContentBuilderFactory messageContentBuilderFactory;
    @Autowired
    private BotAutoManager botAutoManager;
    @Autowired
    private BotService botService;

    @OnGroup
    public void onGroupMsg(GroupMsg groupMsg, Sender sender) {
        // 获取消息构建器
        GroupInfo groupInfo = groupMsg.getGroupInfo();
        // 打印群号与名称
        System.out.println(groupInfo.getGroupCode());
        System.out.println(groupInfo.getGroupName());

        String msg = groupMsg.getMsg();
        System.out.println(msg);
        String text = groupMsg.getText();
        System.out.println(text);
    }

    /**
     * 被@的时候
     */
    @Filter(atBot = true)
    @OnGroup
    public void atBot(GroupMsg groupMsg, Sender sender){
        System.out.println("我被@了"+groupMsg.getMsg());
        botService.atBot(groupMsg, sender);
    }

    /**
     * 可达鸭动图
     * @param groupMsg
     * @param sender
     * @throws IOException
     */
    @Filter(value = "可达鸭", matchType = MatchType.STARTS_WITH)
    @OnGroup
    public void duck(GroupMsg groupMsg, Sender sender) throws IOException {
        botService.duck(groupMsg, sender);
    }
}
