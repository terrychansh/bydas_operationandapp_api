package com.benyi.energy.service;

import com.benyi.energy.domain.CidRelation;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 网关、微逆关系Service接口
 * 
 * @author TerryChan
 * @date 2023-04-12
 */
public interface ICidRelationRoadService
{



    /**
     * 查询微逆板子数据
     *
     * @param: vid;cid；plantid
     * @return
     */
    public List<CidRelation> selectByCidAndVidAndPlantID(String cid,String vid,long plantID);

    /**
     * 更新cid，vid，loop的日、周、月、年、总发电量
     * @param cidRelation
     * @return
     */
    public int updateRelationByCidVidLoop(CidRelation cidRelation);



    public int updateCidRelationForMigrate(CidRelation cidRelation);
}
