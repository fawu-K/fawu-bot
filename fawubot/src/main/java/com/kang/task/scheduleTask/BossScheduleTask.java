package com.kang.task.scheduleTask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.commons.util.CommonsUtils;
import com.kang.entity.ScheduleTime;
import com.kang.entity.monasticPractice.play2.Event;
import com.kang.game.monasticPractice.service.EventService;
import com.kang.task.ScheduleTask;
import com.kang.task.thread.BossThread;
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
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * 世界boss1
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-10-17 17:49
 **/
@Slf4j
@RestController
@SpringBootApplication
@ComponentScan("com.kang.*")
public class BossScheduleTask implements ScheduleTask {

    private final ThreadPoolTaskScheduler botThreadPoolTaskScheduler;

    @Autowired
    private ScheduleTimeService scheduleTimeService;
    @Autowired
    private EventService eventService;

    public BossScheduleTask(@Qualifier("bossThreadPoolTaskScheduler") @Lazy ThreadPoolTaskScheduler botThreadPoolTaskScheduler) {
        this.botThreadPoolTaskScheduler = botThreadPoolTaskScheduler;
    }

    @Bean
    public ThreadPoolTaskScheduler bossThreadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    private final List<ScheduledFuture<?>> futures = new ArrayList<>();

    @Override
    public int onOrReleaseTask() {
        log.info("\n开始或重启定时任务：每日BOSS");
        //从数据库查询对应的定时任务
        QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schedule_name", "bossScheduleTask");
        ScheduleTime scheduleTime = scheduleTimeService.getOne(queryWrapper);

        if (CommonsUtils.isNotEmpty(futures)) {
            futures.forEach(future -> future.cancel(true));
        }

        String time = scheduleTime.getScheduleValue();
        String min, hour, day = "*";

        if ("month".equals(scheduleTime.getScheduleType())) {
            min = time.substring(4, 6);
            hour = time.substring(2, 4);
            day = time.substring(0, 2);
        } else if ("day".equals(scheduleTime.getScheduleType())){
            min = time.substring(2, 4);
            hour = time.substring(0, 2);
        } else {
            min = time.substring(2, 4);
            hour = time.substring(0, 2);
        }

        int minNum = Integer.parseInt(min);
        int hourNum = Integer.parseInt(hour);
        List<Event> events = eventService.getBossList();
        for (Event event : events) {
            String trig = "0 %s %s %s * ?";
            String format = String.format(trig, minNum, hourNum, day);
            ScheduledFuture<?> future = botThreadPoolTaskScheduler.schedule(new BossThread(event), new CronTrigger(format));
            futures.add(future);
            //每30分钟一个Boss
            minNum += 1;
            if (minNum >= 60) {
                minNum -= 60;
                hourNum += 1;
            }
            if (hourNum >= 24) {
                hourNum -= 24;
            }
        }

        scheduleTime.setIsActive(1);
        scheduleTimeService.updateById(scheduleTime);
        return 1;
    }

    @Override
    public String stopTask() {
        log.info("\n停止定时任务：停止所有世界Boss的刷新");
        if (CommonsUtils.isNotEmpty(futures)) {
            futures.forEach(future -> future.cancel(true));

            QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("schedule_name", "bossScheduleTask");
            ScheduleTime scheduleTime = scheduleTimeService.getOne(queryWrapper);
            scheduleTime.setIsActive(0);
            scheduleTimeService.updateById(scheduleTime);
        }

        return "stop cron";
    }
}
