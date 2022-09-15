package com.kang.entity;

import lombok.Data;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description:
 * @create 2022-09-15 11:21
 **/
@Data
public class ScheduleTime {

    private Long id;
    private String scheduleName;
    private String scheduleValue;
    private String scheduleType;
    private String scheduleDes;
    private String remark;
    private int isActive;
}
