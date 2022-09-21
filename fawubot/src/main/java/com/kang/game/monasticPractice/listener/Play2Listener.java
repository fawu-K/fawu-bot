package com.kang.game.monasticPractice.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.commons.util.BotUtil;
import com.kang.commons.util.CommonsUtils;
import com.kang.config.PlayConfig;
import com.kang.entity.monasticPractice.play2.Lv;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Speed;
import com.kang.entity.monasticPractice.play2.vo.RoleVo;
import com.kang.game.monasticPractice.service.LvService;
import com.kang.game.monasticPractice.service.RoleService;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author K.faWu
 * @program service
 * @description: 问大天荒
 * @create 2022-08-19 15:07
 **/
@Slf4j
@Beans
@Component
public class Play2Listener {

    @Autowired
    private RoleService roleService;
    @Autowired
    private LvService lvService;

    public static final String CHOICE = "choice";

    @OnGroup
    @Filter(value = "大荒指令")
    public void help(GroupMsg groupMsg, Sender sender) {
        String result = "【大荒指令】\n" +
                "[修炼]、[突破]、[内视]、[查阅修炼等级#武道/练气]";
        sender.sendGroupMsg(groupMsg, result);
    }


    @OnGroup
    @Filter(value = "修炼", matchType = MatchType.EQUALS)
    public void cultivation(GroupMsg groupMsg, ListenerContext context, Sender sender) {

        sender.sendGroupMsg(groupMsg, "开始修炼...");
        String code = BotUtil.getCode(groupMsg);
        Role role = PlayConfig.getRoleMap(code);

        //增加修为
        RoleVo roleVo = roleService.cultivation(role);

        String result = "修炼完成\n";
        if (roleVo.getExpMax().compareTo(roleVo.getExp()) == 0) {
            result += "当前经验值已满，请[突破]后再进行修炼。";
        } else {
            result += "当前的经验：" + roleVo.getExp() + "/" + roleVo.getExpMax();
        }
        sender.sendGroupMsg(groupMsg, result);
    }

    /**
     * 进行突破操作，若是从0级突破到1级需要选择修炼类型
     * 经验需要达到当前等级的最高才能进行突破
     */
    @OnGroup
    @Filter(value = "突破", matchType = MatchType.EQUALS)
    public void breach(GroupMsg groupMsg, ListenerContext context, Sender sender) {
        String code = BotUtil.getCode(groupMsg);
        Role role = PlayConfig.getRoleMap(code);

        Lv lv = PlayConfig.getLvMap(role.getLvType(), role.getLv());

        if (role.getExp().compareTo(lv.getExpMax()) >= 0) {
            //经验已满，进入升级状态
            // 得到session上下文，并断言它的确不是null
            final ContinuousSessionScopeContext sessionContext = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
            assert sessionContext != null;
            final String accountCode = groupMsg.getAccountInfo().getAccountCode();

            //当从0级提升到1级时需要选择修炼类型
            if (role.getLv() == 0) {
                //0级升到1级需要选择修炼类型
                sender.sendGroupMsg(groupMsg, "天荒大陆自古以来便是天下修士的得道飞升之地，世人为变强开辟出两条道路：武道一途以先天之气锤炼肉身，从而达到肉身成圣，举手投足间便可抗衡天地，山河破碎；练气一途则是化先天之气为体内丹田，容纳天地本源，借大天地之力化自身小天地之威能，自成一片小天地，颠倒日月，翻手覆山河。\n" +
                        "但这两条道路天生相斥，若非为天地宠儿成为那万中无一的双修奇才，否则只能择一而行，还望三思而后行！\n" +
                        "请选择您的修炼方式[武道]/[练气]：");
                // 创建回调函数 SessionCallback 实例。
                // 通过 SessionCallback.builder 进行创建
                final SessionCallback<String> callback = SessionCallback.<String>builder().onResume(choice -> {
                    sender.sendGroupMsg(groupMsg, "您选择的修炼途径为：" + choice);
                    //是否为万里挑一的双修奇才
//                    double v = Math.random() * 10000;
//                    if (v<=1){
//                        role.setLvType("双修");
//                        sender.sendGroupMsg(groupMsg, "叮！您乃万里挑一的武道练气双修的绝世天才！(然而并不会有什么鸟用-暂时)");
//                    } else {
//                        role.setLvType(choice);
//                    }

                    role.setLvType(choice);
                    //突破到新等级，并发送消息
                    toBreach(groupMsg, sender, role);

                }).onError(e -> System.out.println("onError 出错啦: " + e)).onCancel(e -> {
                    // 这里是第一个会话，此处通过 onCancel 来处理会话被手动关闭、超时关闭的情况的处理，有些时候会与 orError 同时被触发（例如超时的时候）
                    System.out.println("onCancel 关闭啦: " + e);
                }).build(); // build 构建

                // 这里开始等待第一个会话。
                sessionContext.waiting(CHOICE, accountCode, callback);
            } else {
                //突破到新等级，并发送消息
                toBreach(groupMsg, sender, role);
            }
        } else {
            sender.sendGroupMsg(groupMsg, "变强需脚踏实地，一步一个脚印，请您将经验值修满后再进行突破，祝君武道昌隆！");
        }
    }

