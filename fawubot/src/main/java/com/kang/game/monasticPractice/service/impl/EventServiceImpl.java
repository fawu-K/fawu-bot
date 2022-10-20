package com.kang.game.monasticPractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.entity.monasticPractice.play2.Event;
import com.kang.entity.monasticPractice.play2.EventPlan;
import com.kang.entity.monasticPractice.play2.vo.EventVo;
import com.kang.game.monasticPractice.mapper.EventMapper;
import com.kang.game.monasticPractice.mapper.EventPlanMapper;
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
    @Autowired
    private EventPlanMapper planMapper;

    @Override
    public EventVo random(Integer lv) {
        QueryWrapper<Event> eventQueryWrapper = new QueryWrapper<>();
        //小于等于
        eventQueryWrapper.le("lv_min", lv);
        //大于等于
        eventQueryWrapper.ge("lv_max", lv);
        eventQueryWrapper.eq("type", "事件");
        List<Event> events = eventMapper.selectList(eventQueryWrapper);
        int index = (int) (Math.random() * events.size());
        EventVo eventVo = new EventVo(events.get(index));
        //获取选项
        setPlans(eventVo);
        return eventVo;
    }

    @Override
    public EventVo toNext(Long eventId, String plan) {

        //通过事件id和选项获取下一个事件的id
        QueryWrapper<EventPlan> planQueryWrapper = new QueryWrapper<>();
        planQueryWrapper.eq("event_id", eventId);
        planQueryWrapper.eq("plan", plan);
        EventPlan eventPlan = planMapper.selectOne(planQueryWrapper);

        //获取事件
        Long triggerEventId = eventPlan.getTriggerEventId();
        EventVo eventVo = new EventVo(eventMapper.selectById(triggerEventId));

        setPlans(eventVo);
        return eventVo;
    }

    @Override
    public List<Event> getBossList() {
        QueryWrapper<Event> eventQueryWrapper = new QueryWrapper<>();
        eventQueryWrapper.eq("type", "BOSS");
        return eventMapper.selectList(eventQueryWrapper);
    }

    /**
     * 获取事件选项
     * @param eventVo 事件
     */
    private void setPlans(EventVo eventVo) {
        QueryWrapper<EventPlan> planQueryWrapper1 = new QueryWrapper<>();
        planQueryWrapper1.eq("event_id", eventVo.getId());
        List<EventPlan> plans = planMapper.selectList(planQueryWrapper1);
        eventVo.setPlans(plans);
    }
}
