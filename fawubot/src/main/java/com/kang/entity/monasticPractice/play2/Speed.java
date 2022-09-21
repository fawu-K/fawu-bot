package com.kang.entity.monasticPractice.play2;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author K.faWu
 * @program service
 * @description: 先天之气数量对应的修炼速度
 * @create 2022-08-19 16:46
 **/
@Data
public class Speed {
    private Long id;
    /**
     * 修炼速度
     */
    private BigDecimal speed;
    /**
     * 出现的概率
     */
    private Double probability;
    /**
     * 介绍
     */
    private String info;
}
