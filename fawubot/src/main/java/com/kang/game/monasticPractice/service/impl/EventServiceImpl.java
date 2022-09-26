package com.kang.game.monasticPractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.entity.monasticPractice.play2.Event;
import com.kang.game.monasticPractice.mapper.EventMapper;
import com.kang.game.monasticPractice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 17:21
 **/
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {
    @Autowired
    private EventMapper eventMapper;

    @Override
    public Event random(Integer lv) {
        QueryWrapper<Event> eventQueryWrapper = new QueryWrapper<>();
        //小于等于
        eventQueryWrapper.le("lv_min", lv);
        //大于等于
        eventQueryWrapper.ge("lv_max", lv);
        List<Event> events = eventMapper.selectList(eventQueryWrapper);
        int index = (int) (Math.random() * events.size());
        return events.get(index);
    }

    @Override
    public Event toNext(Long pid, String plan) {
        QueryWrapper<Event> eventQueryWrapper = new QueryWrapper<>();
        eventQueryWrapper.eq("pid", pid);
        eventQueryWrapper.eq("trigger_plan", plan);

        return eventMapper.selectOne(eventQueryWrapper);
    }
}
