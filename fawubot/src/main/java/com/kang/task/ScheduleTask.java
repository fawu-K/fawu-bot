package com.kang.task;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 定时任务启动与关闭的基础类，减少后续新增定时任务的时候对反射功能的破坏性可能
 * @create 2022-09-15 15:37
 **/

public interface ScheduleTask {
    /**
     * 开启或重启定时任务
     * @return 是否启动成功
     */
    int onOrReleaseTask();

    /**
     * 关闭定时任务
     * @return 是否关闭成功
     */
    String stopTask();
}
