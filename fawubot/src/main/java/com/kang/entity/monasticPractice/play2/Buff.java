package com.kang.entity.monasticPractice.play2;

import lombok.Data;

/**
 * 对角色附加的状态
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 14:10
 **/

@Data
public class Buff {

    /**
     * 状态类型
     */
    private String type;

    /**
     * 状态描述
     */
    private String info;

    /**
     * 状态数值
     */
    private double num;

    /**
     * 状态持续回合
     */
    private int count;

    @Override
    public String toString() {
        return String.format("%s-%s-%s回合", type, num, count);
    }
}
