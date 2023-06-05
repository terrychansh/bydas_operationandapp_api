package com.benyi.energy.mapper;


import com.benyi.energy.domain.CidDataYear;
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
public interface CidDataYearMapper {


    /**
     * 插入多条电量Hour数据
     *
     * @param cidDataYear 电量数据month
     */
    public void insertCidDataYear( CidDataYear cidDataYear );

    public List<CidDataYear> selectByVidAndCidOrderByIdLimit1(CidDataYear cidDataYear);

    public List<CidDataYear>  selectByVidAndCidOrderByIdDescLimit1(CidDataYear cidDataYear);

    public List<CidDataYear>  selectByCidDataYear(CidDataYear cidDataYear);

    public int updateByCidDataYear(CidDataYear cidDataYear);

    public Map analyseDataForPlantAndCurrentYear(Map paramMap);

    public List<Map> sumEnergyByCidAndVid(Map paramMap);





}
