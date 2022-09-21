package com.kang.listener;

import com.kang.config.PlayConfig;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Speed;
import com.kang.game.monasticPractice.service.RoleService;
import com.kang.game.monasticPractice.service.impl.RoleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnlySession;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;
import love.forte.simbot.listener.ContinuousSessionScopeContext;
import love.forte.simbot.listener.ListenerContext;
import love.forte.simbot.listener.SessionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 开启修炼功能
 * @create 2022-09-20 13:59
 **/

@Slf4j
@Beans
@Component
public class MonasticPracticeListener {

    @Autowired
    private RoleService roleService;

    /**
     * 选择性别
     */
    private static final String SEX = "sex";
    /**
     * 名字
     */
    private static final String NAME = "name";

    /**
     * 开始游戏，抽取角色属性
     */
    @Filter(value = "加入大陆", matchType = MatchType.EQUALS)
    @OnGroup
    public void init(GroupMsg groupMsg, ListenerContext context, Sender sender) {

        // 得到session上下文，并断言它的确不是null
        final ContinuousSessionScopeContext sessionContext = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert sessionContext != null;

        final String accountCode = groupMsg.getAccountInfo().getAccountCode();

        sender.sendGroupMsg(groupMsg, "请输入昵称(不超过6个字符)：");

        // 创建回调函数 SessionCallback 实例。
        // 通过 SessionCallback.builder 进行创建
        final SessionCallback<String> callback = SessionCallback.<String>builder().onResume(name -> {
            sender.sendGroupMsg(groupMsg, "角色：" + name + "，请选择性别(男/女)：");
            sessionContext.waiting(SEX, accountCode, sex -> {
                sender.sendGroupMsg(groupMsg, "角色生成中...");
                //创建角色
                Role role = roleService.init(accountCode, name, sex.toString());
                Speed speed = PlayConfig.getSpeedMap(role.getGasNum());
                //展示角色信息
                sender.sendGroupMsg(groupMsg, "角色创建成功：\n" +
                        role +
                        name + "拥有" + role.getGasNum() + "条先天之气，" +
                        "修炼速度为每次修炼经验基础的" + speed.getSpeed() + "倍。\n" +
                        speed.getInfo() +
                        "\n祝君武道昌隆！");
            });
        }).onError(e -> {
            System.out.println("onError 出错啦: " + e);
        }).onCancel(e -> {
            // 这里是第一个会话，此处通过 onCancel 来处理会话被手动关闭、超时关闭的情况的处理，有些时候会与 orError 同时被触发（例如超时的时候）
            System.out.println("onCancel 关闭啦: " + e);
        }).build(); // build 构建

        // 这里开始等待第一个会话。
        sessionContext.waiting(NAME, accountCode, callback);
    }

    @OnGroup
    @OnlySession(group = NAME)
    public void name(GroupMsg m, ListenerContext context, Sender sender) {
        // 得到session上下文。

        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        final String code = m.getAccountInfo().getAccountCode();
        String text = m.getText().trim();
        if (text.length() <= 6) {
            // 尝试将这个phone推送给对应的会话。
            session.push(NAME, code, text);
        } else {
            sender.sendGroupMsg(m, "昵称请勿超过6个字符");
        }
    }

    @OnGroup
    @OnlySession(group = SEX)
    public void onName(GroupMsg m, ListenerContext context, Sender sender) {
        // 得到session上下文。
        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        final String code = m.getAccountInfo().getAccountCode();
        String text = m.getText().trim();
        if ("男".equals(text) || "女".equals(text)) {
            // 尝试推送结果
            session.push(SEX, code, text);
        } else {
            sender.sendGroupMsg(m, "性别选择错误请重新选择(男/女)：");
            session.push(NAME, code, text);
        }
    }
}
