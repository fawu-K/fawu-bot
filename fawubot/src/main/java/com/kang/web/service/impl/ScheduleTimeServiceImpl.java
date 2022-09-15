package com.kang.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.entity.ScheduleTime;
import com.kang.web.mapper.ScheduleTimeMapper;
import com.kang.web.service.ScheduleTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description:
 * @create 2022-09-15 11:53
 **/
@Service
public class ScheduleTimeServiceImpl extends ServiceImpl<ScheduleTimeMapper, ScheduleTime> implements ScheduleTimeService {
    @Autowired
    private ScheduleTimeMapper scheduleTimeMapper;

    @Override
    public List<ScheduleTime> getByIsActive(boolean flag) {
        int isActive;
        if (flag) {
            isActive = 1;
        } else {
            isActive  = 0;
        }

        QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", isActive);
        return scheduleTimeMapper.selectList(queryWrapper);
    }
}
