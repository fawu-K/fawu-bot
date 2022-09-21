package com.kang.entity.monasticPractice.play2;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author K.faWu
 * @program service
 * @description: 角色表
 * @create 2022-08-19 16:39
 **/
@Data
public class Role {
    private Long id;

    /**
     * 人物名称
     */
    private String name;
    /**
     * 所属用户
     */
    private String userId;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 等级
     */
    private Integer lv;
    /**
     * 当前所拥有的经验
     */
    private BigDecimal exp;
    /**
     * 血量
     */
    private Integer hp;
    /**
     * 攻击力
     */
    private Integer attack;
    /**
     * 防御
     */
    private Integer defe;
    /**
     * 类别：玩家角色；npc角色
     */
    private Integer type;
    /**
     * 所属门派
     */
    private Long sectId;
    /**
     * 先天之气数量
     */
    private Integer gasNum;

    /**
     * 修炼类型
     */
    private String lvType;

    public Role(){
    }

    public Role(String accountCode, String name, String sex) {
        this.userId = accountCode;
        this.name = name;
        this.sex = "男".equals(sex)? 1: 0;
        this.lv = 0;
        this.exp = new BigDecimal(0);
        this.hp = 10;
        this.attack = 5;
        this.defe = 2;
        this.type = 1;
        this.lvType = "无";
    }

    @Override
    public String toString() {
        return "【" + name + "】" +
                "\n所属人：" + userId +
                "\n性别：" + (sex==1 ? "男" : "女") +
                "\nLV：" + lv +
                "\nexp：" + exp +
                "\nhp：" + hp +
                "\n攻击：" + attack +
                "\n防御：" + defe +
                (type == 1? "": "\n类型：NPC角色") +
                "\n先天之气：" + gasNum + "条" +
                "\n修炼类型：" + lvType;
    }
}
