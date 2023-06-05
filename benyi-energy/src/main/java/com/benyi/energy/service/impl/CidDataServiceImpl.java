package com.benyi.energy.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson2.JSON;
import com.benyi.energy.domain.CidDayData;
import com.benyi.energy.mapper.CidDayDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.CidDataMapper;
import com.benyi.energy.domain.CidData;
import com.benyi.energy.service.ICidDataService;

/**
 * 发电信息Service业务层处理
 *
 * @author wuqiguang
 * @date 2022-07-31
 */
@Slf4j
@Service
public class CidDataServiceImpl implements ICidDataService {

    @Autowired
    private CidDataMapper cidDataMapper;

    @Autowired
    private CidDayDataMapper cidDayDataMapper;


    /**
     * 获取cid的第一条通讯时间
     *
     * @param cid 参数
     * @return 发电信息
     */
    public List<Map>  selectLastUpdateTime(String cid){
        return cidDataMapper.selectLastUpdateTime(cid);
    }


    /**
     * 查询vid和cid查询故障代码和描述
     *
     * @param vid 微逆编号,cid网关编号
     * @return 发电信息
     */
    public List<Map> getErrorCodeAndDescByCidAndVid(String cid,String vid){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("vid", vid);
        paramMap.put("cid",cid);
        return cidDataMapper.getErrorCodeAndDescByCidAndVid(paramMap);
    }


    @Override
    public List<Map> getStationEnergyInPeriodOfTime(Long queryStationId,Long deptId, String searchStartDate, String searchEndDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("deptId",deptId);
        paramMap.put("weekBegin", searchStartDate);
        paramMap.put("weekEnd",searchEndDate);
        return cidDayDataMapper.getStationEnergyInPeriodOfTime(paramMap);

    }

    /**
     * 查询发电信息
     *
     * @param id 发电信息主键
     * @return 发电信息
     */
    @Override
    public CidData selectCidDataById(Long id) {
        return cidDataMapper.selectCidDataById(id);
    }

    /**
     * 查询发电信息列表
     *
     * @param cidData 发电信息
     * @return 发电信息
     */
    @Override
    public List<CidData> selectCidDataList(CidData cidData) {
        return cidDataMapper.selectCidDataList(cidData);
    }

    /**
     * 查询最后电信息
     *
     * @param cidData 发电信息
     * @return 发电信息集合
     */
    @Override
    public List<CidData> selectLastCidList(CidData cidData) {
        return cidDataMapper.selectLastCidList(cidData);
    }

    /**
     * 查询发电故障信息列表
     *
     * @param
     * @return 发电信息集合
     */
    @Override
    public List<CidData> selectCidDataErrorList(Long deptId,String cid,String vid,String searchDate,
                                                String m1MError,String m1MErrorCn,String m1MErrorCode,
                                                String m1SError,String m1SErrorCn,String m1SErrorCode,Long plantId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("searchDate",searchDate);
        paramMap.put("m1MError",m1MError);
        paramMap.put("m1SError",m1SError);
        paramMap.put("m1MErrorCn",m1MErrorCn);
        paramMap.put("m1SErrorCn",m1SErrorCn);
        paramMap.put("m1MErrorCode",m1MErrorCode);
        paramMap.put("m1SErrorCode",m1SErrorCode);
        paramMap.put("powerStationId",plantId);
        return cidDataMapper.selectCidDataErrorList(paramMap);
    }

    /**
     * 根据cids 或者vids 获取对应cid or vid列表
     *
     * @param ids cid or vids
     * @param type 1 cid 2 vid
     * @return 发电信息
     */
    @Override
    public List<CidData> selectCidDataListByCidsOrVids(String[] ids, int type) {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();

        List<CidData> resultList = null;
        if(type==1){
            paramMap.put("cids", ids);
            resultList = cidDataMapper.getListByCids(paramMap);
        }else if(type==2){
            paramMap.put("vids", ids);
            resultList = cidDataMapper.getListByVids(paramMap);
        }
        return resultList;
    }

    /**
     * 新增发电信息
     *
     * @param cidData 发电信息
     * @return 结果
     */
    @Override
    public int insertCidData(CidData cidData) {
        return cidDataMapper.insertCidData(cidData);
    }

    /**
     * 修改发电信息
     *
     * @param cidData 发电信息
     * @return 结果
     */
    @Override
    public int updateCidData(CidData cidData) {
        return cidDataMapper.updateCidData(cidData);
    }

    /**
     * 批量删除发电信息
     *
     * @param ids 需要删除的发电信息主键
     * @return 结果
     */
    @Override
    public int deleteCidDataByIds(Long[] ids) {
        return cidDataMapper.deleteCidDataByIds(ids);
    }

    /**
     * 删除发电信息信息
     *
     * @param id 发电信息主键
     * @return 结果
     */
    @Override
    public int deleteCidDataById(Long id) {
        return cidDataMapper.deleteCidDataById(id);
    }


    /**
     *
     * 获取当日发电信息数据with out order by
     *
     * @param vids 网关
     * @param cids 微逆
     * @return
     */
    @Override
    public List<CidData> getCountDayPowerList(String[] vids, String[] cids) {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put("vids", vids);
        paramMap.put("cids", cids);

        List<CidData> resultList = cidDataMapper.getDayCountPowerInfo(paramMap);

        return resultList;
    }

