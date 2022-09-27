package com.kang.game.monasticPractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kang.entity.monasticPractice.play2.Event;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 17:18
 **/
@Mapper
public interface EventMapper extends BaseMapper<Event> {
}
