package com.kang.manager;

import com.alibaba.fastjson.JSONObject;
import com.kang.commons.util.BotUtil;
import com.kang.entity.Msg;
import com.kang.entity.ResultByQq;
import com.kang.web.service.ResultByQqService;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.containers.BotInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.message.results.GroupList;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.message.results.GroupMemberList;
import love.forte.simbot.api.sender.*;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.component.mirai.message.*;
import love.forte.simbot.component.mirai.message.event.MiraiTempMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    @Depend
    @Autowired
    private MessageContentBuilderFactory factory;


    public Setter getSetter() {
        return botManager.getDefaultBot().getSender().SETTER;
    }

    /**
     * 组装合并消息
     * @param msgGet 消息发送方
     * @param builder 组装的消息体
     * @param msgs 要组装的消息们
     * @return 组装的消息体
     */
    private MiraiMessageContentBuilder saveForwardMessage(MsgGet msgGet, MiraiMessageContentBuilder builder, Object... msgs) {
        String code = msgGet.getAccountInfo().getAccountCode();
        long codeNumber = msgGet.getAccountInfo().getAccountCodeNumber();
        // 通过 MiraiMessageContentBuilder.forwardMessage 构建一个合并消息。
        // 一般来讲，合并消息不支持与其他类型消息同时存在，因此不应再继续拼接其他消息。
        builder.forwardMessage(forwardBuilder -> {
            // 常见的前三个参数：发送者账号、发送者名称，发送时间（秒时间戳）
            // 最后一个（第四个）参数则为发送的消息
            for (Object msg : msgs) {
                saveForwardBuilder(msgGet, forwardBuilder, msg);
            }
            // 注意：ALPHA版本中，下述内容要在ALPHA.6之后的版本（不包含.6）才会有
            forwardBuilder.add(codeNumber, code, 3000, "");
        });
        return builder;
    }

    private void saveForwardBuilder(MsgGet msgGet, MiraiForwardMessageBuilder forwardBuilder, Object msg) {
       if (msg instanceof MessageContent) {
            forwardBuilder.add(msgGet, 3000, (MessageContent) msg);
        } else if (msg instanceof MessageGet) {
           forwardBuilder.add(msgGet, 3000, (MessageGet) msg);
        } else {
           forwardBuilder.add(msgGet, 3000, msg.toString());
       }
    }

    /**
     * 组装合并消息
     * @return 组装的消息体
     */
    public MiraiMessageContent saveForwardMessage(MsgGet msgGet, Object... msgs) {
        if (!(factory instanceof MiraiMessageContentBuilderFactory)) {
            throw new RuntimeException("不支持mirai组件");
        }

        MiraiMessageContentBuilder builder = ((MiraiMessageContentBuilderFactory) factory).getMessageContentBuilder();
        return saveForwardMessage(msgGet, builder, msgs).build();
    }

    /**
     * 发送合并消息
     */
    public void sendForwardMessage(MsgGet msgGet, Object... msgs) {
        MiraiMessageContent messageContent = this.saveForwardMessage(msgGet, msgs);
        sendMsg(msgGet, messageContent);
    }

    /**
     * 根据接受信息的类型来判断是群消息还是私聊消息
     */
    public void sendMsg(MsgGet msgGet, String msg) {
        String botCode = msgGet.getBotInfo().getBotCode();
        Sender sender = this.getSender(botCode);

        if (msgGet instanceof GroupMsg) {
            sender.sendGroupMsg((GroupMsg) msgGet, msg);
        } else {
            String groupCode = null;
            String accountCode;
            //判断是否为非好友，若是非好友则需要通过群发送消息
            if (msgGet instanceof MiraiTempMsg) {
                MiraiTempMsg miraiTempMsg = (MiraiTempMsg) msgGet;
                MiraiMemberAccountInfo miraiMemberAccountInfo = (MiraiMemberAccountInfo) miraiTempMsg.getAccountInfo();
                groupCode = miraiMemberAccountInfo.getGroupCode();
            }
            accountCode = BotUtil.getCode(msgGet);
            sender.sendPrivateMsg(accountCode, groupCode, msg);
        }
    }

    /**
     * 根据接受信息的类型来判断是群消息还是私聊消息
     */
    public void sendMsg(MsgGet msgGet, MessageContent msg) {
        this.sendMsg(msgGet, msg.getMsg());
    }

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


}
