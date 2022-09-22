package com.kang.root;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.entity.ScheduleTime;
import com.kang.task.ScheduleTask;
import com.kang.task.SpringContextUtil;
import com.kang.web.service.ScheduleTimeService;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 定时任务监听类
 * @create 2022-09-21 14:48
 **/
@Beans
@Component
public class TaskListener {

    @Autowired
    private ScheduleTimeService scheduleTimeService;

    /**
     * 监听定时任务的开启，使用规则，开启+定时任务名称
     */
    @OnPrivate
    @Filter(value = RootListener.ON, matchType = MatchType.STARTS_WITH)
    public void onTask(PrivateMsg privateMsg, Sender sender) {
        ScheduleTask scheduleTask = getScheduleTask(privateMsg, RootListener.ON);
        scheduleTask.onOrReleaseTask();

        String result = "定时任务启动成功~";
        sender.sendPrivateMsg(privateMsg, result);
    }

    /**
     * 监听定时任务的关闭， 使用规则：关闭+定时任务名称
     */
    @OnPrivate
    @Filter(value = RootListener.OFF, matchType = MatchType.STARTS_WITH)
    public void offTask(PrivateMsg privateMsg, Sender sender) {
        ScheduleTask scheduleTask = getScheduleTask(privateMsg, RootListener.OFF);
        scheduleTask.stopTask();

        sender.sendPrivateMsg(privateMsg, "已关闭");
    }

    /**
     * 获取定时任务bean
     */
    private ScheduleTask getScheduleTask(MsgGet msgGet, String repStr){
        String text = msgGet.getText();
        text = text.replace(repStr, "");
        //获取定时任务
        QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schedule_des", text);
        ScheduleTime scheduleTime = scheduleTimeService.getOne(queryWrapper);

        //获取定时任务的bean
        return (ScheduleTask) SpringContextUtil.getBean(scheduleTime.getScheduleName());
    }
}
