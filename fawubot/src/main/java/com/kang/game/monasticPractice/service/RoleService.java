package com.kang.game.monasticPractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.vo.RoleVo;

import java.math.BigDecimal;

/**
 * @author K.faWu
 * @program service
 * @description:play2
 * @create 2022-08-19 17:07
 **/

public interface RoleService extends IService<Role> {
    Role init(String accountCode, String name, String sex);

    /**
     * 修炼，增加角色修为
     * @param role
     * @return
     */
    RoleVo cultivation(Role role);

    /**
     * 对角色进行增加经验操作
     * @param role 角色
     * @param addExp 增加的经验
     * @return 增加完成的角色
     */
    RoleVo addExp(Role role, BigDecimal addExp);

    /**
     * 突破到新的等级
     * @param role
     */
    void breach(Role role);
}
