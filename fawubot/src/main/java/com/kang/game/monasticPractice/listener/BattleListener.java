package com.kang.game.monasticPractice.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.commons.util.BotUtil;
import com.kang.commons.util.CommonsUtils;
import com.kang.config.PlayConfig;
import com.kang.entity.monasticPractice.play2.*;
import com.kang.entity.monasticPractice.play2.vo.EventVo;
import com.kang.entity.monasticPractice.play2.vo.RoleVo;
import com.kang.game.monasticPractice.service.EventService;
import com.kang.game.monasticPractice.service.RoleService;
import com.kang.game.monasticPractice.service.SkillService;
import com.kang.manager.BotAutoManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.annotation.OnlySession;
import love.forte.simbot.api.message.events.MsgGet;
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
    @Autowired
    private BotAutoManager botAutoManager;

    public static final String BATTLE = "战斗";

    public static final String EXPERIENCE = "历练";

    public static final String EVENT = "事件";

    public static final String END = "结束";

    /**
     * 历练，可以通过历练激活随机事件
     *
     * @param msgGet 消息体
     */
    @OnGroup
    @OnPrivate
    @Filter(value = "历练", matchType = MatchType.EQUALS)
    public void experience(MsgGet msgGet, ListenerContext context) {
        String code = BotUtil.getCode(msgGet);
        Role role = PlayConfig.getRoleMap(code);

        // 获取随机事件，通过事件激活对话
        EventVo eventVo = eventService.random(role.getLv());

        // 得到session上下文，并断言它的确不是null
        final ContinuousSessionScopeContext sessionContext = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert sessionContext != null;

        // 这里开始等待第一个会话。
        sessionContext.waiting(EXPERIENCE, code, event(msgGet, context, sessionContext, eventVo));
    }

    /**
     * 事件会话
     *
     * @param sessionContext 持续会话
     * @param msgGet       信息体
     * @param context        会话体
     * @param eventVo        触发的事件
     * @return 持续会话返回
     */
    private SessionCallback<?> event(MsgGet msgGet, ListenerContext context,  ContinuousSessionScopeContext sessionContext, EventVo eventVo) {
        //播放事件信息
        botAutoManager.sendMsg(msgGet, eventVo.toString());

        return SessionCallback.<String>builder().onResume(choice -> {
            //获取选项的下一个事件
            EventVo next = eventService.toNext(eventVo.getId(), choice);
            // 进行事件走向判断
            String msg = this.toNext(msgGet, context, sessionContext, next);
            if (CommonsUtils.isNotEmpty(msg)) {
                botAutoManager.sendMsg(msgGet, msg);
            }
        }).onError(e -> System.out.println("onError 出错啦: " + e)).onCancel(e -> {
            // 这里是第一个会话，此处通过 onCancel 来处理会话被手动关闭、超时关闭的情况的处理，有些时候会与 orError 同时被触发（例如超时的时候）
            System.out.println("onCancel 关闭啦: " + e);
        }).build(); // build 构建
    }

    /**
     * 事件走向，根据选项带来的事件的类型将事件的走向推向不同的方向
     * 战斗、对话、新的事件、结束等等操作
     *
     * @param sessionContext 持续会话
     * @param msgGet       信息体
     * @param context        会话体
     * @param next           触发的事件
     * @return 是否有回复语
     */
    private String toNext(MsgGet msgGet, ListenerContext context, ContinuousSessionScopeContext sessionContext, EventVo next) {

        String result = null;
        String code = BotUtil.getCode(msgGet);

        if (BATTLE.equals(next.getType())) {
            //进行战斗
            this.battle(msgGet, context, next);
        } else if (EVENT.equals(next.getType())) {
            //重新创建事件会话
            sessionContext.waiting(EXPERIENCE, code, event(msgGet, context, sessionContext, next));
        } else if (END.equals(next.getType())) {
            result = end(code, next);
        }
        return result;
    }

    @OnGroup
    @OnPrivate
    @OnlySession(group = EXPERIENCE)
    public void isExperience(MsgGet msgGet, ListenerContext context, Sender sender) {
        // 得到session上下文。
        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        String code = BotUtil.getCode(msgGet);
        String text = msgGet.getText();
        // 尝试将这个选择推送给对应的会话。
        session.push(EXPERIENCE, code, text);
    }

    /**
     * 战斗操作
     * 获取双方的角色信息，并进行战斗操作
     *
     * @param msgGet 消息体
     * @param context 会话
     */
    public void battle(MsgGet msgGet, ListenerContext context, EventVo eventVo) {
        //获取双方角色以及技能信息
        String code = BotUtil.getCode(msgGet);
        Role role = PlayConfig.getRoleMap(code);
        List<Skill> skills = skillService.getSkill(role.getId());
        BattleRole battleRole = new BattleRole(role, skills);

        //对方信息
        String info = eventVo.getInfo();
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("name", info);
        roleQueryWrapper.eq("id", eventVo.getPlace());
        Role one = roleService.getOne(roleQueryWrapper);
        BattleRole battleRole1 = new BattleRole(one, skillService.getSkill(one.getId()));

        // 得到session上下文，并断言它的确不是null
        final ContinuousSessionScopeContext sessionContext = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert sessionContext != null;
        botAutoManager.sendMsg(msgGet, "您的信息：\n" + battleRole + "\n" +
                "======================\n" +
                "敌方信息：\n" + battleRole1 + "\n" +
                "战斗开始，当前为你的回合，请输入您要使用的技能:");

        // 这里开始等待第一个会话。
        sessionContext.waiting(BATTLE, code, battleKill(battleRole, battleRole1, sessionContext, msgGet, context, eventVo));
    }

    /**
     * 进行战斗逻辑处理
     *
     * @param battleRole     我方角色
     * @param battleRole1    对方角色
     * @param sessionContext 持续会话
     * @param msgGet       信息体
     * @param context        会话体
     * @return 持续会话返回
     */
    private SessionCallback<?> battleKill(BattleRole battleRole, BattleRole battleRole1, ContinuousSessionScopeContext sessionContext, MsgGet msgGet, ListenerContext context, EventVo eventVo) {
        return SessionCallback.<String>builder().onResume(str -> {
            String code = BotUtil.getCode(msgGet);
            //寻找对应技能
            Skill skill = getSkill(battleRole.getSkills(), str);
            //进行技能伤害伤害
            String killMsg = kill(battleRole, battleRole1, skill);
            if ("逃跑".equals(skill.getType())) {
                botAutoManager.sendMsg(msgGet, killMsg);
                return;
            }

            if (battleRole1.getSurplusHp() > 0) {
                //对方并未被杀死，则对方进行攻击 随机抽一个技能使用
                List<Skill> skills = battleRole1.getSkills();
                int index = (int) (Math.random() * skills.size());
                Skill skill1 = skills.get(index);
                //进行伤害
                String killMsg2 = kill(battleRole1, battleRole, skill1);
                if ("逃跑".equals(skill1.getType())) {
                    botAutoManager.sendMsg(msgGet, killMsg + "\n====================\n" + killMsg2);
                    return;
                }

                if (battleRole.getSurplusHp() > 0) {
                    //己方未被杀死
                    //播放双方伤害信息
                    botAutoManager.sendMsg(msgGet, killMsg + "\n====================\n" + killMsg2 + "\n请选择你要使用的技能:");

                    //重新回调这个方法，再次进行战斗
                    sessionContext.waiting(BATTLE, code, battleKill(battleRole, battleRole1, sessionContext, msgGet, context, eventVo));
                } else {
                    //失败结束事件
                    EventVo next = eventService.toNext(eventVo.getId(), "失败");
                    //进行事件走向判断
                    String result = this.toNext(msgGet, context, sessionContext, next);
                    // 战斗结束语
                    String msg = String.format("%s使用[%s]技能打败了你！", battleRole1.getName(), skill1.getName());
                    if (CommonsUtils.isNotEmpty(result)) {
                        msg += "\n" + result;
                    }
                    botAutoManager.sendMsg(msgGet, msg);
                }
            } else {
                //胜利结束事件
                EventVo next = eventService.toNext(eventVo.getId(), "胜利");
                //进行事件走向判断
                String result = this.toNext(msgGet, context, sessionContext, next);
                // 战斗结束语
                String msg = String.format("您使用[%s]对%s进行了绝杀！", skill.getName(), battleRole1.getName());
                if (CommonsUtils.isNotEmpty(result)) {
                    msg += "\n" + result;
                }
                botAutoManager.sendMsg(msgGet, msg);
            }
        }).build();
    }

    /**
     * 结束处理，返回一个结束的话语和对经验进行增加处理
     *
     * @param code    触发事件的角色账号
     * @param eventVo 结束事件
     * @return 结束语
     */
    private String end(String code, EventVo eventVo) {
        //进行增加经验处理
        Role role = PlayConfig.getRoleMap(code);
        String result = addExp(role, eventVo);
        return eventVo.getInfo() + "\n" + result;
    }

    /**
     * 增加经验，事件结束需要增加经验，根据最后结果进行经验增加
     * 最后结果增加的经验值系数为{@link Event#getPlace()}
     *
     * @param role    增肌经验的角色
     * @param eventVo 事件的结果
     * @return 最后判断经验是否已满
     */
    private String addExp(Role role, EventVo eventVo) {

        //不增加经验的情况
        if ("0".equals(eventVo.getPlace())) {
            return "";
        }

        //增加经验的逻辑
        Lv lv = PlayConfig.getLvMap(role.getLvType(), role.getLv());
        BigDecimal addExp = lv.getExp().multiply(new BigDecimal(eventVo.getPlace()));
        RoleVo roleVo = roleService.addExp(role, addExp);

        String result = "增加" + addExp + "点经验，";
        result += "当前经验值：" + roleVo.getExp() + "/" + roleVo.getExpMax();
        return result;
    }

    /**
     * 伤害计算，计算出伤害量，并对角色进行伤害
     * 技能的系数 * 伤害者攻击 - 被伤害者防御
     * @param battleRole  伤害者
     * @param battleRole1 被伤害者
     * @param skill       造成伤害的技能
     * @return 技能伤害信息
     */
    private String kill(BattleRole battleRole, BattleRole battleRole1, Skill skill) {
        String killMsg = "";
        if ("伤害".equals(skill.getType())) {
            //技能造成的伤害计算
            BigDecimal kill = BigDecimal.valueOf(skill.getNum()).multiply(BigDecimal.valueOf(battleRole.getAttack())).subtract(BigDecimal.valueOf(battleRole1.getDefe()));
            //伤害最低造成1点
            if (kill.compareTo(BigDecimal.ZERO) <= 0) {
                kill = new BigDecimal("1");
            }
            //扣除后剩余的血量
            double surplusHp = BigDecimal.valueOf(battleRole1.getSurplusHp()).subtract(kill).doubleValue();
            if (surplusHp <= 0) {
                surplusHp = 0;
            }
            battleRole1.setSurplusHp(surplusHp);

            //造成的伤害信息
            killMsg = String.format("%s使用[%s]技能对%s造成[%s]点伤害。\n%s", battleRole.getName(), skill.getName(), battleRole1.getName(), kill, battleRole1);
        } else if ("治疗".equals(skill.getType())) {
            //获取治疗后的hp
            double surplusHp = battleRole.getSurplusHp();
            BigDecimal hp = BigDecimal.valueOf(surplusHp).add(BigDecimal.valueOf(skill.getNum()));

            //判断治疗后是否超出上线
            if (battleRole.getHp() <= hp.doubleValue()) {
                battleRole.setSurplusHp(battleRole.getHp());
            } else {
                battleRole.setSurplusHp(hp.doubleValue());
            }
            //治疗信息
            killMsg = String.format("%s使用[%s]技能恢复[%s]点血量。\n%s",  battleRole.getName(), skill.getName(), skill.getNum(), battleRole);
        } else if ("逃跑".equals(skill.getType())) {
            killMsg = String.format("%s使用[%s]技能，撤出了战斗", battleRole.getName(), skill.getName());
        }

        return killMsg;
    }

    private Skill getSkill(List<Skill> skills, String name) {
        Skill result = null;
        for (Skill skill : skills) {
            if (name.equals(skill.getName())) {
                result = skill;
            }
        }
        return result;
    }

    @OnGroup
    @OnPrivate
    @OnlySession(group = BATTLE)
    public void isBattle(MsgGet msgGet, ListenerContext context, Sender sender) {
        // 得到session上下文。
        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        String code = BotUtil.getCode(msgGet);
        String text = msgGet.getText();
        // 尝试将这个选择推送给对应的会话。
        session.push(BATTLE, code, text);
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

        public BattleRole() {
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
        public String toString() {
            String result = "_________________\n" +
                            "| [%s]\thp:%s/%s\n" +
                            "| 攻击：%s;\t防御：%s\n" +
                            //  "状态：%s\n" +
                            "| 技能：%s\n" +
                            "---------------------";
            return String.format(result, this.getName(), surplusHp, this.getHp(), //昵称，剩余血量，总血量
                    this.getAttack(), this.getDefe(), //攻击力，防御力
                    //  this.getBuffs(),  //状态栏
                    this.skills);
        }
    }
}
