package com.benyi.energy.mapper;

import com.benyi.energy.domain.CidRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 网关、微逆关系Mapper接口
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
@Mapper
public interface CidRelationRoadMapper
{



    /**
     * 根据电站id与 cid,vid类型查询数量
     * @param param
     * @return
     */
    public List<Map> selectByCidAndVidAndPlantID(Map<String,Object> param);

    /**
     * 更新cid，vid，loop的日、周、月、年、总发电量
     * @param cidRelation
     * @return
     */
    public int updateRelationByCidVidLoop(CidRelation cidRelation);

    public int insertBatchCidVidRoadRelation(@Param("list") List<CidRelation> dataList);

    public List<CidRelation> selectVidListByCidAndPowerStationId(CidRelation cidVidRoadRelation);

    public int updateCidRelationForMigrate(CidRelation cidRelation);


}
