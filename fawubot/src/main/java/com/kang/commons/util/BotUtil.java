package com.kang.commons.util;

import love.forte.simbot.api.message.events.GroupMsg;

/**
 * @author K.faWu
 * @program service
 * @description: 机器人工具类
 * @create 2022-09-09 16:53
 **/

public class BotUtil {

    public static String getGroupCode(GroupMsg groupMag){
        return groupMag.getGroupInfo().getGroupCode();
    }

    public static String getGroupName(GroupMsg groupMsg) {
        return groupMsg.getGroupInfo().getGroupName();
    }

    public static String getCode(GroupMsg groupMsg) {
        return groupMsg.getAccountInfo().getAccountCode();
    }

    public static String getNickname(GroupMsg groupMsg) {
        return groupMsg.getAccountInfo().getAccountNickname();
    }
}
