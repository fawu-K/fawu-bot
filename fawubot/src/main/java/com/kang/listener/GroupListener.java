package com.kang.listener;

import catcode.CatCodeUtil;
import com.kang.commons.util.BotUtil;
import com.kang.manager.BotAutoManager;
import com.kang.service.BotService;
import lombok.extern.slf4j.Slf4j;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.component.mirai.message.MiraiMessageContent;
import love.forte.simbot.component.mirai.message.MiraiMessageContentBuilder;
import love.forte.simbot.component.mirai.message.MiraiMessageContentBuilderFactory;
import love.forte.simbot.filter.MatchType;
import net.mamoe.mirai.message.data.ForwardMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @program: service
 * @description: 群聊监听器
 * @author: K.faWu
 * @create: 2022-04-28 16:34
 **/
@Slf4j
@Beans
@Component
public class GroupListener {
    /**
     * 通过依赖注入获取一个 "消息正文构建器工厂"。
     */
    @Depend
    @Autowired
    private BotService botService;
    @Autowired
    private BotAutoManager botAutoManager;
    @Depend
    @Autowired
    private MessageContentBuilderFactory factory;

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

        String code = BotUtil.getCode(groupMsg);

        MiraiMessageContentBuilder builder = ((MiraiMessageContentBuilderFactory) factory).getMessageContentBuilder();

        String image = CatCodeUtil.getInstance().getStringTemplate().image("D:\\photos\\Saved Pictures\\68ec83bb0a544.41488f57c136.1019206.jpg");
        // 假设这里构建了一个图片消息
        final MessageContent imgContent = builder.image("D:\\photos\\Saved Pictures\\68ec83bb0a544.41488f57c136.1019206.jpg").image("D:\\photos\\Saved Pictures\\68ec83bb0a544.41488f57c136.1019206.jpg").build();

        builder.clear();

        MiraiMessageContent messageContent = botAutoManager.saveForwardMessage(groupMsg, image, "[CAT:at,code=" + code + "] 你好", groupMsg, imgContent);
        // 发送消息
        sender.sendGroupMsg(groupMsg, messageContent);
    }

    /**
     * 被@的时候
     */
    @Filter(atBot = true)
    @OnGroup
    public void atBot(GroupMsg groupMsg, Sender sender) {
        System.out.println("我被@了" + groupMsg.getMsg());
        botService.roBot(groupMsg, sender);
    }

    /**
     * 可达鸭动图
     *
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
