package com.kang.task.thread;

import com.kang.entity.monasticPractice.play2.Event;
import com.kang.service.BotService;
import com.kang.task.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 去创造boss的线程
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-10-18 10:26
 **/

@Slf4j
public class BossThread implements Runnable {
    private final BotService botService;
    private final Event event;

    public BossThread(Event event){
        this.botService = (BotService) SpringContextUtil.getBean(BotService.class);
        this.event = event;
    }

    @Override
    public void run() {
        log.info("生成世界BOSS");
        botService.toBoss(event);
    }
}
