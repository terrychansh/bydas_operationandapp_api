package com.benyi.energy.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidData;
import com.benyi.energy.domain.CidRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网关、微逆关系Mapper接口
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
@Mapper
public interface CidRelationMapper 
{



    public List<Map> selectByVid(String vid);

    public List<Map> countCidRelationByCidType();

    public List<Map> selectPowerstationIDByCidAndPlantID(Map paramMap);


        /**
         * 查询所有微逆数据
         *
         * @param paramMap:vid;cid
         * @return
         */
    public List<Map> selectPowerstationIDByCidAndVid(Map paramMap);


    public void updateRelationOffline(long id);
    /**
     *取得当前电站下面的emu
     *
     * @param
     * @return Emu List
     */
    public List<String>getEmuList(long stationID);



    /**
     *取得当前电站下面的emu和状态
     *
     * @param
     * @return Emu List
     */
    public List<String>getEmuAndStatusList(long stationID);


    /**
     *根据relation统计当前的功率
     *
     * @param
     * @return 发电信息
     */
    public String sumCurrentPowerByPowerStationID(long powerStationID);


    /**
     *统计微逆的状态分布数据
     *
     * @param
     * @return 发电信息
     */
    public  List<Map> analyseDeviceStatus(Map paramMap);


    /**
     *根据组织下面的所有MIs数据
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
     * 根据powerStationId查询设备列表
     *
     * @param powerStationId 网关、微逆关系主键
     * @return 网关、微逆关系
     */
    public List<Map> selectVidListByPowerStationID(long powerStationId);


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
     * @param param 网关、微逆关系
     * @return 网关、微逆关系集合
     */
    public List<CidRelation> selectCidRelationListByPowerStationIds(Map<String,Object> param);

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

    /**
     * 修改网关、微逆关系
     *
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    public int  updateCidRelationWhenOffline(CidRelation cidRelation);

    public int  resetCurrentPowerEnergyForOfflineCidrelation(Date updateTime);

    /**
     * 修改网关、微逆关系 for add
     *
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    public int updateCidRelationForAdd(CidRelation cidRelation);

    /**
     * 修改网关、微逆关系 for add
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
     * 删除网关、微逆关系
     * 
     * @param id 网关、微逆关系主键
     * @return 结果
     */
    public int deleteCidRelationById(Long id);

    /**
     * 批量删除网关、微逆关系
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidRelationByIds(Long[] ids);

    public List<CidRelation> selectCidRelationListGroupBy(CidRelation queryRelation);

    public List<CidRelation> selectCidRelationListGroupByWithCid(CidRelation queryRelation);

    /**
     * 获取网关与微逆关系列表（包含路信息）
     * @param param
     * @return
     */
    public List<CidRelation> selectCidRelationByStationIdWithGroup(Map<String,Object> param);


    /**
     * 按日期获取创建的网关数量
     * @param param
     * @return
     */
    public List<CidRelation> selectCreateCidCount(Map<String,Object> param);

    /**
     * 按日期获取创建的微逆数量
     * @param param
     * @return
     */
    public List<CidRelation> selectCreateVidCount(Map<String,Object> param);


    /**
     * 获取创建网关数量
     * @param param
     * @return
     */
    public List<CidRelation> selectCidCount(Map<String,Object> param);

    /**
     * 获取创建微逆数量
     * @param param
     * @return
     */
    public List<CidRelation> selectVidCount(Map<String,Object> param);


    /**
     * 获取设备数量With状态
     * @param map
     * @return
     */
    public List<CidRelation> getEquptStatuCount(Map<String, Object> map);

    /**
     * 根据所有信息进行删除操作
     * @param param
     * @return
     */
    public int deleteCidRelationByInfo(Map<String,Object> param);


    /**
     * 根据所有信息进行删除操作
     * @param param
     * @return
     */
    public int deleteBatchCidRelationByInfo(Map<String,Object> param);




    /**
     * 根据Cid进行删除
     * @param param
     * @return
     */
    public int deleteCidRelationByCid(Map<String,Object> param);

    /**
     * 获取cidlist   deptId by group
     * @param param
     * @return
     */
    public List<CidRelation> getCidList(Map<String,Object> param);

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
     * @param param
     * @return
     */
    public CidRelation getCidOrVidDetail(Map<String,Object> param);

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
     * @param param
     * @return
     */
    public List<CidRelation> getRelationByCidVidLoop(Map<String,Object> param);


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
     * 查找非在线设备
     * @return
     */
    public List<CidRelation> selectNotOfflineCidRelation();

    /**
     * 获取机构下emu列表
     * @param param
     * @return
     */
    public List<Map> selectEmuTree(Map<String,Object> param);


    /**
     * 获取机构下emu列表
     * @param param
     * @return
     */
    public List<Map> selectEmuListOnly(Map<String,Object> param);

    public List<Map>     selectEmuListOnlyInPlantArr(Map<String,Object> param);


    /**
     * 根据网关发送指令
     * @return
     */
    public int updateCidRelationByCammand(Map<String,Object> param);

    /**
     * 批量更新网关、微逆关系
     * @return
     */
    public int updateBatchCidRelationByCammand(Map<String,Object> param);

    /**
     * 根据电站id与 cid,vid类型查询数量
     * @param param
     * @return
     */
    public List<CidRelation> getAppCidVidCount(Map<String,Object> param);
}
