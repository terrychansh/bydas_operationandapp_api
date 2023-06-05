package com.benyi.energy.service.impl;

import com.benyi.energy.domain.CidRelation;
import com.benyi.energy.mapper.CidRelationRoadMapper;
import com.benyi.energy.service.ICidVidRoadRelationService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/3/14 07:11
 */
@Service
public class CidVidRoadRelationServiceImpl  implements ICidVidRoadRelationService {

    @Autowired
    private CidRelationRoadMapper cidRelationRoadMapper;

    public int insertBatchCidVidRoadRelation(@Param("list") List<CidRelation> dataList){

       return cidRelationRoadMapper.insertBatchCidVidRoadRelation(dataList);
    }

    public List<CidRelation> selectVidListByCidAndPowerStationId(CidRelation cidVidRoadRelation){
        return cidRelationRoadMapper.selectVidListByCidAndPowerStationId(cidVidRoadRelation);
    }


}
