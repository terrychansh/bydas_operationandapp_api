package com.benyi.energy.mapper;

import com.benyi.energy.domain.CidData;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * 发电信息Mapper接口
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
public interface CidDataRoadMapper
{


    /**
     * 获取cid的第一条通讯时间
     *
     * @param
     * @return 发电信息
     */
    public List<CidData>  selectAll();



}
