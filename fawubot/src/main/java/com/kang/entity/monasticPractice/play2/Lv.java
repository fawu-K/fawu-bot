package com.kang.entity.monasticPractice.play2;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author K.faWu
 * @program service
 * @description: 等级
 * @create 2022-08-19 16:34
 **/
@Data
public class Lv {
    private Long id;
    /**
     *修炼类型
     */
    private String type;
    /**
     *等级名称
     */
    private String name;
    /**等级大小编号
     *
     */
    private Integer lv;
    /**
     *升到下一级所需要的经验
     */
    private BigDecimal expMax;
    /**
     * 当升到下一级时增加的血量
     */
    private Integer hp;
    /**
     * 当升到下一级时增加的攻击力
     */
    private Integer attack;
    /**
     * 防御
     */
    private Integer defe;
    /**
     * 每次增加的基础经验
     */
    private BigDecimal exp;
}
