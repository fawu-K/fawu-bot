package com.kang.entity.monasticPractice.play2.vo;

import com.kang.entity.monasticPractice.play2.Event;
import com.kang.entity.monasticPractice.play2.EventPlan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 携带选项的事件类
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-27 11:16
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class EventVo extends Event {

    private List<EventPlan> plans;

    public EventVo(){
    }

    public EventVo(Event event) {
        this();
        BeanUtils.copyProperties(event, this);
    }


    @Override
    public String toString() {
        return this.getInfo() + plans;
    }
}
