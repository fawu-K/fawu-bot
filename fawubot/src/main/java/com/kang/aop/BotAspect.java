package com.kang.aop;

import com.kang.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.containers.GroupContainer;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.GroupMsgRecall;
import love.forte.simbot.api.message.events.MessageRecallEventGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author K.faWu
 * @program service
 * @description: 切面处理类
 * @create 2022-08-04 16:25
 **/
@Aspect
@Component
@Slf4j
public class BotAspect {

    @Autowired
    private BotConfig botConfig;
    /**
     * 判断机器人是否启动
     */
    @Around("execution(* com.kang.listener..*.*(..))")
    public Object botListener(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        Object[] args = point.getArgs();
        for (Object arg : args) {
            if (arg instanceof GroupContainer) {
                GroupContainer groupMsg = (GroupContainer)arg;
                String groupCode = groupMsg.getGroupInfo().getGroupCode();
                if (botConfig.getBotState(groupCode)) {
                    result = point.proceed();
                    break;
                }
            } else if (arg instanceof PrivateMsg || arg instanceof MessageRecallEventGet) {
                //私聊以及私聊撤回
                result = point.proceed();
                break;
            }
        }
        return result;
    }
}
