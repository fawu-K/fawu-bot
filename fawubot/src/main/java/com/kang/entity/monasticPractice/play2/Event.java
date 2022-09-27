package com.kang.entity.monasticPractice.play2;

import lombok.Data;

/**
 * 触发事件类
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 10:24
 **/
@Data
public class Event {

    private Long id;
    /**
     * 触发地点
     */
    private String place;
    /**
     * 事件
     */
    private String info;
    /**
     * 类型
     */
    private String type;
    /**
     * 事件发生的最小等级
     */
    private Integer lvMin;
    /**
     * 事件发生的最大等级
     */
    private Integer lvMax;

}
