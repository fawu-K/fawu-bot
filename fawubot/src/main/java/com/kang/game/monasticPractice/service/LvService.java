package com.kang.game.monasticPractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.entity.monasticPractice.play2.Lv;

import java.util.List;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-08-19 17:06
 **/

public interface LvService extends IService<Lv> {
    List<Lv> getAll();

    List<Lv> getLvList(String text);
}
