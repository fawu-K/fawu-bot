package com.kang.game.monasticPractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.entity.monasticPractice.play2.Lv;
import com.kang.game.monasticPractice.mapper.LvMapper;
import com.kang.game.monasticPractice.service.LvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-08-19 17:08
 **/
@Service
public class LvServiceImpl extends ServiceImpl<LvMapper, Lv> implements LvService {
    @Autowired
    private LvMapper lvMapper;

    @Override
    public List<Lv> getAll() {
        return lvMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public List<Lv> getLvList(String text) {
        QueryWrapper<Lv> speedQueryWrapper = new QueryWrapper<>();
        speedQueryWrapper.eq("type", text);
        speedQueryWrapper.orderBy(true, true, "lv");
        return lvMapper.selectList(speedQueryWrapper);
    }
}
