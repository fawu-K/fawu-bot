package com.kang.game.monasticPractice.listener;

import catcode.CatCodeUtil;
import com.kang.commons.util.BotUtil;
import com.kang.commons.util.CommonsUtils;
import com.kang.config.PlayConfig;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Skill;
import com.kang.entity.monasticPractice.play2.vo.BattleRole;
import com.kang.entity.monasticPractice.play2.vo.BossBattleRole;
import com.kang.entity.monasticPractice.play2.vo.EventVo;
import com.kang.game.monasticPractice.service.EventService;
import com.kang.manager.BotAutoManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.filter.MatchType;
import love.forte.simbot.listener.ContinuousSessionScopeContext;
import love.forte.simbot.listener.ListenerContext;
import love.forte.simbot.listener.SessionCallback;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 世界boss战斗逻辑
 * 构建世界boss战斗系统，在群聊中进行世界boss战斗操作
 * boss激活后，所有人可以发送攻击指令，通过[攻击+技能名称]对boss进行攻击
 * 对boss造成伤害后boss进行反击，若血量降为0则角色死亡，复活需要15秒，即在复活期间无法进行攻击
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-10-14 10:48
 **/
@Slf4j
@Beans
@Component
public class BossListener {

    public static BattleRole killBossRole = null;
    public static BattleRole boss = null;


    public static final Map<BattleRole, BigDecimal> KILL_MAP = new HashMap<>();

    public static final String BATTLE = "战斗";

    @Autowired
    private BattleListener battleListener;
    @Autowired
    private BotAutoManager botAutoManager;
    @Autowired
    private EventService eventService;

    public BossListener(){
        Role role = new Role();
        role.setName("boss");
        role.setHp(120);
        role.setAttack(100);
        role.setDefe(50);
        boss = new BattleRole(role);
    }

    @OnGroup
    @Filter(value = "#", matchType = MatchType.STARTS_WITH)
    public void battle(MsgGet msgGet, ListenerContext context) {
        if (boss == null) {
            botAutoManager.sendMsg(msgGet, "Boss已消失，请下次再进行挑战~");
            return;
        }

        String code = BotUtil.getCode(msgGet);
        BattleRole battleRole = (BattleRole) PlayConfig.getRoleMap(code);
        String at = CatCodeUtil.getInstance().getStringTemplate().at(code);

        //判断角色阵亡状态
        if (battleRole.getSurplusHp() <= 0) {
            if (battleRole.getRestartTime().getTime() > System.currentTimeMillis()) {
                long time = battleRole.getRestartTime().getTime() - System.currentTimeMillis();
                time = time/1000;
                botAutoManager.sendMsg(msgGet, at + "您已阵亡，" + time + "秒后复活，请耐心等待。");
                return;
            } else {
                battleRole.setSurplusHp(battleRole.getHp());
            }
        }

        //选择释放的技能名称
        String skillName = msgGet.getText().replace("#", "");
        //寻找对应技能
        Skill skill = BattleListener.getSkill(battleRole.getSkills(), skillName);
        if (CommonsUtils.isEmpty(skill)) {
            botAutoManager.sendMsg(msgGet, at + "[" + skillName + "]技能不存在或您未携带。");
        }
        //进行技能伤害伤害
        BattleListener.Kill kill = BattleListener.kill(battleRole, boss, skill);
        //计算总伤害，保存伤害排行
        BigDecimal killNum = KILL_MAP.get(battleRole);
        if (killNum == null) {
            killNum = kill.getKillNum();
        }else {
            killNum = killNum.add(kill.getKillNum());
        }
        KILL_MAP.put(battleRole, killNum);

        String killMsg = kill.getMsg();
        String killMsg1 = "";
        if (boss.getSurplusHp() > 0) {
            //对方并未被杀死，则对方进行攻击 随机抽一个技能使用
            List<Skill> skills = boss.getSkills();
            int index = (int) (Math.random() * skills.size());
            Skill skill1 = skills.get(index);
            //进行伤害
            BattleListener.Kill kill1 = BattleListener.kill(boss, battleRole, skill1);
            killMsg1 = kill1.getMsg();

            //表示角色被击败
             if (battleRole.getSurplusHp() <= 0) {
                 battleRole.setOutTime(new Date());
                 long time = System.currentTimeMillis() + 1000 * 30;
                 battleRole.setRestartTime(new Date(time));
                 killMsg1 += "\n您已被Boss击败，需要30s的复活时间";
             }
        } else {
            //boss被击败
            killMsg1 = String.format("%s被击败了！\n[%s]对boss造成的最后一击！", boss.getName(), battleRole.getName());
            boss = null;
            killBossRole = battleRole;
        }
        //播放双方伤害信息
        botAutoManager.sendMsg(msgGet, at + "\n" +
                killMsg + "\n" +
                killMsg1);
    }

    @OnGroup
    @Filter(value = ".排行", matchType = MatchType.STARTS_WITH)
    public void killNumRanking(MsgGet msgGet){
        List<BossBattleRole> list = new ArrayList<>();
        Set<BattleRole> battleRoles = KILL_MAP.keySet();
        battleRoles.forEach(battleRole -> list.add(new BossBattleRole(battleRole, KILL_MAP.get(battleRole))));

        list.sort((o1, o2) -> o2.getKillNum().compareTo(o1.getKillNum()));
        StringBuilder msg = new StringBuilder("[伤害排行]\n");
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            BossBattleRole bossBattleRole = list.get(i);
            msg.append(i + 1).append(bossBattleRole.getName()).append(" -------- ").append(bossBattleRole.getKillNum()).append("\n");
            if (i>=9) {
                break;
            }
        }
        botAutoManager.sendMsg(msgGet, msg.toString());
    }
}
