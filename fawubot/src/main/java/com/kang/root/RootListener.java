package com.kang.root;

import com.kang.config.BotConfig;
import com.kang.web.service.RootService;
import lombok.Data;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author K.faWu
 * @program service
 * @description: 系统设置类
 * @create 2022-09-09 15:51
 **/
@Beans
@Component
public class RootListener {
    public static final String ON = "启动";
    public static final String OFF = "关闭";

    @Autowired
    private RootService rootService;

    public final static Map<String, List<Blacklist>> BLACKLISTS = new HashMap<>();

    /**
     * 开机，开启bot在该群的操作
     */
    @OnGroup
    @Filter(value = ON, matchType = MatchType.EQUALS)
    public void open(GroupMsg groupMsg, Sender sender) {
        rootService.openOrOff(groupMsg, true);
    }

    /**
     * 关机，关闭bot在该群的操作
     */
    @OnGroup
    @Filter(value = OFF, matchType = MatchType.EQUALS)
    public void off(GroupMsg groupMsg, Sender sender) {
        rootService.openOrOff(groupMsg, false);
    }

    /**
     * 越权行为统计名单
     */
    @Data
    public static class Blacklist {
        String groupCode; //对应群
        String accountCode; //账号
        String instruct; //越权指令
        Date date; // 越权行为发生的时间

        public Blacklist(){}

        public Blacklist(String groupCode, String accountCode, String instruct) {
            this.groupCode = groupCode;
            this.accountCode = accountCode;
            this.instruct = instruct;
            this.date = new Date();
        }
    }
}
