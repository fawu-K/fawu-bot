package com.kang.entity.monasticPractice.play2;

import java.io.Serializable;
import lombok.Data;

/**
 * event_plan
 * @author fawu
 */
@Data
public class EventPlan implements Serializable {
    /**
     * 事件的选项以及选项所触发的事件
     */
    private Long id;

    /**
     * 该选项对应的事件id
     */
    private Long eventId;

    /**
     * 选项
     */
    private String plan;

    /**
     * 该选项触发的事件id
     */
    private Long triggerEventId;

    /**
     * 角色触发事件后能选择该选项的最小等级
     */
    private Integer lvMin;

    /**
     * 该选项可以触发的前提条件，现无
     */
    private String conditionInfo;

    /**
     * 经验系数
     */
    private double expRatio;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return plan;
    }
}
