package com.benyi.energy.service;

import com.benyi.energy.domain.CidRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/3/14 07:09
 */
public interface ICidVidRoadRelationService {

    public int insertBatchCidVidRoadRelation(@Param("list") List<CidRelation> dataList);
    public List<CidRelation> selectVidListByCidAndPowerStationId(CidRelation cidVidRoadRelation);

}
