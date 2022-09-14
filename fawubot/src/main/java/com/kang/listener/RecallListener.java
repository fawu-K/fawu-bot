package com.kang.listener;

import com.kang.config.BotConfig;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsgRecall;
import love.forte.simbot.api.message.events.PrivateMsgRecall;
import love.forte.simbot.api.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 消息撤回
 * @create 2022-09-13 14:52
 **/
@Beans
@Component
public class RecallListener {

    @Depend
    @Autowired
    private MessageContentBuilderFactory messageContentBuilderFactory;

    /**
     * 发现群里有撤回消息
     */
    @Listen(GroupMsgRecall.class)
    public void getRecall(GroupMsgRecall groupMsgRecall, Sender sender){
        MessageContent msgContent = groupMsgRecall.getMsgContent();
        System.out.println("这是撤回的消息： "+ msgContent.getMsg());

        //操作人的信息
        AccountInfo accountInfo = groupMsgRecall.getAccountInfo();
        GroupInfo groupInfo = groupMsgRecall.getGroupInfo();
        String accountNickname = accountInfo.getAccountNickname();
        String accountCode = accountInfo.getAccountCode();
        MessageContentBuilder msgBuilder = messageContentBuilderFactory.getMessageContentBuilder();
        String groupCode = groupInfo.getGroupCode();
        String groupName = groupInfo.getGroupName();
        MessageContent content = msgBuilder.text("群"+groupName+"("+ groupCode +")\n"+accountNickname + "(" + accountCode + ")撤回一条消息").build();

        sender.sendPrivateMsg(BotConfig.getRootCode(), content);
        sender.sendPrivateMsg(BotConfig.getRootCode(), msgContent);
    }

    /**
     * 发现私有有撤回消息
     */
    @Listen(PrivateMsgRecall.class)
    public void getPrivateRecall(PrivateMsgRecall groupMsgRecall, Sender sender){
        MessageContent msgContent = groupMsgRecall.getMsgContent();
        System.out.println("这是撤回的消息： "+ msgContent.getMsg());

        //操作人的信息
        AccountInfo accountInfo = groupMsgRecall.getAccountInfo();
        String accountNickname = accountInfo.getAccountNickname();
        String accountCode = accountInfo.getAccountCode();
        MessageContentBuilder msgBuilder = messageContentBuilderFactory.getMessageContentBuilder();
        MessageContent content = msgBuilder.text(accountNickname + "(" + accountCode + ")撤回一条消息").build();

        sender.sendPrivateMsg(BotConfig.getRootCode(), content);
        sender.sendPrivateMsg(BotConfig.getRootCode(), msgContent);
    }
}
