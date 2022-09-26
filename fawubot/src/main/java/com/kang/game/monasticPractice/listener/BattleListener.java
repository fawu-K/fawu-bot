package com.kang.game.monasticPractice.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.commons.util.BotUtil;
import com.kang.config.PlayConfig;
import com.kang.entity.monasticPractice.play2.Buff;
import com.kang.entity.monasticPractice.play2.Event;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Skill;
import com.kang.entity.monasticPractice.play2.vo.RoleVo;
import com.kang.game.monasticPractice.service.EventService;
import com.kang.game.monasticPractice.service.RoleService;
import com.kang.game.monasticPractice.service.SkillService;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 战斗系统
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-23 15:44
 **/
@Beans
@Component
public class BattleListener {

    @Autowired
    private RoleService roleService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private EventService eventService;

    public static final String BATTLE = "战斗";

    public static final String EXPERIENCE = "历练";
    /**
     * 历练，可以通过历练激活随机事件
     * @param groupMsg
     * @param sender
     */
    @OnGroup
    @Filter(value = "历练", matchType = MatchType.EQUALS)
     public void experience (GroupMsg groupMsg, ListenerContext context, Sender sender) {
        String code = BotUtil.getCode(groupMsg);
        Role role = PlayConfig.getRoleMap(code);

        // 获取随机事件，通过事件激活对话
        Event event = eventService.random(role.getLv());

        // 得到session上下文，并断言它的确不是null
        final ContinuousSessionScopeContext sessionContext = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert sessionContext != null;

        sender.sendGroupMsg(groupMsg, event.toString());
        final SessionCallback<String> callback = SessionCallback.<String>builder().onResume(choice -> {

            if ("战斗".equals(event.getType())) {
                if (choice.equals(event.getPlanA())) {
                    //进行战斗
                    this.battle(groupMsg, context, sender);
                } else {
                    // 放弃处理
                    // 获取对应的下一个选项
                    Event next = eventService.toNext(event.getId(), event.getPlanB());
                    sender.sendGroupMsg(groupMsg, next.getInfo());
                }
            }
        }).onError(e -> System.out.println("onError 出错啦: " + e)).onCancel(e -> {
            // 这里是第一个会话，此处通过 onCancel 来处理会话被手动关闭、超时关闭的情况的处理，有些时候会与 orError 同时被触发（例如超时的时候）
            System.out.println("onCancel 关闭啦: " + e);
        }).build(); // build 构建
        // 这里开始等待第一个会话。
        sessionContext.waiting(EXPERIENCE, code, callback);
    }

    @OnGroup
    @OnlySession(group = EXPERIENCE)
    public void isExperience(GroupMsg groupMsg, ListenerContext context, Sender sender) {
        // 得到session上下文。

        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        String code = BotUtil.getCode(groupMsg);
        String text = groupMsg.getText();
        // 尝试将这个选择推送给对应的会话。
        session.push(EXPERIENCE, code, text);
    }

    /**
     * 战斗操作
     * @param groupMsg
     * @param context
     * @param sender
     */
    public void battle (GroupMsg groupMsg, ListenerContext context, Sender sender) {
        //获取双方角色以及技能信息
        String code = BotUtil.getCode(groupMsg);
        Role role = PlayConfig.getRoleMap(code);
        List<Skill> skills = skillService.getSkill(role.getId());
        BattleRole battleRole = new BattleRole(role, skills);

        //对方信息
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("name", "流氓");
        Role one = roleService.getOne(roleQueryWrapper);
        BattleRole battleRole1 = new BattleRole(one, skillService.getSkill(one.getId()));

        // 得到session上下文，并断言它的确不是null
        final ContinuousSessionScopeContext sessionContext = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert sessionContext != null;
        sender.sendGroupMsg(groupMsg, "您的信息：\n"+ battleRole + "\n" +
                "======================\n" +
                "敌方信息：\n" + battleRole1 + "\n" +
                "战斗开始，当前为你的回合，请输入您要使用的技能:");

        // 这里开始等待第一个会话。
        sessionContext.waiting(BATTLE, code, battleKill(battleRole, battleRole1, sessionContext, groupMsg, sender));
    }

