package com.kang.task.scheduleTask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.entity.ScheduleTime;
import com.kang.task.ScheduleTask;
import com.kang.task.thread.SeTuR18Thread;
import com.kang.task.thread.TianGouThread;
import com.kang.web.service.ScheduleTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务类
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-29 16:37
 **/
@Slf4j
@RestController
@SpringBootApplication
@ComponentScan("com.kang.*")
public class SeTuScheduleTask implements ScheduleTask {


    private final ThreadPoolTaskScheduler botThreadPoolTaskScheduler;

    @Autowired
    private ScheduleTimeService scheduleTimeService;

    public SeTuScheduleTask(@Qualifier("seTuThreadPoolTaskScheduler") @Lazy ThreadPoolTaskScheduler botThreadPoolTaskScheduler) {
        this.botThreadPoolTaskScheduler = botThreadPoolTaskScheduler;
    }

    @Bean
    public ThreadPoolTaskScheduler seTuThreadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    private ScheduledFuture<?> future;

    @Override
    public int onOrReleaseTask() {
        log.info("\n开始或重启定时任务：下载图片");
        //从数据库查询对应的定时任务
        QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schedule_name", "seTuScheduleTask");
        ScheduleTime scheduleTime = scheduleTimeService.getOne(queryWrapper);

        if (future != null) {
            future.cancel(true);
        }

        String time = scheduleTime.getScheduleValue();
        String min, hour, day = "*";

        if ("month".equals(scheduleTime.getScheduleType())) {
            min = time.substring(4, 6);
            hour = time.substring(2, 4);
            day = time.substring(0, 2);
        } else {
            min = time.substring(2, 4);
            hour = time.substring(0, 2);
        }

        String trig = "0 %s %s %s * ?";
        String format = String.format(trig, min, hour, day);
        future = botThreadPoolTaskScheduler.schedule(new SeTuR18Thread(), new CronTrigger(format));
        scheduleTime.setIsActive(1);
        scheduleTimeService.updateById(scheduleTime);
        return 1;
    }

    @Override
    public String stopTask() {
        log.info("\n停止定时任务：下载图片");
        if (future != null) {
            future.cancel(true);
            QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("schedule_name", "seTuScheduleTask");
            ScheduleTime scheduleTime = scheduleTimeService.getOne(queryWrapper);
            scheduleTime.setIsActive(0);
            scheduleTimeService.updateById(scheduleTime);
        }

        return "stop cron";
    }
}
