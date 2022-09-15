package com.kang.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.entity.ScheduleTime;

import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description:
 * @create 2022-09-15 11:52
 **/

public interface ScheduleTimeService extends IService<ScheduleTime> {
    List<ScheduleTime> getByIsActive(boolean flag);
}
