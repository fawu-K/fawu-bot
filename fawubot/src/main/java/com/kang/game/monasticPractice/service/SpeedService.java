package com.kang.game.monasticPractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.entity.monasticPractice.play2.Speed;


import java.util.List;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-08-19 17:08
 **/

public interface SpeedService extends IService<Speed> {
    int getGasNum();

    List<Speed> getAll();

    List<Speed> getSpeedList(String text);
}
