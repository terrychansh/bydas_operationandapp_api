package com.benyi.energy.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2022/12/26 09:47
 */
@Mapper
public interface CidDataHourMapper {


    /**
     * 插入多条电量Hour数据
     *
     * @param paramMap 电量数据Hour
     */
    public List<Map> selectByDay(Map paramMap);
    public List<Map> selectBySomeDay(Map map);
    public List<Map> analyseByOneDay(Map map);




}
