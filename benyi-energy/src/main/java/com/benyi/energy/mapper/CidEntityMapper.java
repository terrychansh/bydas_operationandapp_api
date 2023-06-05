package com.benyi.energy.mapper;

import com.benyi.energy.domain.CidEntity;
import com.benyi.energy.domain.CidRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/5/26 12:25
 */
@Mapper
public interface CidEntityMapper {


    /**
     * 新增网关、微逆关系
     * @param cidEntity 网关、微逆关系
     * @return 结果
     */
    public int insertCidEntity(CidEntity cidEntity);

    /**
     * 修改网关、微逆关系
     * @param cidEntity 网关、微逆关系
     * @return 结果
     */
    public int updateCidEntity(CidEntity cidEntity);


    public List<CidEntity> selectCidEntityByCidAndPlantID(Map map);

    public List<Map> selectCidList(Map map);

}
