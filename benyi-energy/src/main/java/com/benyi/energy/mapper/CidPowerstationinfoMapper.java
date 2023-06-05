package com.benyi.energy.mapper;

import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidPowerstationinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 电站列表Mapper接口
 * 
 * @author wuqiguang
 * @date 2022-08-01
 */
@Mapper
public interface CidPowerstationinfoMapper 
{



    public List<Map> selectUTCZoneOfPowerstationByID(long id);

    /**
     * 根据deptId
     *
     * @param deptId
     * @return id====创建数量
     */
    public List<CidPowerstationinfo> selectCidPowerstationinfoListByDeptID(Long deptId);


    /**
     * 查询电站列表
     * 
     * @param id 电站列表主键
     * @return 电站列表
     */
    public CidPowerstationinfo selectCidPowerstationinfoById(Long id);

    /**
     * 查询电站列表
     *
     * @param name 电站列表主键
     * @return 电站列表
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
     * 查询电站列表列表
     *
     * @param cidPowerstationinfo 电站列表
     * @return 电站列表集合
     */
    public List<CidPowerstationinfo> selectCidPowerstationinfoListForPlantList(CidPowerstationinfo cidPowerstationinfo);


    public List<CidPowerstationinfo>  selectAllCidPowerstationinfoTimezoneList();

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
     * 删除电站列表
     * 
     * @param id 电站列表主键
     * @return 结果
     */
    public int deleteCidPowerstationinfoById(Long id);

    /**
     * 批量删除电站列表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidPowerstationinfoByIds(Long[] ids);


    /**
     * 修改用户头像
     *
     * @param id             电站id
     * @param energyImageUrl 电站图片地址
     * @return 结果
     */
    public int updatePowerImage(@Param("id") Long id, @Param("energyImageUrl") String energyImageUrl);

}
