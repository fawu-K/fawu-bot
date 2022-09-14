package com.kang.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.entity.GroupState;

import java.util.List;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-09-09 15:09
 **/

public interface GroupStateService extends IService<GroupState> {

    /**
     * 获取全部群状态
     * @return 全部群状态
     */
    List<GroupState> getAll();

    /**
     * 根据code判断是否有该群状态，若有则更新，没有则添加
     * @param code 群号
     * @param state 状态
     */
    void save(String code, boolean state);
}