    /**
     * 获取日发电量信息
     *
     * @param vids 网关
     * @param cids 微逆
     * @return
     */
    @Override
    public Long getCountPowerList(String[] vids, String[] cids) {


        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put("vids", vids);
        paramMap.put("cids", cids);

        Long result = cidDataMapper.getCountPowerInfo(paramMap);

        return result;
    }



    /**
     * 获取日发电量信息
     *
     * @param deptId 机构Id
     * @param searchDate 搜索日期
     * @return
     */
    @Override
    public List<CidData> getCurrentDayPowerList(Long deptId,String searchDate) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate",searchDate) ;
        List<CidData> resultList = cidDataMapper.getDayPowerInfo(paramMap);
        return resultList;
    }

    /**
     * 获取当日发电信息数据
     *
     * @param deptId     网关
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidData> getCurrentDayPowerListForMinute(Long deptId, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate",searchDate) ;
        List<CidData> resultList = cidDataMapper.getDayPowerInfoForMinute(paramMap);
        return resultList;
    }

    /**
     * 获取日发电量信息
     *
     * @param deptId 机构Id
     * @param searchDate 搜索日期
     * @return
     */
    @Override
    public List<CidDayData> getWeekPowerList(Long deptId, String searchDate) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate",searchDate) ;
        List<CidDayData> resultList = cidDayDataMapper.getWeekPowerInfo(paramMap);

        return resultList;
    }

    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 网关
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getMonthPowerList(Long deptId,String searchDate) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate",searchDate);
        List<CidDayData> resultList = cidDayDataMapper.getMonthPowerInfo(paramMap);
        return resultList;
    }

    /**
     * 根据网关 查询月发电量信息
     *
     * @param deptId     企业id
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getMonthEnergyInfo(Long deptId, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate",searchDate);
        List<CidDayData> resultList = cidDayDataMapper.getMonthEnergyInfo(paramMap);
        return resultList;
    }

    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 企业id
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getYearPowerList(Long deptId,String searchDate) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate",searchDate);
        List<CidDayData> resultList = cidDayDataMapper.getYearPowerInfo(paramMap);

        return resultList;
    }

    /**
     * 根据网关 查询年发电量信息
     *
     * @param deptId     企业id
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getYearEnergyInfo(Long deptId, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate",searchDate);
        List<CidDayData> resultList = cidDayDataMapper.getYearEnergyInfo(paramMap);
        return resultList;
    }

    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 企业id
     * @return
     */
    @Override
    public List<CidDayData> getAllPowerList(Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        List<CidDayData> resultList = cidDayDataMapper.getAllPowerInfo(paramMap);

        return resultList;
    }

    /**
     * 根据网关 查询年发电量信息
     *
     * @param deptId
     * @return
     */
    @Override
    public List<CidDayData> getAllEnergyInfo(Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        List<CidDayData> resultList = cidDayDataMapper.getAllEnergyInfo(paramMap);
        return resultList;
    }

    /**
     * 获取cids所有power和
     *
     * @param cids cid集合
     * @return 总power
     */
    public String getCidPowerCount(String[] cids){



        return "";
    }

    @Override
    public List<CidDayData> getCidListByCreateDate(Long deptId) {
        return cidDayDataMapper.getCidCountByDate(deptId);
    }

    /**
     * 获取所有网关列表
     *
     * @return 网关列表
     */
    @Override
    public List<CidData> getCidListByGroup(CidData cidData) {
        return cidDataMapper.getCidListByGroup(cidData);
    }

    @Override
    public List<CidData> getEmuList(Long deptId, String vid, String cid, String queryStationId,
                                    String m1MError,String m1MErrorCode
                                    ,String m1SError,String m1SErrorCode
                                    ,String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("vid", vid);
        paramMap.put("cid", cid);
        paramMap.put("m1MError",m1MError);
        paramMap.put("m1SError",m1SError);
        paramMap.put("m1MErrorCode", m1MErrorCode);
        paramMap.put("m1SErrorCode", m1SErrorCode);
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
       System.out.println("===getEmuList paramMap:"+paramMap);
        return cidDataMapper.getEmuList(paramMap);
    }


    @Override
    public List<CidData> getEmuList(String cidType,Long deptId, String vid, String cid, String queryStationId,
                                    String m1MError,String m1MErrorCode
            ,String m1SError,String m1SErrorCode
            ,String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("vid", vid);
        paramMap.put("cid", cid);
        paramMap.put("cidType", cidType);
        paramMap.put("m1MError",m1MError);
        paramMap.put("m1SError",m1SError);
        paramMap.put("m1MErrorCode", m1MErrorCode);
        paramMap.put("m1SErrorCode", m1SErrorCode);
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
        System.out.println("===getEmuList paramMap:"+paramMap);
        return cidDataMapper.getEmuList(paramMap);
    }


    /**
     * 根据电站ID与查询时间 (周、月、年、总)  Power
     *
     * @param queryStationId 电站ID
     * @param searchDate     查询日期
     * @param searchDateType 查询日期种类
     * @return
     */
    @Override
    public List<CidDayData> getStationPowerByPidAndDate(Long queryStationId,Long deptId, String searchDate, String searchDateType) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
        paramMap.put("deptId",deptId);
        if(searchDateType=="d"||searchDateType.equals("d")){
            return cidDayDataMapper.getStationPowerDayByPidAndDate(paramMap);
        }else if(searchDateType=="w"||searchDateType.equals("w")){
            return cidDayDataMapper.getStationPowerWeekByPidAndDate(paramMap);
        }else if(searchDateType=="m"||searchDateType.equals("m")){
            return cidDayDataMapper.getStationPowerMonthByPidAndDate(paramMap);
        }else if(searchDateType=="y"||searchDateType.equals("y")){
            return cidDayDataMapper.getStationPowerYearByPidAndDate(paramMap);
        }else if(searchDateType=="a"||searchDateType.equals("a")){
            return cidDayDataMapper.getStationPowerAllByPidAndDate(paramMap);
        }

        return null;
    }

    /**
     * 根据电站ID与查询时间 天  Power
     *
     * @param queryStationId 电站ID
     * @param searchDate     查询日期
     * @param searchDateType 查询日期种类
     * @return
     */
    @Override
    public List<CidData> getStationPowerByPidAndDateForDay(Long queryStationId,Long deptId, String searchDate, String searchDateType) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
        paramMap.put("deptId",deptId);

        return cidDataMapper.getStationPowerDayByPidAndDate(paramMap);

    }

    @Override
    public List<CidData> getStationPowerDayByCidAndVidAndPidAndDateForMinute(Long queryStationId, Long deptId, String searchDate, String searchDateType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
        paramMap.put("deptId",deptId);
        System.out.println("===getStationPowerByPidAndDateForDayForMinute:  paramMap"+JSON.toJSONString(paramMap));
        return cidDataMapper.getStationPowerDayByCidAndVidAndPidAndDateForMinute(paramMap);
    }

    /**
     * 根据电站ID与查询时间 (天) 发电量
     *
     * @param queryStationId 电站ID
     * @param deptId         机构Id
     * @param searchDate     查询日期
     * @param searchDateType 查询日期种类
     * @return
     */
    @Override
    public List<CidData> getStationPowerByPidAndDateForDayForMinute(Long queryStationId, Long deptId, String searchDate, String searchDateType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
        paramMap.put("deptId",deptId);
        System.out.println("===getStationPowerByPidAndDateForDayForMinute:  paramMap"+JSON.toJSONString(paramMap));
        return cidDataMapper.getStationPowerDayByPidAndDateForMinute(paramMap);
    }

    /**
     * 根据电站ID与查询时间 (周、月、年、总) Energy
     *
     * @param queryStationId 电站ID
     * @param deptId         机构Id
     * @param searchDate     查询日期
     * @param searchDateType 查询日期种类
     * @return
     */
    @Override
    public List<CidDayData> getStationEnergyByPidAndDate(Long queryStationId, Long deptId, String searchDate, String searchDateType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
        paramMap.put("deptId",deptId);
        if(searchDateType=="d"||searchDateType.equals("d")){
            return cidDayDataMapper.getStationPowerDayByPidAndDate(paramMap);
        }else if(searchDateType=="w"||searchDateType.equals("w")){
            return cidDayDataMapper.getStationEnergyWeekByPidAndDate(paramMap);
        }else if(searchDateType=="m"||searchDateType.equals("m")){
            return cidDayDataMapper.getStationEnergyMonthByPidAndDate(paramMap);
        }else if(searchDateType=="y"||searchDateType.equals("y")){
            return cidDayDataMapper.getStationEnergyYearByPidAndDate(paramMap);
        }else if(searchDateType=="a"||searchDateType.equals("a")){
            return cidDayDataMapper.getStationEnergyAllByPidAndDate(paramMap);
        }else{
            return null;
        }
    }

