package com.kang.entity.monasticPractice.play2;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 装备类
 * equipment
 * @author
 */
@Data
public class Equipment implements Serializable {
    /**
     * 装备
     */
    private Long id;

    /**
     * 类型
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 品质
     */
    private String quality;

    /**
     * 装备增加的攻击力
     */
    private BigDecimal attack;

    /**
     * 装备增加的防御力
     */
    private BigDecimal defe;

    /**
     * 装备增加的血量
     */
    private BigDecimal hp;

    /**
     * 装备描述
     */
    private String info;

    /**
     * 最低装备等级
     */
    private Integer lvMin;

    /**
     * 该装备适用的修炼方式
     */
    private String lvType;

    private static final long serialVersionUID = 1L;
}
