package com.kang.entity.monasticPractice.play2.vo;

import com.kang.entity.monasticPractice.play2.Buff;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Skill;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class BattleRole extends RoleVo {
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

    /**
     * 死亡世间
     */
    private Date outTime;

    /**
     * 复活时间
     */
    private Date restartTime;

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
                                            //昵称，剩余血量，总血量
        return String.format(result, this.getName(), surplusHp, this.getHp(),
                //攻击力，防御力
                this.getAttack(), this.getDefe(),
                //  this.getBuffs(),  //状态栏
                //技能组
                this.skills);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BattleRole) {
            BattleRole battleRole = (BattleRole) o;
            return this.getUserId().equals(battleRole.getUserId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getName());
    }
}