    /**
     * 突破新的等级
     */
    private void toBreach(GroupMsg groupMsg, Sender sender, Role role) {
        //将角色更新到新的等级
        roleService.breach(role);

        Lv lv = PlayConfig.getLvMap(role.getLvType(), role.getLv());
        Speed speed = PlayConfig.getSpeedMap(role.getGasNum());
        //等级没有更高的等级
        if (CommonsUtils.isEmpty(lv)) {
            sender.sendGroupMsg(groupMsg, "您已经达到当前修炼等级巅峰，请等待世界开放后继续修炼。");
            return;
        }

        //生成展示信息
        RoleVo roleVo = new RoleVo(role, lv);
        sender.sendGroupMsg(groupMsg, "天道酬勤，恭喜您突破成功！\n" +
                "当前等级每修炼一次获取的经验为：\n" +
                lv.getExp() + "*" + speed.getSpeed() + "=" + (lv.getExp().multiply(speed.getSpeed())) + "\n" +
                roleVo);
    }

    /**
     * 选择修炼的方式
     */
    @OnGroup
    @OnlySession(group = CHOICE)
    public void choice(GroupMsg m, ListenerContext context, Sender sender) {
        // 得到session上下文。

        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        final String code = m.getAccountInfo().getAccountCode();
        String text = m.getText().trim();
        if ("武道".equals(text) || "练气".equals(text)) {
            // 尝试将这个选择推送给对应的会话。
            session.push(CHOICE, code, text);
        } else {
            sender.sendGroupMsg(m, "请选择正确的修炼途径");
        }
    }

    @OnGroup
    @Filter(value = "内视", matchType = MatchType.EQUALS)
    public void look(GroupMsg groupMsg, Sender sender){
        String code = BotUtil.getCode(groupMsg);
        Role role = PlayConfig.getRoleMap(code);

        Lv lv = PlayConfig.getLvMap(role.getLvType(), role.getLv());

        sender.sendGroupMsg(groupMsg, new RoleVo(role, lv).toString());
    }

    @OnGroup
    @Filter(value = "查阅修炼等级#", matchType = MatchType.STARTS_WITH)
    public void lvList(GroupMsg groupMsg, Sender sender){
        String text = groupMsg.getText().replace("查阅修炼等级#", "");

        List<Lv> lvList = lvService.getLvList(text);
        StringBuilder s = new StringBuilder(text + "修炼等级\n");
        for (Lv lv: lvList) {
            s.append(lv.getLv()).append(lv.getName()).append("\n");
        }
        sender.sendGroupMsg(groupMsg, s.toString());
    }
}
