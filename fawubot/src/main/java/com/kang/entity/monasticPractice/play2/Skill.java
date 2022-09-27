package com.kang.entity.monasticPractice.play2;

import lombok.Data;

/**
 * 人物的技能表
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 13:59
 **/
@Data
public class Skill {

    private Long id;

    /**
     * 技能类型。伤害、增益、控制
     */
    private String type;

    /**
     * 技能名称
     */
    private String name;

    /**
     * 技能简介
     */
    private String info;

    /**
     * 技能造成的数值
     * 如果是伤害则是减低的血量；如果是增益，则是恢复的血量或者技能增加的收益数字
     */
    private double num;

    /**
     * 持续的回合
     */
    private int count;

    /**
     * 稀有程度，数字越高越稀有
     */
    private int quality;

    /**
     * 获取的最低等级
     */
    private int lvMin;

    @Override
    public String toString() {
        return String.format("%s-%s-%s", name, num, count);
    }

}