//    /**
//     * 根据电站ID与查询时间 （天） Energy
//     *
//     * @param queryStationId 电站ID
//     * @param deptId         机构Id
//     * @param searchDate     查询日期
//     * @param searchDateType 查询日期种类
//     * @return
//     */
//    @Override
//    public List<CidData> getStationEnergyByPidAndDateForDay(Long queryStationId, Long deptId, String searchDate, String searchDateType) {
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("queryStationId", queryStationId);
//        paramMap.put("searchDate", searchDate);
//        paramMap.put("deptId",deptId);
//        return cidDataMapper.getStationEnergyDayByPidAndDate(paramMap);
//
//    }

    @Override
    public List<CidData> getStationEnergyByPidAndDateForDay(String cid,String vid,String roadType,String searchDate,String searchDateType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        paramMap.put("searchDate",searchDate);
        return cidDataMapper.getStationEnergyDayByPidAndDate(paramMap);

    }



    /**
     * 根据电站ID与查询时间 (天-小时) 一天发电量
     *
     * @param queryStationId
     * @param deptId
     * @param searchDate
     * @return
     */
    @Override
    public List<CidData> getStationEnergyHourByPidAndDate(Long queryStationId, Long deptId, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queryStationId", queryStationId);
        paramMap.put("searchDate", searchDate);
        paramMap.put("deptId",deptId);
        return cidDataMapper.getStationEnergyHourByPidAndDate(paramMap);
    }

    /**
     * 获取网关列表详情
     *
     * @param deptId
     * @param vid
     * @param cid
     * @param stationId
     * @param searchDate
     * @return
     */
    @Override
    public List<CidData> getCidList(Long deptId, String vid, String cid, String stationId, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("deptId", deptId);
        paramMap.put("vid", vid);
        paramMap.put("cid", cid);
        paramMap.put("queryStationId", stationId);
        paramMap.put("searchDate", searchDate);
        return cidDataMapper.getCidList(paramMap);
    }


    /**
     * 获取App网关列表详情
     *
     * @param deptId
     * @param vid
     * @param cid
     * @param searchDate
     * @return
     */
    @Override
    public List<CidData> getAppCidList(Long deptId, String vid, String cid, String searchDate,Long powerStationId) {

        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("deptId", deptId);
        paramMap.put("vid", vid);
        paramMap.put("cid", cid);
        paramMap.put("powerStationId", powerStationId);
        paramMap.put("searchDate", searchDate);

        return cidDataMapper.getCidAppList(paramMap);
    }

    /**
     * 根据网关 获取 微逆当前发电量信息
     *
     * @param vid 微逆
     * @return
     */
    @Override
    public CidData getEmuCurrentPowerByEmu(String vid) {

        return cidDataMapper.getEmuCurrentPowerByEmu(vid);
    }

    /**
     * 根据网关 获取 微逆总发电量信息
     *
     * @param vid 微逆
     * @return
     */
    @Override
    public CidData getEmuPowerCountByEmu(String vid) {
        return cidDataMapper.getEmuPowerCountByEmu(vid);
    }


    /**
     * 根据网关 查询 网关指定天时间的详情
     *
     * @param cid        网关
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidData> getCidDayDetail(String cid, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("searchDate", searchDate);
        return cidDataMapper.getCidDayDetail(paramMap);
    }

    /**
     * 根据网关 查询 网关指定月时间的详情
     *
     * @param cid        网关
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getCidMonthDetail(String cid, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("searchDate", searchDate);
        return cidDayDataMapper.getCidMonthDetail(paramMap);
    }

    /**
     * 根据网关 查询 网关指定年时间的详情
     *
     * @param cid        网关
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getCidYearDetail(String cid, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("searchDate", searchDate);
        return cidDayDataMapper.getCidYearDetail(paramMap);
    }

    /**
     * 根据网关 查询 网关指定总时间的详情
     *
     * @param cid 网关
     * @return
     */
    @Override
    public List<CidDayData> getCidAllDetail(String cid) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        return cidDayDataMapper.getCidAllDetail(paramMap);
    }

    /**
     * 根据微逆  查询 微逆指定天时间的详情
     *
     * @param vid      微逆
     * @param roadType     路
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidData> getVidsDayDetail(String vid,String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("vid", vid);
        paramMap.put("roadType",roadType);
        paramMap.put("searchDate", searchDate);
        return cidDataMapper.getVidsDayDetail(paramMap);
    }

    /**
     * 根据微逆  查询 微逆指定月时间的详情
     *
     * @param vid      微逆
     * @param roadType     路
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getVidsMonthDetail(String vid,String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("vid", vid);
        paramMap.put("roadType",roadType);
        paramMap.put("searchDate", searchDate);
        return cidDayDataMapper.getVidsMonthDetail(paramMap);
    }

    /**
     * 根据微逆  查询 微逆指定年时间的详情
     *
     * @param vid      微逆
     * @param roadType     路
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getVidsYearDetail(String vid,String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("vid", vid);
        paramMap.put("roadType",roadType);
        paramMap.put("searchDate", searchDate);
        return cidDayDataMapper.getVidsYearDetail(paramMap);
    }

    /**
     * 根据微逆  查询 微逆指定总时间的详情
     *
     * @param vid      微逆
     * @param roadType     路
     * @return
     */
    @Override
    public List<CidDayData> getVidsAllDetail(String vid,String roadType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("vid", vid);
        paramMap.put("roadType",roadType);
        return cidDayDataMapper.getVidsAllDetail(paramMap);
    }




    /**
     * 根据网关 查询 网关指定周时间的详情
     *
     * @param cid        网关
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getCidWeekDetail(String cid, String searchDate) {

        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("cid", cid);
        paramMap.put("searchDate", searchDate);
        return cidDayDataMapper.getCidWeekDetail(paramMap);
    }

    /**
     * 根据微逆  查询 微逆指定周时间的详情
     *
     * @param vid      微逆
     * @param roadType     路
     * @param searchDate 查询日期
     * @return
     */
    @Override
    public List<CidDayData> getVidsWeekDetail(String vid,String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("vid", vid);
        paramMap.put("roadType",roadType);
        paramMap.put("searchDate", searchDate);
        return cidDayDataMapper.getVidsWeekDetail(paramMap);
    }

    /**
     * 根据deptId与日期查询当日功率
     *
     * @param deptId
     * @param searchDate
     * @return
     */
    @Override
    public List<CidData> selectEnergyByDate(Long deptId, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("searchDate", searchDate);
        return cidDataMapper.selectEnergyByDate(paramMap);
    }


    /**
     * 根据网关、微逆、起始日期、结束日期查询发电信息详情
     *
     * @param cid             网关
     * @param vid             微逆
     * @param searchDateStart 起始日期
     * @param searchDateEnd   截止日期
     * @return
     */
    @Override
    public List<CidDayData> selectDetailByCidVidAndDate(String cid, String vid,String roadType, String searchDateStart, String searchDateEnd,String searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        paramMap.put("searchDateStart", searchDateStart);
        paramMap.put("searchDateEnd", searchDateEnd);
        paramMap.put("searchType", searchType);
        return cidDayDataMapper.selectDetailByCidVidAndDate(paramMap);
    }




    /**
     * 根据网关、微逆、起始日期、结束日期查询发电信息详情
     *
     * @param cid             网关
     * @param vid             微逆
     * @param searchDateStart 起始日期
     * @param searchDateEnd   截止日期
     * @param searchType      日期类型
     * @return
     */
    @Override
    public List<CidData> selectDetailByCidVidAndDateForDay(String cid, String vid,String roadType, String searchDateStart, String searchDateEnd, String searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        paramMap.put("searchDateStart", searchDateStart);
        paramMap.put("searchDateEnd", searchDateEnd);
        paramMap.put("searchType", searchType);

        return  cidDataMapper.selectDetailByCidVidAndDate(paramMap);


    }

    @Override
    public List<CidData> selectDetailByCidVidAndDateForDay(String plantID, String cid, String vid,String roadType, String searchDateStart, String searchDateEnd, String searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        paramMap.put("searchDateStart", searchDateStart);
        paramMap.put("searchDateEnd", searchDateEnd);
        paramMap.put("plantID", plantID);
        paramMap.put("searchType", searchType);

        if (roadType==null){
            paramMap.put("cidOrEmu", "5");
        }


        log.info("selectDetailByCidVidAndDate paramMap:"+paramMap);
        return  cidDataMapper.selectDetailByCidVidAndDate(paramMap);


    }

    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     *  （周、月、年、总）
     *
     * @param cid             网关
     * @param vid             微逆
     * @param loop            路
     * @param searchDateStart 起始日期
     * @param searchDateEnd   截止日期
     * @param searchType      日期类型
     * @return
     */
    @Override
    public List<CidDayData> selectMisDetailByDateType(String cid, String vid, String loop, String searchDateStart, String searchDateEnd, String searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("loop",loop);
        System.out.println("===selectMisDetailByDateType loop: "+loop);

        if(searchType.equals("m")||searchType=="m"){
            paramMap.put("searchDate",searchDateStart);
            return cidDayDataMapper.selectMisDetailVoltByMonth(paramMap);
        }
        else if(searchType.equals("y")||searchType=="y"){
            paramMap.put("searchDate",searchDateStart);
            return cidDayDataMapper.selectMisDetailVoltByYear(paramMap);
        }else{
            return null;
        }
    }

    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     *  （天）
     *
     * @param cid             网关
     * @param vid             微逆
     * @param loop            路
     * @param searchDateStart 起始日期
     * @param searchDateEnd   截止日期
     * @param searchType      日期类型
     * @return
     */
    @Override
    public List<CidData> selectMisDetailByDateTypeForDay(String cid, String vid, String loop, String searchDateStart, String searchDateEnd, String searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("loop",loop);
        paramMap.put("searchDateStart", searchDateStart);
        paramMap.put("searchDateEnd", searchDateEnd);
        return cidDataMapper.selectMisDetailVoltByDay(paramMap);

    }

    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     * (天)
     *
     * @param cid        网关
     * @param vid        微逆
     * @param loop       路
     * @param searchDate 日期
     * @return
     */
    @Override
    public List<CidData> selectMisDetailByDateTypeForDayNew(String cid, String vid, String loop, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("loop",loop);
        paramMap.put("searchDate", searchDate);
        return cidDataMapper.selectMisDetailVoltByDayNew(paramMap);
    }

    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     * (天) 5分钟sql语句
     *
     * @param cid        网关
     * @param vid        微逆
     * @param loop       路
     * @param searchDate 日期
     * @return
     */
    @Override
    public List<Map> selectMisDetailByDateTypeForDayNewForMinute(Long plantID,String cid, String vid, String loop, String searchDate) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("plantID", plantID);
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("loop",loop);
        paramMap.put("searchDate", searchDate);

        System.out.println("===selectMisDetailByDateTypeForDayNewForMinute  paramMap:"+JSON.toJSONString(paramMap));
        List<Map> resultList = cidDataMapper.selectMisDetailVoltByDayNewForMinute(paramMap);

        if (loop==null){

            Map<String,Map> cidDataMap =new TreeMap<>();
            for (Map itemMap:resultList){
                System.out.println("===  itemMap:"+JSON.toJSONString(itemMap));
                String key=itemMap.get("createDate").toString();
                if (cidDataMap.get(key)==null){
                    cidDataMap.put(key,itemMap);
                }else {
                    Map cidDataMapExist = cidDataMap.get(key);
                    cidDataMapExist.put("power",String.valueOf(Float.parseFloat(cidDataMapExist.get("power").toString())+Float.parseFloat(itemMap.get("power").toString())) );
                    cidDataMap.put(key,cidDataMapExist);
                }

            }

            List<Map> resultNewList = new ArrayList<>();
            Iterator<Map.Entry<String, Map>> iterator = cidDataMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map> entry = iterator.next();
                resultNewList.add(entry.getValue());
            }
            System.out.println("===  resultNewList:"+JSON.toJSONString(resultNewList));

            return resultNewList;
        }else {
            return  resultList;
        }
    }

    /**
     * 根据网关、微逆 查询 Loop
     *
     * @param cid 网关
     * @param vid 微逆
     * @return
     */
    @Override
    public List<String> selectMisDetailLoop(String cid, String vid) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        return cidDataMapper.selectMisDetailLoop(paramMap);
    }


    /**
     * 根据 网关或者微逆 查询数据 日
     *
     * @param cid        网关
     * @param vid        微逆
     * @param loop       路
     * @param searchDate 日期
     * @param searchType 日期类型
     * @return
     */
    @Override
    public List<CidData> selectMisDetailBySearchDateForDay(String cid, String vid, String loop, String searchDate, String searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("loop",loop);
        paramMap.put("searchDate",searchDate);
        if(searchType.equals("d")||searchType=="d"){
            return cidDataMapper.selectMisDetailByDayAndSearchDate(paramMap);
        }else{
            return null;
        }
    }

    /**
     * 根据 网关或者微逆 查询数据 月、年
     *
     * @param cid        网关
     * @param vid        微逆
     * @param loop       路
     * @param searchDate 日期
     * @param searchType 日期类型
     * @return
     */
    @Override
    public List<CidDayData> selectMisDetailBySearchDate(String cid, String vid, String loop, String searchDate, String searchType) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("loop",loop);
        paramMap.put("searchDate",searchDate);

        System.out.println("===selectMisDetailBySearchDate paramMap:"+paramMap);

        if(searchType.equals("m")||searchType=="m"){
            List<CidDayData> resultList = cidDayDataMapper.selectMisDetailByMonthAndSearchDate(paramMap);
            System.out.println("===selectMisDetailBySearchDate month resultList:"+JSON.toJSONString(resultList));
            return resultList;
        }
        else if(searchType.equals("y")||searchType=="y"){
            return cidDayDataMapper.selectMisDetailByYearAndSearchDate(paramMap);
        }else if(searchType.equals("w")||searchType=="w"){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
            Date parseDate =new Date();
            if (searchDate!=null){
                try{
                    parseDate = sdf.parse(searchDate);
                }catch (Exception ex){
                    ex.printStackTrace();
                    parseDate=new Date();
                }
            }else{
                searchDate=sdf.format(parseDate);
            }


            Calendar cal = Calendar.getInstance();
            cal.setTime(parseDate);
            // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }

            cal.setFirstDayOfWeek(Calendar.SUNDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            Date weekBeginDate =cal.getTime();
            String weekBegin = sdf.format(weekBeginDate);

            cal.add(Calendar.DATE, 6);
            Date weekEndDate =cal.getTime();
            String weekEnd = sdf.format(weekEndDate);
            paramMap.put("weekBegin",weekBegin);
            paramMap.put("weekEnd",weekEnd);
            System.out.println("===selectMisDetailByYearAndInPeriodOfTime weekreturnList paramMap:"+ JSON.toJSONString(paramMap));
            List<CidDayData> returnList = cidDayDataMapper.selectMisDetailByYearAndInPeriodOfTime(paramMap);


            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("MM-dd");

            HashMap initcidDayDataMap=new HashMap();
            initcidDayDataMap.put("cid",cid);
            initcidDayDataMap.put("vid",vid);
            initcidDayDataMap.put("loop",vid.substring(0,1));
            initcidDayDataMap.put("power",0);
            initcidDayDataMap.put("energy",0);


            Map<String,Object> weekMap=new HashMap();
            Calendar c = Calendar.getInstance();
            System.out.println("parseDate："+parseDate);

            c.setTime(parseDate);
            dayWeek = c.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
            c.setFirstDayOfWeek(Calendar.SUNDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            day = c.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
            c.add(Calendar.DATE, c.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

            System.out.println("weekBegin："+weekBegin);


            initcidDayDataMap.put("createDate",simpleDateFormat.format(c.getTime()));
            weekMap.put(simpleDateFormat.format(c.getTime()),initcidDayDataMap.clone());
            System.out.println(simpleDateFormat.format(c.getTime()));


            c.add(Calendar.DAY_OF_MONTH, 1);
            initcidDayDataMap.put("createDate",simpleDateFormat.format(c.getTime()));
            weekMap.put(simpleDateFormat.format(c.getTime()),initcidDayDataMap.clone());
            System.out.println(simpleDateFormat.format(c.getTime()));


            c.add(Calendar.DAY_OF_MONTH, 1);
            initcidDayDataMap.put("createDate",simpleDateFormat.format(c.getTime()));
            weekMap.put(simpleDateFormat.format(c.getTime()),initcidDayDataMap.clone());
            System.out.println(simpleDateFormat.format(c.getTime()));


            c.add(Calendar.DAY_OF_MONTH, 1);
            initcidDayDataMap.put("createDate",simpleDateFormat.format(c.getTime()));
            weekMap.put(simpleDateFormat.format(c.getTime()),initcidDayDataMap.clone());
            System.out.println(simpleDateFormat.format(c.getTime()));


            c.add(Calendar.DAY_OF_MONTH, 1);
            initcidDayDataMap.put("createDate",simpleDateFormat.format(c.getTime()));
            weekMap.put(simpleDateFormat.format(c.getTime()),initcidDayDataMap.clone());

            System.out.println(simpleDateFormat.format(c.getTime()));

            c.add(Calendar.DAY_OF_MONTH, 1);
            initcidDayDataMap.put("createDate",simpleDateFormat.format(c.getTime()));
            weekMap.put(simpleDateFormat.format(c.getTime()),initcidDayDataMap.clone());

            System.out.println(simpleDateFormat.format(c.getTime()));

            c.add(Calendar.DAY_OF_MONTH, 1);
            initcidDayDataMap.put("createDate",simpleDateFormat.format(c.getTime()));
            weekMap.put(simpleDateFormat.format(c.getTime()),initcidDayDataMap.clone());
            System.out.println(simpleDateFormat.format(c.getTime()));

            System.out.println("weekMap:"+JSON.toJSONString(weekMap));

            for (int i=0;i<returnList.size();i++){
                Map cidDayData =JSON.parseObject(JSON.toJSONString(returnList.get(i)),Map.class);
                if (weekMap.get(cidDayData.get("createDate"))!=null){
                    HashMap mapItem =(HashMap)weekMap.get(cidDayData.get("createDate"));
                    cidDayData.put(cidDayData.get("createDate"),Float.parseFloat(cidDayData.get("energy").toString())+Float.parseFloat(mapItem.get("energy").toString()));
                }
                weekMap.put(cidDayData.get("createDate").toString(),cidDayData);
            }
            System.out.println("weekMap222:"+JSON.toJSONString(weekMap));

            List<CidDayData> returnNewList = new ArrayList<CidDayData>();
            for (String key : weekMap.keySet()) {
                HashMap map =(HashMap)weekMap.get(key);
                CidDayData cidDayData =new CidDayData();
                cidDayData.setCid(map.get("cid").toString());
                cidDayData.setVid(map.get("vid").toString());
                cidDayData.setRoadType(map.get("loop").toString());
                cidDayData.setPower(map.get("power").toString());
                cidDayData.setEnergy(map.get("energy").toString());
                try {
                    cidDayData.setCreateDate(simpleDateFormat.parse(map.get("createDate").toString()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                returnNewList.add(cidDayData);
            }


            return returnNewList;
        }else{
            return null;
        }
    }


    /**
     * 获取今天所有微逆发电量最后一条记录
     *
     * ids cid_data---id
     * @return
     */
    @Override
    public List<CidData> selectTodayLastData(Long[] ids) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ids",ids);
        return cidDataMapper.selectTodayLastData(paramMap);
    }

    /**
     * 获取今天所有微逆发电量最后一条记录 Ids
     *
     * @param startDate 今天 yyyy-mm-dd 00:00:00
     * @param endDate   今天 yyyy-mm-dd 23:59:59
     * @return
     */
    @Override
    public List<CidData> selectTodayLastDataGetMaxId(String startDate, String endDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        return cidDataMapper.selectTodayLastDataGetMaxId(paramMap);
    }

    /**
     * 获获取今天所有微逆发电量第一条记录
     *
     * @param startDate 今天 yyyy-mm-dd 00:00:00
     * @param endDate   今天 yyyy-mm-dd 23:59:59
     * @return
     */
    @Override
    public List<CidData> selectTodayFirstData(String startDate, String endDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        return cidDataMapper.selectTodayFirstData(paramMap);
    }

    /**
     * 获取昨天所有微逆发电量最后一条记录
     * @return
     */
    @Override
    public List<CidData> selectLastDayLastData(Long[] ids) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ids",ids);

        return cidDataMapper.selectLastDayLastData(paramMap);
    }

    /**
     * 获取昨天所有微逆发电量最后一条记录 ids
     *
     * @return ids
     */
    @Override
    public List<CidData> selectLastDayLastDataGetMaxId() {
        return cidDataMapper.selectLastDayLastDataGetMaxId();
    }

    /**
     * 获取电站当前发电功率
     * @return
     */
    @Override
    public List<CidData> selectCurrentPower(Long[] ids){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ids",ids);
        return cidDataMapper.selectCurrentPower(paramMap);
    }

    /**
     * 获取今日发电量信息
     *
     * @return
     */
    @Override
    public List<CidData> selectLastEnergyDataByToday() {
        return cidDataMapper.selectLastEnergyDataByToday();
    }


    /**
     * 自动绑定关系时查询所有发电信息 by group
     * @return
     */
    @Override
    public List<CidData> selectCidVidLoopForRelation(){
        return cidDataMapper.selectCidVidLoopForRelation();
    }

    /**
     * 获取最后一次发电时间
     * @return
     */
    @Override
    public List<CidData> selectCidVidLoopDateForStatus(){
        return cidDataMapper.selectCidVidLoopDateForStatus();
    }


    /**
     * 获取电站当前发电功率
     * @return
     */
    @Override
    public List<CidData> selectCurrentPowerGetMaxId(){
        return cidDataMapper.selectCurrentPowerGetMaxId();
    }

    /**
     * 获取电站最后发电的记录id
     * @param powerStationId
     * @return
     */
    @Override
    public List<CidData> selectPlantCurrentPowerId(Long powerStationId){
        return cidDataMapper.selectPlantCurrentPowerId(powerStationId);
    }

    /**
     * 获取电站最后发电的记录id
     * @param powerStationId
     * @return
     */
    @Override
    public List<CidData> selectPlantMinId(Long powerStationId){
        return cidDataMapper.selectPlantMinId(powerStationId);
    }

    /**
     * 获取电站当前发电功率
     * @param ids
     * @return
     */
    @Override
    public List<CidData> selectPlantCurrentPowerByIds(Long[] ids){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ids",ids);
        return cidDataMapper.selectPlantCurrentPowerByIds(paramMap);
    }

    /**
     * 获取某天所有发电的网关、微逆信息
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<CidData> selectTaskCidVidLoop(String startDate, String endDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        return cidDataMapper.selectTaskCidVidLoop(paramMap);
    }

    /**
     * 获取日期内最大发电记录
     * @param
     * @return
     */
    @Override
    public Long selectTaskMaxIds(String cid,String vid,String roadType,String startDate,String endDate){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        System.out.println("===CidEnergyTask  analyseCurrentDayEnergy start,selectTaskMaxIds paramMap:"+JSON.toJSONString(paramMap));

        return cidDataMapper.selectTaskMaxIds(paramMap);
    }

    /**
     * 获取指定日期内最大发电记录
     *
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    @Override
    public Long selectTaskMaxIdsByLastDate(String cid, String vid, String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        System.out.println("service impl:"+searchDate);
        paramMap.put("searchDate", searchDate);
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        return cidDataMapper.selectTaskMaxIdsByLastDate(paramMap);
    }

    /**
     * 获取日期内最小发电记录
     * @param
     * @return
     */
    @Override
    public Long selectTaskMinIds(String cid,String vid,String roadType,String startDate,String endDate){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        return cidDataMapper.selectTaskMinIds(paramMap);
    }


    /**
     * 获取Tree cid最后记录id
     *
     * @param cid
     * @return
     */
    @Override
    public List<Long> selectEmuTreeEmuIds(String cid) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        return cidDataMapper.selectEmuTreeEmuIds(paramMap);
    }

    /**
     * 获取Tree vid最后记录id
     *
     * @param cid
     * @param vid
     * @param roadType
     * @return
     */
    @Override
    public Long selectEmuTreeMisId(String cid, String vid, String roadType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType", roadType);
        return cidDataMapper.selectEmuTreeMisId(paramMap);
    }

    /**
     * 根据电站id 获取当天发电量最后ids
     *
     * @param powerStationId
     * @return
     */
    @Override
    public List<CidData> selectPlantCurrentPowerIdNew(Long powerStationId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("powerStationId", powerStationId);
        return cidDataMapper.selectPlantCurrentPowerIdNew(paramMap);
    }

    /**
     * 根据电站id 获取最后一条发电记录
     *
     * @param powerStationId
     * @return
     */
    @Override
    public Long selectPlantLastUpdateId(Long powerStationId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("powerStationId", powerStationId);
        return cidDataMapper.selectPlantLastUpdateId(paramMap);
    }

    /**
     * 获取每小时的所有发电记录
     *
     * @param powerStationId
     * @return
     */
    @Override
    public List<CidData> selectPlantEnergyByHour(Long powerStationId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("powerStationId", powerStationId);
        return cidDataMapper.selectPlantEnergyByHour(paramMap);
    }

    /**
     * 根据电站cid vid loop 获取最后一条发电记录
     *
     * @param cid
     * @param vid
     * @param roadType
     * @return
     */
    @Override
    public Long getMaxDataByCidVidLoop(String cid, String vid, String roadType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("roadType",roadType);
        return cidDataMapper.getMaxDataByCidVidLoop(paramMap);
    }

    /**
     * 获取当前登陆用户所属机构的发电量
     *
     * @param deptId
     * @return
     */
    @Override
    public String getSumEnergyByLoginUser(Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        return cidDataMapper.getSumEnergyByLoginUser(paramMap);
    }

    /**
     * 获取当前登陆用户所属机构的发电量
     *
     * @param deptId
     * @return
     */
    @Override
    public String getSumTodayEnergyByLoginUser(Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        return cidDataMapper.getSumTodayEnergyByLoginUser(paramMap);
    }

    /**
     * 获取cid，vid，road 日期的发电量
     *
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    @Override
    public List<CidData> getStationEnergyDayByCidVidAndDate(String plantID,String cid, String vid, String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("plantID",plantID);
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("roadType",roadType);
        paramMap.put("searchDate",searchDate);
        System.out.println("===getStationEnergyDayByCidVidAndDate roadType: "+roadType);
        return cidDataMapper.getStationEnergyDayByCidVidAndDate(paramMap);

    }

    /**
     * lof app 8小时内发电功率 20分钟一条
     *
     * @param searchDate 8小时前的时间
     * @param deptId
     * @return
     */
    @Override
    public List<CidData> selectLofPower(String searchDate, Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("searchDate",searchDate);
        paramMap.put("deptId",deptId);
        return cidDataMapper.selectLofPower(paramMap);
    }

    /**
     * app 获取发电功率
     *
     * @param deptId
     * @return
     */
    @Override
    public List<CidData> selectPowerForAppIndex(Long deptId) {
        return cidDataMapper.selectPowerForAppIndex(deptId);
    }

    public static void main(String[] args) {

        HashMap weekMap=new HashMap();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 1);
        System.out.println(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        System.out.println(c.getTime());
    }
}
