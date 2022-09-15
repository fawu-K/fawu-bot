package com.kang.task;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 多线程依赖工具
 * @create 2022-09-15 10:36
 **/
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    /**
     * 获取bean的方法
     * 字符串的类名需要首字母小写
     * @param beanId
     * @return
     */
    public static Object getBean(String beanId){
        return applicationContext.getBean(beanId);
    }

    public static Object getBean(Class<?> clazz) {
        return applicationContext.getBean(clazz);
    }
}
