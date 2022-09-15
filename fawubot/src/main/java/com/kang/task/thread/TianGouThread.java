package com.kang.task.thread;

import com.kang.service.BotService;
import com.kang.task.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description:
 * @create 2022-09-15 14:30
 **/
@Slf4j
public class TianGouThread  implements Runnable {
    private final BotService botService;

    public TianGouThread(){
        this.botService = (BotService) SpringContextUtil.getBean(BotService.class);
    }

    @Override
    public void run() {
        log.info("定时发送舔狗信息");
        botService.tianGou();
    }
}
