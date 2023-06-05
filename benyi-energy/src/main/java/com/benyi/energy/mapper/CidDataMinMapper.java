package com.benyi.energy.mapper;


import com.benyi.energy.domain.CidDataMin;
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
public interface CidDataMinMapper {



    public List<Map> selectByDay(Map paramMap);




}
