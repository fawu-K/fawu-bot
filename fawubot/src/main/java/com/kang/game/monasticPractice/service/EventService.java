package com.kang.game.monasticPractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.entity.monasticPractice.play2.Event;
import com.kang.entity.monasticPractice.play2.vo.EventVo;

import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 17:20
 **/


public interface EventService extends IService<Event> {
    /**
     * 根据角色的等级随机获取事件
     * @param lv 等级
     * @return 事件
     */
    EventVo random(Integer lv);

    /**
     * 获取事件进行选择后的下一个事件
     * @param eventId 上个事件id
     * @param plan 选项
     * @return 下个事件
     */
    EventVo toNext(Long eventId, String plan);

    /**
     * 获取全部的BOSS列表
     * @return BOSS列表
     */
    List<Event> getBossList();
}
