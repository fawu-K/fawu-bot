package com.kang.game.monasticPractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.entity.monasticPractice.play2.Equipment;
import com.kang.game.monasticPractice.mapper.EquipmentMapper;
import com.kang.game.monasticPractice.mapper.RoleEquipmentMapper;
import com.kang.game.monasticPractice.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 装备服务类
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-10-20 15:04
 **/

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private RoleEquipmentMapper roleEquipmentMapper;

}
