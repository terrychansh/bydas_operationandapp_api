package com.benyi.energy.service.impl;

import java.util.List;

import com.benyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Map;

import com.benyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.benyi.energy.mapper.CidPowerstationinfoMapper;
import com.benyi.energy.domain.CidPowerstationinfo;
import com.benyi.energy.service.ICidPowerstationinfoService;

import javax.annotation.Resource;

/**
 * 电站列表Service业务层处理
 * 
 * @author wuqiguang
 * @date 2022-08-01
 */
@Service
public class CidPowerstationinfoServiceImpl implements ICidPowerstationinfoService 
{

    @Resource
    private CidPowerstationinfoMapper cidPowerstationinfoMapper;


    /**
     * 根据电站ID，查询时区便宜量
     *
     * @param id
     * @return id====创建数量
     */
    public List<Map> selectUTCZoneOfPowerstationByID(long id){
        return cidPowerstationinfoMapper.selectUTCZoneOfPowerstationByID(id);
    }


    /**
     * 根据deptId
     *
     * @param deptId
     * @return id====创建数量
     */
    public List<CidPowerstationinfo> selectCidPowerstationinfoListByDeptID(Long deptId){
        return cidPowerstationinfoMapper.selectCidPowerstationinfoListByDeptID(deptId);

    }

    @Override
    public List<CidPowerstationinfo> selectAllCidPowerstationinfoTimezoneList() {
        return cidPowerstationinfoMapper.selectAllCidPowerstationinfoTimezoneList();
    }

    /**
     * 查询电站列表
     * 
     * @param id 电站列表主键
     * @return 电站列表
     */
    @Override
    public CidPowerstationinfo selectCidPowerstationinfoById(Long id)
    {
        return cidPowerstationinfoMapper.selectCidPowerstationinfoById(id);
    }

    /**
     * 查询电站
     *
     * @param name 电站名称
     * @return 电站
     */
    public CidPowerstationinfo selectCidPowerstationinfoByStationName(String name){
        return cidPowerstationinfoMapper.selectCidPowerstationinfoByStationName(name);

    }


    /**
     * 查询电站列表列表
     * 
     * @param cidPowerstationinfo 电站列表
     * @return 电站列表
     */
    @Override
    public List<CidPowerstationinfo> selectCidPowerstationinfoList(CidPowerstationinfo cidPowerstationinfo)
    {
        return cidPowerstationinfoMapper.selectCidPowerstationinfoList(cidPowerstationinfo);
    }

    /**
     * 查询电站列表列表
     *
     * @param cidPowerstationinfo 电站列表
     * @return 电站列表
     */
    @Override
    public List<CidPowerstationinfo> selectCidPowerstationinfoListForPlantList(CidPowerstationinfo cidPowerstationinfo)
    {
        return cidPowerstationinfoMapper.selectCidPowerstationinfoListForPlantList(cidPowerstationinfo);
    }



    /**
     * 根据deptId与日期group by 查询电站列表
     *
     * @param deptId
     * @return id====创建数量
     */
    @Override
    public List<CidPowerstationinfo> selectCidPowerstationinfoListByGroupCreateTime(Long deptId) {
        return cidPowerstationinfoMapper.selectCidPowerstationinfoListByGroupCreateTime(deptId);
    }

    /**
     * 新增电站列表
     * 
     * @param cidPowerstationinfo 电站列表
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCidPowerstationinfo(CidPowerstationinfo cidPowerstationinfo)
    {
        cidPowerstationinfo.setCreateTime(DateUtils.getNowDate());
        int rows =-1;
        try {
            rows= cidPowerstationinfoMapper.insertCidPowerstationinfo(cidPowerstationinfo);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return rows;
    }

    /**
     * 修改电站列表
     * 
     * @param cidPowerstationinfo 电站列表
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCidPowerstationinfo(CidPowerstationinfo cidPowerstationinfo)
    {
//        cidPowerstationinfo.setUpdateTime(DateUtils.getNowDate());
        return cidPowerstationinfoMapper.updateCidPowerstationinfo(cidPowerstationinfo);
    }

    /**
     * 批量删除电站列表
     * 
     * @param ids 需要删除的电站列表主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCidPowerstationinfoByIds(Long[] ids)
    {
        return cidPowerstationinfoMapper.deleteCidPowerstationinfoByIds(ids);
    }

    /**
     * 删除电站列表信息
     * 
     * @param id 电站列表主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCidPowerstationinfoById(Long id)
    {
        return cidPowerstationinfoMapper.deleteCidPowerstationinfoById(id);
    }


    /**
     * 修改用户头像
     *
     * @param id             电站id
     * @param energyImageUrl 电站图片地址
     * @return 结果
     */
    @Override
    public boolean updatePowerImage(Long id, String energyImageUrl) {
        return cidPowerstationinfoMapper.updatePowerImage(id,energyImageUrl)  > 0;
    }
}
