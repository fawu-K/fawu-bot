package com.kang.commons.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.Constants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: service
 * @description:
 * @author: K.faWu
 * @create: 2022-06-10 14:18
 **/

public class CommonsUtils {

    /**
     * 判断对象是否为空
     * @param obj 需要判断的对象
     * @return 是否为空
     */
    public static Boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        //当是集合的时候
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).size() == 0;
        }
        //当是string的时候
        if (obj instanceof String) {
            return "".equals(obj);
        }
        return false;
    }

    /**
     * 判断是否不为空
     * @param obj 需要判断的对象
     * @return 是否不为空
     */
    public static Boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static <T> QueryWrapper<T> selectCode(String accountCode, String groupCode) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Constants.ACCOUNT_CODE, accountCode);
        queryWrapper.eq(Constants.GROUP_CODE, groupCode);
        return queryWrapper;
    }

    public static Map<String, Object> catCodeToMap(String catCode) {
        catCode = catCode.replace("[", "{").replace("]", "}").replaceAll(":", "=");
        return mapStringToMap(catCode);
    }

    public static Map<String, Object> mapStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",");
        Map<String, Object> map = new HashMap<>(8);
        for (String string : strs) {
            String key = string.split("=")[0];
            String value = string.split("=")[1];
            map.put(key, value);
        }
        return map;
    }
}
