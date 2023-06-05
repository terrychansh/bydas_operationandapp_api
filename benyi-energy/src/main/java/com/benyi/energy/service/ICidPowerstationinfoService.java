package com.benyi.energy.service;

import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidPowerstationinfo;
import org.apache.ibatis.annotations.Param;

/**
 * 电站列表Service接口
 * 
 * @author wuqiguang
 * @date 2022-08-01
 */
public interface ICidPowerstationinfoService 
{

    /**
     * 根据电站ID，查询时区便宜量
     *
     * @param id
     * @return id====创建数量
     */
    public List<Map> selectUTCZoneOfPowerstationByID(long id);
    /**
     * 根据deptId
     *
     * @param deptId
     * @return id====创建数量
     */
    public List<CidPowerstationinfo> selectCidPowerstationinfoListByDeptID(Long deptId);

    /**
     *获取所有电站列表
     *
     * @return id====创建数量
     */
    public List<CidPowerstationinfo> selectAllCidPowerstationinfoTimezoneList();

    public List<CidPowerstationinfo> selectCidPowerstationinfoListForPlantList(CidPowerstationinfo cidPowerstationinfo);

    /**
     * 查询电站
     * 
     * @param id 电站列表主键
     * @return 电站
     */
    public CidPowerstationinfo selectCidPowerstationinfoById(Long id);

    /**
     * 查询电站
     *
     * @param name 电站名称
     * @return 电站
     */
    public CidPowerstationinfo selectCidPowerstationinfoByStationName(String name);

    /**
     * 查询电站列表列表
     * 
     * @param cidPowerstationinfo 电站列表
     * @return 电站列表集合
     */
    public List<CidPowerstationinfo> selectCidPowerstationinfoList(CidPowerstationinfo cidPowerstationinfo);


    /**
     * 根据deptId与日期group by 查询电站列表
     *
     * @param deptId
     * @return id====创建数量
     */
    public List<CidPowerstationinfo> selectCidPowerstationinfoListByGroupCreateTime(Long deptId);

    /**
     * 新增电站列表
     * 
     * @param cidPowerstationinfo 电站列表
     * @return 结果
     */
    public int insertCidPowerstationinfo(CidPowerstationinfo cidPowerstationinfo);

    /**
     * 修改电站列表
     * 
     * @param cidPowerstationinfo 电站列表
     * @return 结果
     */
    public int updateCidPowerstationinfo(CidPowerstationinfo cidPowerstationinfo);

    /**
     * 批量删除电站列表
     * 
     * @param ids 需要删除的电站列表主键集合
     * @return 结果
     */
    public int deleteCidPowerstationinfoByIds(Long[] ids);

    /**
     * 删除电站列表信息
     * 
     * @param id 电站列表主键
     * @return 结果
     */
    public int deleteCidPowerstationinfoById(Long id);


    /**
     * 修改用户头像
     *
     * @param id 电站id
     * @param energyImageUrl 电站图片地址
     * @return 结果
     */
    public boolean updatePowerImage(Long id,String energyImageUrl);
}
