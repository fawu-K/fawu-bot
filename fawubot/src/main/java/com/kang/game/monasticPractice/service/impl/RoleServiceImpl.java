package com.kang.game.monasticPractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.config.PlayConfig;
import com.kang.entity.monasticPractice.play2.Lv;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Speed;
import com.kang.entity.monasticPractice.play2.vo.RoleVo;
import com.kang.game.monasticPractice.mapper.RoleMapper;
import com.kang.game.monasticPractice.service.LvService;
import com.kang.game.monasticPractice.service.RoleService;
import com.kang.game.monasticPractice.service.SpeedService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-08-19 17:10
 **/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    private final RoleMapper roleMapper;
    private final SpeedService speedService;
    private final LvService lvService;

    public RoleServiceImpl(SpeedService speedService, LvService lvService, RoleMapper roleMapper) {
        this.speedService = speedService;
        this.lvService = lvService;
        this.roleMapper = roleMapper;
    }


    @Override
    public Role init(String accountCode, String name, String sex) {
        Role role = new Role(accountCode, name, sex);
        int gasNum = speedService.getGasNum();
        role.setGasNum(gasNum);
        roleMapper.insert(role);
        return role;
    }

    /**
     * 修炼，增加角色修为
     * @param role
     * @return
     */
    @Override
    public RoleVo cultivation(Role role) {

        //获取用户信息，根据等级以及先天之气数量进行获取经验
        Speed speed = PlayConfig.getSpeedMap(role.getGasNum());
        Lv lv = PlayConfig.getLvMap(role.getLvType(), role.getLv());

        BigDecimal speed1 = speed.getSpeed();
        BigDecimal exp = lv.getExp();

        //计算出本次要增加的经验
        BigDecimal addExp = speed1.multiply(exp);
        //判断该次增加的经验是否超出了当前等级需要的经验
        if (lv.getExpMax().compareTo(role.getExp().add(addExp)) <= 0) {
            //等级经验超过了
            role.setExp(lv.getExpMax());
        } else {
            //未升级
            role.setExp(role.getExp().add(addExp));
        }
        roleMapper.updateById(role);

        return new RoleVo(role, lv);
    }

    /**
     * 突破到新的等级
     * @param role
     */
    @Override
    public void breach(Role role) {
        Lv lv = PlayConfig.getLvMap(role.getLvType(), role.getLv());

        //经验清零
        role.setExp(new BigDecimal(0));
        //等级+1
        role.setLv(role.getLv() + 1);
        //血量增加
        role.setHp(role.getHp() + lv.getHp());
        //攻击力增加
        role.setAttack(role.getAttack() + lv.getAttack());
        //防御力增加
        role.setDefe(role.getDefe() + lv.getDefe());

        roleMapper.updateById(role);
    }
}
