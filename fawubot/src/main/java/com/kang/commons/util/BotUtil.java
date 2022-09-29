package com.kang.commons.util;

import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;

/**
 * @author K.faWu
 * @program service
 * @description: 机器人工具类
 * @create 2022-09-09 16:53
 **/

public class BotUtil {

    /**
     * 获取发送消息群聊的群号
     * @param groupMag 群消息体
     * @return 群号
     */
    public static String getGroupCode(GroupMsg groupMag){
        return groupMag.getGroupInfo().getGroupCode();
    }

    /**
     * 获取发送消息群聊的群名称
     * @param groupMsg 群消息体
     * @return 群名称
     */
    public static String getGroupName(GroupMsg groupMsg) {
        return groupMsg.getGroupInfo().getGroupName();
    }

    /**
     * 获取消息体中的发送消息人账号
     * @param messageGet 消息体
     * @return 账号
     */
    public static String getCode(MessageGet messageGet) {
        return messageGet.getAccountInfo().getAccountCode();
    }

    /**
     * 获取消息体中的发送消息人账号
     * @param msgGet 消息体
     * @return 账号
     */
    public static String getCode(MsgGet msgGet) {
        return msgGet.getAccountInfo().getAccountCode();
    }

    /**
     * 获取消息体中的发送消息人昵称
     * @param messageGet 消息体
     * @return 昵称
     */
    public static String getNickname(MessageGet messageGet) {
        return messageGet.getAccountInfo().getAccountNickname();
    }

}
