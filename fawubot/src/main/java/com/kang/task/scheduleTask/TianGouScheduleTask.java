package com.kang.task.scheduleTask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.entity.ScheduleTime;
import com.kang.task.ScheduleTask;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ScheduledFuture;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 新闻定时任务类
 * @create 2022-09-15 10:16
 **/
@Slf4j
@RestController
@SpringBootApplication
@ComponentScan("com.kang.*")
public class TianGouScheduleTask implements ScheduleTask {

    private final ThreadPoolTaskScheduler botThreadPoolTaskScheduler;

    @Autowired
    private ScheduleTimeService scheduleTimeService;

    public TianGouScheduleTask(@Qualifier("tianGouThreadPoolTaskScheduler") @Lazy ThreadPoolTaskScheduler botThreadPoolTaskScheduler) {
        this.botThreadPoolTaskScheduler = botThreadPoolTaskScheduler;
    }

    @Bean
    public ThreadPoolTaskScheduler tianGouThreadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    private ScheduledFuture<?> future;

    /**
     * 开始或重启定时任务
     *
     * @return
     */
    @Override
    @GetMapping("/tianGouScheduleTask/restartCron")
    public int onOrReleaseTask() {
        log.info("\n开始或重启定时任务：舔狗日记");
        //从数据库查询对应的定时任务
        QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schedule_name", "tianGouScheduleTask");
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
        future = botThreadPoolTaskScheduler.schedule(new TianGouThread(), new CronTrigger(format));
        scheduleTime.setIsActive(1);
        scheduleTimeService.updateById(scheduleTime);
        return 1;
    }

    /**
     * 停止定时任务
     *
     * @return
     */
    @Override
    @GetMapping("/tianGouScheduleTask/stopCron")
    public String stopTask() {
        log.info("\n停止定时任务：舔狗日记");
        if (future != null) {
            future.cancel(true);
            QueryWrapper<ScheduleTime> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("schedule_name", "tianGouScheduleTask");
            ScheduleTime scheduleTime = scheduleTimeService.getOne(queryWrapper);
            scheduleTime.setIsActive(0);
            scheduleTimeService.updateById(scheduleTime);
        }

        return "stop cron";
    }
}
