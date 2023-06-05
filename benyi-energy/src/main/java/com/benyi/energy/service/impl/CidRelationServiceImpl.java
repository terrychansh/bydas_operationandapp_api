package com.benyi.energy.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.benyi.common.core.redis.RedisCache;
import com.benyi.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.CidRelationMapper;
import com.benyi.energy.domain.CidRelation;
import com.benyi.energy.service.ICidRelationService;

/**
 * 网关、微逆关系Service业务层处理
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
@Service
public class CidRelationServiceImpl implements ICidRelationService 
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CidRelationMapper cidRelationMapper;

    public List<Map> countCidRelationByCidType(){
        return cidRelationMapper.countCidRelationByCidType();
    }

    @Override
    public List<Map> selectByVid(String vid) {
        return cidRelationMapper.selectByVid(vid);
    }

    public void updateRelationOffline(long id){

         cidRelationMapper.updateRelationOffline(id);
    }

    /**
     * 查询所有微逆数据
     *
     * @param paramMap:vid;cid
     * @return
     */
    public List<Map> selectPowerstationIDByCidAndVid(Map paramMap){
        return cidRelationMapper.selectPowerstationIDByCidAndVid(paramMap);
    }

    @Override
    public List<Map> selectVidByCidAndPlantID(String cid, Long plantID){
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("cid", cid);
            paramMap.put("powerStationId", plantID);
            return cidRelationMapper.selectPowerstationIDByCidAndPlantID(paramMap);
    }

    @Override
    public List<Map> selectAllDeviceByDeptId(Long deptId) {
        return cidRelationMapper.selectAllDeviceByDeptId(deptId);
    }

    @Override
    public List<CidRelation> selectVidDataByDeptId(Long deptId) {
        return cidRelationMapper.selectVidDataByDeptId(deptId);
    }

    @Override
    public List<CidRelation> selectNotOfflineCidRelation() {
        return cidRelationMapper.selectNotOfflineCidRelation();
    }

    @Override
    public List<Map> selectVidListByPowerStationID(Long powerstationId) {
        return cidRelationMapper.selectVidListByPowerStationID(powerstationId);
    }

    @Override
    public List<Map> analyseDeviceStatus(long powerStationId, long deptId) {

        Map map =new HashMap();
        if( powerStationId!=-1l){
            map.put("powerStationId",powerStationId);
        }

        map.put("deptId",deptId);
        return cidRelationMapper.analyseDeviceStatus(map);
    }


    /**
     *取得当前电站下面的emu
     *
     * @param
     * @return emu List
     */
    public List<String>getEmuList(long stationID){
        return cidRelationMapper.getEmuList(stationID);
    }

    /**
     *根据relation统计当前的功率
     *
     * @param
     * @return 发电信息
     */
    public String sumCurrentPowerByPowerStationID(long stationID){
            return cidRelationMapper.sumCurrentPowerByPowerStationID(stationID);
}

    /**
     * 查询网关、微逆关系
     * @param id 网关、微逆关系主键
     * @return 网关、微逆关系
     */
    @Override
    public CidRelation selectCidRelationById(Long id)
    {
        return cidRelationMapper.selectCidRelationById(id);
    }

    /**
     * 查询网关、微逆关系列表
     * 
     * @param cidRelation 网关、微逆关系
     * @return 网关、微逆关系
     */
    @Override
    public List<CidRelation> selectCidRelationList(CidRelation cidRelation)
    {
        return cidRelationMapper.selectCidRelationList(cidRelation);
    }

    @Override
    public List<CidRelation> selectCidRelationListByPowerStationIds(Long powerstationId,Long deptId) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("powerstationId",powerstationId);
        paramMap.put("deptId",deptId);

        return cidRelationMapper.selectCidRelationListByPowerStationIds(paramMap);
    }

    /**
     * 新增网关、微逆关系
     * 
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    @Override
    public int insertCidRelation(CidRelation cidRelation)
    {

        cidRelation.setCreateTime(DateUtils.getNowDate());
        return cidRelationMapper.insertCidRelation(cidRelation);
    }

    /**
     * 修改网关、微逆关系
     * 
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    @Override
    public int updateCidRelation(CidRelation cidRelation)
    {
        cidRelation.setUpdateTime(DateUtils.getNowDate());
        return cidRelationMapper.updateCidRelation(cidRelation);
    }

    /**
     * 修改网关、微逆关系
     *
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    @Override
    public int updateCidRelationWhenOffline(CidRelation cidRelation)
    {
        cidRelation.setUpdateTime(DateUtils.getNowDate());
        return cidRelationMapper.updateCidRelationWhenOffline(cidRelation);
    }

    @Override
    public int resetCurrentPowerEnergyForOfflineCidrelation(Date updateTime) {
        return cidRelationMapper.resetCurrentPowerEnergyForOfflineCidrelation(updateTime);
    }

    /**
     * 0点重置今日发电量
     *
     * @return
     */
    @Override
    public int updateForResetCurrentPowerEnergy() {
        return cidRelationMapper.updateForResetCurrentPowerEnergy();
    }

    /**
     * 修改网关、微逆关系 for add
     *
     * @param cidRelation 网关、微逆关系
     * @return 结果
     */
    @Override
    public int updateCidRelationForAdd(CidRelation cidRelation) {
        cidRelation.setUpdateTime(DateUtils.getNowDate());
        return cidRelationMapper.updateCidRelationForAdd(cidRelation);
    }

    @Override
    public int updateCidRelationForAddMI(CidRelation cidRelation){

        cidRelation.setUpdateTime(DateUtils.getNowDate());
        return cidRelationMapper.updateCidRelationForAddMI(cidRelation);
    }

    @Override
    public int updateCidRelationForMigrate(CidRelation cidRelation){

        cidRelation.setUpdateTime(DateUtils.getNowDate());
        cidRelationMapper.updateCidRelationForMigrate(cidRelation);

        return cidRelationMapper.updateCidRelationForMigrate(cidRelation);
    }

    /**
     * 批量删除网关、微逆关系
     * 
     * @param ids 需要删除的网关、微逆关系主键
     * @return 结果
     */
    @Override
    public int deleteCidRelationByIds(Long[] ids)
    {
        return cidRelationMapper.deleteCidRelationByIds(ids);
    }

    /**
     * 删除网关、微逆关系信息
     * 
     * @param id 网关、微逆关系主键
     * @return 结果
     */
    @Override
    public int deleteCidRelationById(Long id)
    {
        return cidRelationMapper.deleteCidRelationById(id);
    }



    /**
     * 根据电站Id获取网关列表
     * @param queryRelation
     * @return
     */
    @Override
    public List<CidRelation> selectCidRelationListGroupBy(CidRelation queryRelation) {
        return cidRelationMapper.selectCidRelationListGroupBy(queryRelation);
    }


    /**
     * 获取网关绑定的微逆
     *
     * @param queryRelation
     * @return
     */
    @Override
    public List<CidRelation> selectCidRelationListGroupByWithCid(CidRelation queryRelation) {
        return cidRelationMapper.selectCidRelationListGroupByWithCid(queryRelation);
    }

    /**
     * 获取网关与微逆关系列表（包含路信息）
     *
     * @param powerStationId
     * @return
     */
    @Override
    public List<CidRelation> selectCidRelationByStationIdWithGroup(Long powerStationId,String cid,String vid) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("powerStationId",powerStationId);

        return cidRelationMapper.selectCidRelationByStationIdWithGroup(paramMap);
    }


    /**
     * 获取创建网关数量
     *
     * @param energyDeptId
     * @return
     */
    @Override
    public List<CidRelation> selectCreateCidCount(Long energyDeptId) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("energyDeptId",energyDeptId);

        return cidRelationMapper.selectCreateCidCount(paramMap);
    }

    /**
     * 获取创建网关数量
     *
     * @param energyDeptId
     * @return
     */
    @Override
    public List<CidRelation> selectCreateVidCount(Long energyDeptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("energyDeptId",energyDeptId);
        return cidRelationMapper.selectCreateVidCount(paramMap);
    }

    /**
     * 获取创建网关数量
     *
     * @param energyDeptId
     * @return
     */
    @Override
    public List<CidRelation> selectCidCount(Long energyDeptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("energyDeptId",energyDeptId);
        return cidRelationMapper.selectCidCount(paramMap);
    }

    /**
     * 获取创建微逆数量
     *
     * @param energyDeptId
     * @return
     */
    @Override
    public List<CidRelation> selectVidCount(Long energyDeptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("energyDeptId",energyDeptId);
        return cidRelationMapper.selectVidCount(paramMap);
    }

    /**
     * 根据所有信息进行删除操作
     *
     * @param powerStationId
     * @return
     */
    @Override
    public int deleteCidRelationByInfo(String cid, String vid, String roadType, Long powerStationId,String updateBy) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("roadType",roadType);
        paramMap.put("powerStationId",powerStationId);
        paramMap.put("updateBy",updateBy);
        return cidRelationMapper.deleteCidRelationByInfo(paramMap);
    }


    /**
     * 根据所有信息进行删除操作
     *
     * @param powerStationId
     * @return
     */
    @Override
    public int deleteBatchCidRelationByInfo(String cid,String[] vid,Long powerStationId,String updateBy){


        String extraIds= null;
        if (vid!=null & vid.length>0) {
            extraIds="";
            for (String extraId : vid) {
                if (extraId != null && !"".equals(extraId)) {
                    extraIds += ",'" + extraId + "'";
                }
            }
            extraIds   =    "(" + extraIds.substring(1) + ")";
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",extraIds);
        paramMap.put("powerStationId",powerStationId);
        paramMap.put("updateBy",updateBy);

        return cidRelationMapper.deleteBatchCidRelationByInfo(paramMap);
    }



    /**
     * 根据Cid进行删除
     *
     * @param cid
     * @param powerStationId
     * @param updateBy
     * @return
     */
    @Override
    public int deleteCidRelationByCid(String cid, Long powerStationId,String updateBy) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("powerStationId",powerStationId);
        paramMap.put("updateBy",updateBy);
        return cidRelationMapper.deleteCidRelationByCid(paramMap);
    }


    /**
     * 获取设备数量With状态
     *
     * @param deptId
     * @return
     */
    @Override
    public List<CidRelation> getEquptStatuCount(Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        return cidRelationMapper.getEquptStatuCount(paramMap);
    }

    /**
     * 获取cidList
     *
     * @param deptId 帐号机构id
     * @return
     */
    @Override
    public List<CidRelation> getCidList(Long deptId,Long powerStationId,String cid,String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        paramMap.put("cid",cid);
        paramMap.put("powerStationId",powerStationId);
        paramMap.put("searchDate",searchDate);
        return cidRelationMapper.getCidList(paramMap);
    }


    /**
     * 根据cid获取空的vid
     *
     * @param cid
     * @return
     */
    @Override
    public CidRelation getEmptyVidInfoByCid(String cid) {
        return cidRelationMapper.getEmptyVidInfoByCid(cid);
    }

    /**
     * 根据id 物理删除（用于新增时删除空的vid）
     *
     * @param id
     * @return
     */
    @Override
    public int deleteEmptyVidById(Long id) {
        return cidRelationMapper.deleteEmptyVidById(id);
    }


    /**
     * 获取是否有未确认的
     *
     * @param deptId
     * @return
     */
    @Override
    public List<CidRelation> getIsConfirmByDept(Long deptId) {
        return cidRelationMapper.getIsConfirmByDept(deptId);
    }

    /**
     * 获取网关或微逆的详情
     *
     * @param cid
     * @param vid
     * @return
     */
    @Override
    public CidRelation getCidOrVidDetail(String cid, String vid,String roadType) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("roadType",roadType);
        return cidRelationMapper.getCidOrVidDetail(paramMap);
    }

    /**
     * 获取网关下微逆的详情
     *
     * @param cid
     * @return
     */
    @Override
    public List<CidRelation> getCidChildDetail(String cid) {
        return cidRelationMapper.getCidChildDetail(cid);
    }


    /**
     * 更新发电量信息
     *
     * @param cidRelation
     * @return
     */
    @Override
    public int updateCidRelationEnergyInfo(CidRelation cidRelation) {
        return cidRelationMapper.updateCidRelationEnergyInfo(cidRelation);
    }


    /**
     * 获取所有电站的发电情况
     *
     * @return
     */
    @Override
    public List<CidRelation> selectPowerEnergy() {
        return cidRelationMapper.selectPowerEnergy();
    }


    /**
     * 自动绑定关系查询所有已录入的关系情况
     * @return
     */
    @Override
    public List<CidRelation> selectCidVidLoopPlantForRealtion(){
        return cidRelationMapper.selectCidVidLoopPlantForRealtion();
    }

    /**
     * 根据cid获取电站id
     * @return
     */
    @Override
    public List<CidRelation> getPowerStationIdByCid(String cid){
        return cidRelationMapper.getPowerStationIdByCid(cid);
    }

    /**
     * 根据cid,vid,loop 获取relation记录
     *
     * @return
     */
    @Override
    public List<CidRelation> getRelationByCidVidLoop(String cid, String vid, String roadType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("roadType",roadType);
        return cidRelationMapper.getRelationByCidVidLoop(paramMap);
    }

    /**
     * 获取网关电站信息  cid_powerstionId
     *
     * @return
     */
    @Override
    public List<CidRelation> getPlantIdByRelationGroup() {
        return cidRelationMapper.getPlantIdByRelationGroup();
    }


    /**
     * 更新cid，vid，loop的日、周、月、年、总发电量
     * @param cidRelation
     * @return
     */
    @Override
    public int updateRelationByCidVidLoop(CidRelation cidRelation){
        return cidRelationMapper.updateRelationByCidVidLoop(cidRelation);
    }

    /**
     * 获取月发电量
     *
     * @return
     */
    @Override
    public List<CidRelation> selectPlantMonthEnergy() {
        return cidRelationMapper.selectPlantMonthEnergy();
    }

    /**
     * 获取年发电量
     *
     * @return
     */
    @Override
    public List<CidRelation> selectPlantYearEnergy() {
        return cidRelationMapper.selectPlantYearEnergy();
    }

    /**
     * 获取总发电量
     *
     * @return
     */
    @Override
    public List<CidRelation> selectPlantCountEnergy() {
        return cidRelationMapper.selectPlantCountEnergy();
    }


    /**
     * 根据网关发送指令
     *
     * @param cid
     * @return
     */
    @Override
    public int updateCidRelationByCammand(String cid,String cammandUpdateMap) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("cammandUpdateMap",cammandUpdateMap);
        return cidRelationMapper.updateCidRelationByCammand(paramMap);
    }





    /**
     * 设备列表tree
     * @param deptId
     * @param cid
     * @return
     */
    @Override
    public List<Map> selectEmuList(Long deptId,Long plantId,String cid) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        paramMap.put("powerStationId",plantId);
        paramMap.put("cid",cid);
        return cidRelationMapper.selectEmuTree(paramMap);
    }

    /**
     * 设备列表tree
     * @param deptId
     * @param cid
     * @return
     */
    @Override
    public List<Map> selectEmuListOnly(Long deptId,Long plantId,String cid) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        paramMap.put("powerStationId",plantId);
        paramMap.put("cid",cid);

        List<Map>  cidRelationList =  cidRelationMapper.selectEmuListOnly(paramMap);
        return cidRelationList;
    }

    @Override
    public List<Map>     selectEmuListOnlyInPlantArr(Long[] plantId,String cid) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("powerStationId",plantId);
        paramMap.put("cid",cid);

        List<Map>  cidRelationList =  cidRelationMapper.selectEmuListOnlyInPlantArr(paramMap);
        return cidRelationList;
    }



    public List<Map> selectEmuListOnly(Long deptId,Long plantId,String cid,String cidType){

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        paramMap.put("powerStationId",plantId);
        paramMap.put("cid",cid);
        paramMap.put("cidType",cidType);

        List<Map>  cidRelationList =  cidRelationMapper.selectEmuListOnly(paramMap);
        return cidRelationList;
    }


    /**
     * 根据电站id与 cid,vid类型查询数量
     *
     * @param powerStationId
     * @param type
     * @return
     */
    @Override
    public List<CidRelation> getAppCidVidCount(Long powerStationId, String type) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("powerStationId",powerStationId);
        paramMap.put("cidType",type);
        return cidRelationMapper.getAppCidVidCount(paramMap);
    }
}
