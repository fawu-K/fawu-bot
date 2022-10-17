package com.kang.task;

import com.kang.entity.ScheduleTime;
import com.kang.web.service.ScheduleTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description:
 * @create 2022-09-15 14:48
 **/
@Slf4j
@Component
public class RunTask implements ApplicationRunner {

    private final ApplicationContext applicationContext;

    private final ScheduleTimeService scheduleTimeService;

    public RunTask(ApplicationContext applicationContext, ScheduleTimeService scheduleTimeService){
        this.applicationContext = applicationContext;
        this.scheduleTimeService = scheduleTimeService;
    }

    /**
     * 项目启动后，将状态为启动的定时任务启动
     * 注意：定时任务的类名称一定要与数据库中的scheduleName相同(除首字母小写)
     * 该地方使用的是反射的方式，
     * 通过数据库中存储的scheduleName字段和isActive字段对需要启动的定时任务进行启动
     */
    @Override
    public void run(ApplicationArguments args) {
        List<ScheduleTime> list = scheduleTimeService.getByIsActive(true);
        for (ScheduleTime scheduleTime : list) {
            //获取spring bean中存储的定时任务实例
            try {
                ScheduleTask scheduleTask = (ScheduleTask) applicationContext.getBean(scheduleTime.getScheduleName());
                scheduleTask.onOrReleaseTask();
            }catch (Exception e) {
                e.printStackTrace();
                log.warn("定时任务java文件不存在：{} - {}", scheduleTime.getScheduleName(), scheduleTime.getScheduleDes());
            }
        }
    }
}
