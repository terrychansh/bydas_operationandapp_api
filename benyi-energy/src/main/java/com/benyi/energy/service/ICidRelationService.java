package com.benyi.energy.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidRelation;

/**
 * 网关、微逆关系Service接口
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
public interface ICidRelationService 
{


    /**
     * 按照类型统计
     * @param
     * @param
     * @return
     */
    public List<Map> countCidRelationByCidType();


    public List<Map> selectByVid(String vid);

    /**
     * 查询所有微逆数据
     *
     * @param paramMap:vid;cid
     * @return
     */
    public List<Map> selectPowerstationIDByCidAndVid(Map paramMap);


    /**
     * 查询所有微逆vid数据
     *
     * @param
     * @return
     */
    public List<Map> selectVidByCidAndPlantID(String cid, Long plantID);


    /**
     *取得当前电站下面的emu
     *
     * @param
     * @return 发电信息
     */
    public List<String>getEmuList(long stationID);
    /**
     *根据relation统计当前的功率
     *
     * @param
     * @return 发电信息
     */
    public String sumCurrentPowerByPowerStationID(long stationID);

    /**
     *根据组织下面的所有设备数据
     *
     * @param deptId 发电信息主键
     * @return 发电信息
     */
    public List<Map> selectAllDeviceByDeptId(Long deptId);




    /**
     *根据组织下面的所有MIs数据
     *
     * @param deptId 发电信息主键
     * @return 发电信息
     */
    public List<CidRelation> selectVidDataByDeptId(Long deptId);

    /**
     *获取所有非在线设备
     *
     * @param
     * @return 设备数据
     */
    public List<CidRelation> selectNotOfflineCidRelation();

    /**
     *根据电站ID获取微逆SN及状态列表
     *
     * @param powerStationId 发电信息主键
     * @return 发电信息
     */
    public List<Map> selectVidListByPowerStationID(Long powerStationId);

    /**
     *根据电站ID获取微逆的状态统计数据
     *
     * @param powerStationId 发电信息主键
     * @return 发电信息
     */
    public List<Map> analyseDeviceStatus(long powerStationId,long deptID);

    /**
     * 查询网关、微逆关系
     * 
     * @param id 网关、微逆关系主键
     * @return 网关、微逆关系
     */
    public CidRelation selectCidRelationById(Long id);

    /**
     * 查询网关、微逆关系列表
     * 
     * @param cidRelation 网关、微逆关系
     * @return 网关、微逆关系集合
     */
    public List<CidRelation> selectCidRelationList(CidRelation cidRelation);


    /**
     * 根据电站ids查询电站绑定的网关与微逆
     *
     * @param powerstationId 电站ID
     * @param deptId 机构ID
     * @return 网关、微逆关系集合
     */
    public List<CidRelation> selectCidRelationListByPowerStationIds(Long powerstationId,Long deptId);


    /**
     * 获取网关与微逆关系列表（包含路信息）
     * @param powerStationId
     * @return
     */
    public List<CidRelation> selectCidRelationByStationIdWithGroup(Long powerStationId,String cid,String vid);


    /**
     * 获取创建网关数量
     * @param energyDeptId
     * @return
     */
    public List<CidRelation> selectCreateCidCount(Long energyDeptId);

    /**
     * 获取创建网关数量
     * @param energyDeptId
     * @return
     */
    public List<CidRelation> selectCreateVidCount(Long energyDeptId);


    /**
     * 获取创建网关数量
     * @param energyDeptId
     * @return
     */
    public List<CidRelation> selectCidCount(Long energyDeptId);

    /**
     * 获取创建微逆数量
     * @param energyDeptId
     * @return
     */
    public List<CidRelation> selectVidCount(Long energyDeptId);

    /**
     * 新增网关、微逆关系
     * 
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    public int insertCidRelation(CidRelation cidRelation);

    /**
     * 修改网关、微逆关系
     * 
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    public int updateCidRelation(CidRelation cidRelation);

    public void updateRelationOffline(long id);

    /**
     * 更新线上为线下
     *
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    public int updateCidRelationWhenOffline(CidRelation cidRelation);

    public int resetCurrentPowerEnergyForOfflineCidrelation(Date updateTime);

    /**
     * 修改网关、微逆关系 for add
     *
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    public int updateCidRelationForAdd(CidRelation cidRelation);


    /**
     * 修改网关、微逆关系 for add device
     *
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    public int updateCidRelationForAddMI(CidRelation cidRelation);


    public int updateCidRelationForMigrate(CidRelation cidRelation);

    /**
     * 0点重置今日发电量
     * @return
     */
    public int updateForResetCurrentPowerEnergy();

    /**
     * 批量删除网关、微逆关系
     * 
     * @param ids 需要删除的网关、微逆关系主键集合
     * @return 结果
     */
    public int deleteCidRelationByIds(Long[] ids);

    /**
     * 删除网关、微逆关系信息
     * 
     * @param id 网关、微逆关系主键
     * @return 结果
     */
    public int deleteCidRelationById(Long id);


    /**
     * 根据电站获取网关列表
     * @param queryRelation
     * @return
     */
    public List<CidRelation> selectCidRelationListGroupBy(CidRelation queryRelation);


    /**
     * 获取网关绑定的微逆
     * @param queryRelation
     * @return
     */
    public List<CidRelation> selectCidRelationListGroupByWithCid(CidRelation queryRelation);


    /**
     * 获取设备数量With状态
     * @param deptId
     * @return
     */
    public List<CidRelation> getEquptStatuCount(Long deptId);

    /**
     * 根据所有信息进行删除操作
     * @param cid
     * @param vid
     * @param roadType
     * @param powerStationId
     * @return
     */
    public int deleteCidRelationByInfo(String cid,String vid,String roadType,Long powerStationId,String updateBy);

    /**
     * 根据所有信息进行删除操作
     * @param cid
     * @param vid
     * @param powerStationId
     * @return
     */
    public int deleteBatchCidRelationByInfo(String cid,String[] vid,Long powerStationId,String updateBy);


    /**
     * 根据Cid进行删除
     * @param cid
     * @param powerStationId
     * @return
     */
    public int deleteCidRelationByCid(String cid,Long powerStationId,String updateBy);

    /**
     * 获取cidList
     * @param deptId 帐号机构id
     * @param cid 网关
     * @param searchDate 创建日期
     * @return
     */
    public List<CidRelation> getCidList(Long deptId,Long powerStationId,String cid,String searchDate);


    /**
     * 根据cid获取空的vid
     * @param cid
     * @return
     */
    public CidRelation getEmptyVidInfoByCid(String cid);



    /**
     * 根据id 物理删除（用于新增时删除空的vid）
     * @param id
     * @return
     */
    public int deleteEmptyVidById(Long id);


    /**
     * 获取是否有未确认的
     * @param deptId
     * @return
     */
    public List<CidRelation> getIsConfirmByDept(Long deptId);


    /**
     * 获取网关或微逆的详情
     * @param cid
     * @param vid
     * @return
     */
    public CidRelation getCidOrVidDetail(String cid,String vid,String roadType);

    /**
     * 获取网关下微逆的详情
     * @param cid
     * @return
     */
    public List<CidRelation> getCidChildDetail(String cid);

    /**
     * 更新发电量信息
     * @param cidRelation
     * @return
     */
    public int updateCidRelationEnergyInfo(CidRelation cidRelation);


    /**
     * 获取所有电站的发电情况
     * @return
     */
    public List<CidRelation> selectPowerEnergy();

    /**
     * 自动绑定关系查询所有已录入的关系情况
     * @return
     */
    public List<CidRelation> selectCidVidLoopPlantForRealtion();


    /**
     * 根据cid获取电站id
     * @return
     */
    public List<CidRelation> getPowerStationIdByCid(String cid);


    /**
     * 根据cid,vid,loop 获取relation记录
     * @return
     */
    public List<CidRelation> getRelationByCidVidLoop(String cid,String vid,String roadType);


    /**
     * 获取网关电站信息  cid_powerstionId
     * @return
     */
    public List<CidRelation> getPlantIdByRelationGroup();

    /**
     * 更新cid，vid，loop的日、周、月、年、总发电量
     * @param cidRelation
     * @return
     */
    public int updateRelationByCidVidLoop(CidRelation cidRelation);


    /**
     * 获取月发电量
     * @return
     */
    public List<CidRelation> selectPlantMonthEnergy();

    /**
     * 获取年发电量
     * @return
     */
    public List<CidRelation> selectPlantYearEnergy();

    /**
     * 获取总发电量
     * @return
     */
    public List<CidRelation> selectPlantCountEnergy();

    /**
     * 设备列表tree
     * @param deptId
     * @param cid
     * @return
     */
    public List<Map> selectEmuList(Long deptId,Long plantId,String cid);

    /**
     * 设备列表tree
     * @param deptId
     * @param cid
     * @return
     */
    public List<Map> selectEmuListOnly(Long deptId,Long plantId,String cid);

    public List<Map>     selectEmuListOnlyInPlantArr(Long[] plantId,String cid);


    /**
     * 设备列表tree
     * @param deptId
     * @param cid
     * @return
     */
    public List<Map> selectEmuListOnly(Long deptId,Long plantId,String cid,String cidType);

    /**
     * 根据网关发送指令
     * @return
     */
    public int updateCidRelationByCammand(String cid,String cammandUpdateMap);


    /**
     * 根据电站id与 cid,vid类型查询数量
     * @param powerStationId
     * @param type
     * @return
     */
    public List<CidRelation> getAppCidVidCount(Long powerStationId,String type);
}
