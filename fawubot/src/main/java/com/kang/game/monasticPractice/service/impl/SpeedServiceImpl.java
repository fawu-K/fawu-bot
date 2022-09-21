package com.kang.game.monasticPractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.entity.monasticPractice.play2.Speed;
import com.kang.game.monasticPractice.mapper.SpeedMapper;
import com.kang.game.monasticPractice.service.SpeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-08-19 17:11
 **/
@Service
public class SpeedServiceImpl extends ServiceImpl<SpeedMapper, Speed> implements SpeedService {
    @Autowired
    private SpeedMapper speedMapper;

    @Override
    public int getGasNum() {
        List<Speed> speeds = speedMapper.selectList(new QueryWrapper<>());
        double d = Math.random() * 100;
        double probability = 0.0;
        for (Speed speed : speeds) {
            probability += speed.getProbability();
            if (d <= probability) {
                return speed.getId().intValue();
            }
        }
        return 0;
    }

    @Override
    public List<Speed> getAll() {
        List<Speed> speeds = speedMapper.selectList(new QueryWrapper<>());
        return speeds;
    }

    @Override
    public List<Speed> getSpeedList(String text) {
        QueryWrapper<Speed> speedQueryWrapper = new QueryWrapper<>();
        speedQueryWrapper.eq("type", text);
        speedQueryWrapper.orderBy(true, true, "lv");
        List<Speed> speeds = speedMapper.selectList(speedQueryWrapper);
        return speeds;
    }
}
