package com.benyi.energy.service.impl;

import com.alibaba.fastjson2.JSON;
import com.benyi.common.core.redis.RedisCache;
import com.benyi.common.utils.DateUtils;
import com.benyi.energy.domain.CidRelation;
import com.benyi.energy.mapper.CidRelationMapper;
import com.benyi.energy.mapper.CidRelationRoadMapper;
import com.benyi.energy.service.ICidRelationRoadService;
import com.benyi.energy.service.ICidRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关、微逆关系Service业务层处理
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
@Service
public class CidRelationRoadServiceImpl implements ICidRelationRoadService
{
    @Autowired
    private CidRelationRoadMapper cidRelationRoadMapper;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public List<CidRelation> selectByCidAndVidAndPlantID(String cid, String vid, long plantID) {

        Map paramMap = new HashMap();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("powerStationId",plantID);
        return cidRelationRoadMapper.selectByCidAndVidAndPlantID(paramMap);
    }

    @Override
    public int updateRelationByCidVidLoop(CidRelation cidRelation) {
        return cidRelationRoadMapper.updateRelationByCidVidLoop(cidRelation);
    }

    @Override
    public int updateCidRelationForMigrate(CidRelation cidRelation) {
        return cidRelationRoadMapper.updateCidRelationForMigrate(cidRelation);
    }
}
