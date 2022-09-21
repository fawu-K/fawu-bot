package com.kang.game.monasticPractice.aop;

import com.kang.commons.util.BotUtil;
import com.kang.commons.util.CommonsUtils;
import com.kang.config.PlayConfig;
import com.kang.entity.monasticPractice.play2.Role;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.events.MessageGet;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 问大天荒切面
 * @create 2022-09-20 14:06
 **/

@Aspect
@Component
@Slf4j
public class PlayAspect {

    /**
     * 是否能够开始修炼，即只有生成角色的才能进行
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.kang.game.monasticPractice.listener..*.*(..))")
    public Object botListener(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        Object[] args = point.getArgs();
        //遍历执行方法的所有参数
        for (Object arg : args) {
            //找到消息体
            if (arg instanceof MessageGet) {
                MessageGet msgGet = (MessageGet) arg;
                //发送人信息
                String code = BotUtil.getCode(msgGet);

                //该发送人拥有账号
                if (PlayConfig.getRoleMap().containsKey(code)) {
                    result = point.proceed();
                    break;
                }
            }
        }
        return result;
    }
}