    /**
     * 进行战斗逻辑处理
     * @param battleRole 我方角色
     * @param battleRole1 对方角色
     * @param sessionContext 持续会话
     * @param groupMsg 信息体
     * @param sender 发送功能
     * @return 持续会话返回
     */
    private SessionCallback<?> battleKill(BattleRole battleRole, BattleRole battleRole1, ContinuousSessionScopeContext sessionContext, GroupMsg groupMsg, Sender sender){

        String code = BotUtil.getCode(groupMsg);

        return SessionCallback.<String>builder().onResume(str -> {
            //寻找对应技能
            Skill skill = getSkill(battleRole.getSkills(), str);
            //进行伤害
            BigDecimal kill = kill(battleRole, battleRole1, skill);

            if (battleRole1.getSurplusHp() > 0) {
                //对方并未被杀死，则对方进行攻击
                sender.sendGroupMsg(groupMsg, String.format("你使用[%s]技能对%s造成[%s]点伤害，并未打败对方。\n%s",
                        str, battleRole1.getName(), kill, battleRole1));

                Skill skill1 = battleRole1.getSkills().get(0);
                //进行伤害
                BigDecimal kill1 = kill(battleRole1, battleRole, skill1);

                if (battleRole.getSurplusHp() > 0) {
                    //己方未被杀死
                    sender.sendGroupMsg(groupMsg, String.format("%s使用[%s]技能对您造成[%s]点伤害，您抗了下来。\n%s\n请输入您要使用的技能：",
                            battleRole1.getName(), skill1.getName(), kill1, battleRole));

                    //重新回调这个方法，再次进行战斗
                    sessionContext.waiting(BATTLE, code, battleKill(battleRole, battleRole1, sessionContext, groupMsg, sender));
                } else {
                    //被反杀
                    sender.sendGroupMsg(groupMsg, String.format("%s使用[%s]技能对您进行了绝杀，您只能落荒而逃！",
                            battleRole1.getName(), skill1.getName()));
                }
            } else {
                sender.sendGroupMsg(groupMsg, String.format("您使用[%s]对%s进行了绝杀，他被打的满地找牙!",
                        skill.getName(), battleRole1.getName()));
            }
        }).build();
    }

    /**
     * 伤害计算，计算出伤害量，并对角色进行伤害
     *
     * @param battleRole 伤害者
     * @param battleRole1 被伤害者
     * @param skill 造成伤害的技能
     * @return 技能的系数 * 伤害者攻击 - 被伤害者防御
     */
    private BigDecimal kill(BattleRole battleRole, BattleRole battleRole1, Skill skill) {

        //技能造成的伤害计算
        BigDecimal kill = BigDecimal.valueOf(skill.getNum()).multiply(BigDecimal.valueOf(battleRole.getAttack())).subtract(BigDecimal.valueOf(battleRole1.getDefe()));
        //伤害最低造成1点
        if (kill.compareTo(BigDecimal.ZERO) <= 0) {
            kill = new BigDecimal("1");
        }
        //扣除被伤害者的血量
        BigDecimal surplusHp = BigDecimal.valueOf(battleRole1.getSurplusHp()).subtract(kill);
        battleRole1.setSurplusHp(surplusHp.doubleValue());
        return kill;
    }

    private Skill getSkill(List<Skill> skills, String name) {
        Skill result = null;
        for (Skill skill: skills) {
            if (name.equals(skill.getName())) {
                result = skill;
            }
        }
        return result;
    }

    @OnGroup
    @OnlySession(group = BATTLE)
    public void isBattle(GroupMsg groupMsg, ListenerContext context, Sender sender) {
        // 得到session上下文。
        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        String code = BotUtil.getCode(groupMsg);
        String text = groupMsg.getText();
        // 尝试将这个选择推送给对应的会话。
        boolean push = session.push(BATTLE, code, text);
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class BattleRole extends RoleVo {
        /**
         * 角色的技能组
         */
        private List<Skill> skills;

        /**
         * 战斗时的剩余血量
         */
        private double surplusHp;

        /**
         * 状态栏
         */
        private List<Buff> buffs;

        public BattleRole(){
        }

        public BattleRole(Role role) {
            this();
            BeanUtils.copyProperties(role, this);
            this.surplusHp = role.getHp();
            Skill skill = new Skill();
            skill.setType("伤害");
            skill.setName("挥拳");
            skill.setInfo("挥出一拳");
            skill.setNum(1);
            skill.setCount(0);
            this.skills = new ArrayList<>();
            skills.add(skill);
            this.buffs = new ArrayList<>();
        }

        public BattleRole(Role role, List<Skill> skills) {
            this(role);
            this.skills = skills;
        }

        @Override
        public String toString(){
            String result = "[%s]\thp:%s/%s\n" +
                    "攻击：%s;\t防御：%s\n" +
                  //  "状态：%s\n" +
                    "--------------------\n" +
                    "技能：%s";
            return String.format(result, this.getName(), surplusHp, this.getHp(), //昵称，剩余血量，总血量
                    this.getAttack(), this.getDefe(), //攻击力，防御力
                  //  this.getBuffs(),  //状态栏
                    this.skills);
        }
    }
}
