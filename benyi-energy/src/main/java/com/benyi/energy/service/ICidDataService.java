package com.benyi.energy.service;

import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidData;
import com.benyi.energy.domain.CidDayData;
import org.apache.ibatis.annotations.MapKey;

/**
 * 发电信息Service接口
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
public interface ICidDataService 
{

    /**
     * 获取cid的第一条通讯时间
     *
     * @param cid 参数
     * @return 发电信息
     */
    public List<Map>  selectLastUpdateTime(String cid);

    /**
     * 查询vid和cid查询故障代码和描述
     *
     * @param vid 微逆编号,cid网关编号
     * @return 发电信息
     */
    public List<Map> getErrorCodeAndDescByCidAndVid(String cid,String vid);

    /**
     * 查询发电信息,根据起始时间
     *
     * @param queryStationId 电站
     * @return 发电信息
     */
    public List<Map> getStationEnergyInPeriodOfTime(Long queryStationId,Long deptId, String searchStartDate, String searchEndDate);

    /**
     * 查询发电信息
     * 
     * @param id 发电信息主键
     * @return 发电信息
     */
    public CidData selectCidDataById(Long id);

    /**
     * 查询发电信息列表
     * 
     * @param cidData 发电信息
     * @return 发电信息集合
     */
    public List<CidData> selectCidDataList(CidData cidData);

    /**
     * 查询最后电信息
     *
     * @param cidData 发电信息
     * @return 发电信息集合
     */
    public List<CidData> selectLastCidList(CidData cidData);

    /**
     * 查询发电故障信息列表
     *
     * @param
     * @return 发电信息集合
     */
    public List<CidData> selectCidDataErrorList(Long deptId,String cid,String vid,String searchDate,
                                                String m1MError,String m1MErrorCn,String m1MErrorCode
                                                ,String m1SError,String m1SErrorCn,String m1SErrorCode,Long plantId);


    /**
     * 查询发电信息列表
     *
     * @param ids cid or vids
     * @param type 1 cid 2 vid
     * @return 发电信息集合
     */
    public List<CidData> selectCidDataListByCidsOrVids(String[] ids,int type);

    /**
     * 新增发电信息
     * 
     * @param cidData 发电信息
     * @return 结果
     */
    public int insertCidData(CidData cidData);

    /**
     * 修改发电信息
     * 
     * @param cidData 发电信息
     * @return 结果
     */
    public int updateCidData(CidData cidData);

    /**
     * 批量删除发电信息
     * 
     * @param ids 需要删除的发电信息主键集合
     * @return 结果
     */
    public int deleteCidDataByIds(Long[] ids);

    /**
     * 删除发电信息信息
     * 
     * @param id 发电信息主键
     * @return 结果
     */
    public int deleteCidDataById(Long id);

    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 网关
     * @param searchDate 查询日期
     * @return
     */
    public List<CidData> getCurrentDayPowerList(Long deptId,String searchDate);

    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 网关
     * @param searchDate 查询日期
     * @return
     */
    public List<CidData> getCurrentDayPowerListForMinute(Long deptId,String searchDate);

    /**
     *
     * 获取当日发电信息数据with out order by
     *
     * @param vids 网关
     * @param cids 微逆
     * @return
     */
    public List<CidData> getCountDayPowerList(String[] vids,String[] cids);


    /**
     *
     * 获取总发电信息数据
     *
     * @param vids 网关
     * @param cids 微逆
     * @return
     */
    public Long getCountPowerList(String[] vids,String[] cids);

    /**
     *
     * 获取周发电量信息
     *
     * @param deptId 企业id
     * @param searchDate 搜索日期
     * @return
     */
    public List<CidDayData> getWeekPowerList(Long deptId, String searchDate);


    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 企业id
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getMonthPowerList(Long deptId,String searchDate);

    /**
     * 根据网关 查询月发电量信息
     *
     * @param deptId 企业id
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getMonthEnergyInfo(Long deptId,String searchDate);

    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 企业id
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getYearPowerList(Long deptId,String searchDate);

    /**
     * 根据网关 查询年发电量信息
     *
     * @param deptId 企业id
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getYearEnergyInfo(Long deptId,String searchDate);

    /**
     *
     * 获取当日发电信息数据
     *
     * @param deptId 企业id
     * @return
     */
    public List<CidDayData> getAllPowerList(Long deptId);

    /**
     * 根据网关 查询年发电量信息
     *
     * @param deptId
     * @return
     */
    public List<CidDayData> getAllEnergyInfo(Long deptId);

    /**
     * 按日期获取创建的网关数量
     *
     * @return
     */
    public List<CidDayData> getCidListByCreateDate(Long dept);


    /**
     * 获取cids所有power和
     *
     * @param cids cid集合
     * @return 总power
     */
    public String getCidPowerCount(String[] cids);

    /**
     * 获取所有网关列表
     *
     * @return 网关列表
     */
    public List<CidData> getCidListByGroup(CidData cidData);


    /**
     * 获取微逆详情列表
     * @return
     */
    public List<CidData> getEmuList(Long deptId,String vid,String cid,String stationId
            ,String m1MError,String m1MErrorCode
            ,String m1SError,String m1SErrorCode
            ,String searchDate);

    /**
     * 获取微逆详情列表
     * @return
     */
    public List<CidData> getEmuList(String cidType,Long deptId,String vid,String cid,String stationId
            ,String m1MError,String m1MErrorCode
            ,String m1SError,String m1SErrorCode
            ,String searchDate);



    /**
     * 获取网关列表详情
     * @param
     * @return
     */
    public List<CidData> getCidList(Long deptId,String vid,String cid,String stationId,String searchDate);


    /**
     * 获取网关列表详情
     * @param
     * @return
     */
    public List<CidData> getAppCidList(Long deptId,String vid,String cid,String searchDate,Long powerStationId);


    /**
     * 根据电站ID与查询时间 (天) 发电量
     * @param queryStationId 电站ID
     * @param searchDate 查询日期
     * @param searchDateType 查询日期种类
     * @param deptId 机构Id
     * @return
     */
    public List<CidData> getStationPowerByPidAndDateForDay(Long queryStationId,Long deptId,String searchDate,String searchDateType);

    /**
     * 根据电站ID与查询时间 (天) 发电量
     * @param queryStationId 电站ID
     * @param searchDate 查询日期
     * @param searchDateType 查询日期种类
     * @param deptId 机构Id
     * @return
     */
    public List<CidData> getStationPowerDayByCidAndVidAndPidAndDateForMinute(Long queryStationId,Long deptId,String searchDate,String searchDateType);

    /**
     * 根据电站ID与查询时间 (天) 发电量
     * @param queryStationId 电站ID
     * @param searchDate 查询日期
     * @param searchDateType 查询日期种类
     * @param deptId 机构Id
     * @return
     */
    public List<CidData> getStationPowerByPidAndDateForDayForMinute(Long queryStationId,Long deptId,String searchDate,String searchDateType);


    /**
     * 根据电站ID与查询时间  (周、月、年、总) 一月发电量
     * @param queryStationId 电站ID
     * @param searchDate 查询日期
     * @param searchDateType 查询日期种类
     * @param deptId 机构Id
     * @return
     */
    public List<CidDayData> getStationPowerByPidAndDate(Long queryStationId,Long deptId,String searchDate,String searchDateType);

    /**
     * 根据电站ID与查询时间  (周、月、年、总)  一月发电量
     * @param queryStationId 电站ID
     * @param searchDate 查询日期
     * @param searchDateType 查询日期种类
     * @param deptId 机构Id
     * @return
     */
    public List<CidDayData> getStationEnergyByPidAndDate(Long queryStationId,Long deptId,String searchDate,String searchDateType);

    /**
     * 根据电站ID与查询时间 (天-小时) 一天发电量
     * @param queryStationId
     * @param deptId
     * @param searchDate
     * @return
     */
    public List<CidData> getStationEnergyHourByPidAndDate(Long queryStationId,Long deptId,String searchDate);

    /**
     * 根据电站ID与查询时间 (天) 一月发电量
     * @param searchDate 查询日期
     * @param searchDateType 查询日期种类
     * @return
     */
//    public List<CidData> getStationEnergyByPidAndDateForDay(Long queryStationId,Long deptId,String searchDate,String searchDateType);
    public List<CidData> getStationEnergyByPidAndDateForDay(String cid,String vid,String roadType,String searchDate,String searchDateType);

    /**
     * 根据网关 获取 微逆当前发电量信息
     * @param vid 微逆
     * @return
     */
    public CidData getEmuCurrentPowerByEmu(String vid);

    /**
     * 根据网关 获取 微逆总发电量信息
     * @param vid 微逆
     * @return
     */
    public CidData getEmuPowerCountByEmu(String vid);



    /**
     * 根据网关 查询 网关指定天时间的详情
     * @param cid 网关
     * @param searchDate 查询日期
     * @return
     */
    public List<CidData> getCidDayDetail(String cid,String searchDate);

    /**
     * 根据微逆  查询 微逆指定天时间的详情
     * @param vid 微逆
     * @param searchDate 查询日期
     * @return
     */
    public List<CidData> getVidsDayDetail(String vid,String roadType,String searchDate);

    /**
     * 根据网关 查询 网关指定周时间的详情
     * @param cid 网关
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getCidWeekDetail(String cid,String searchDate);

    /**
     * 根据网关 查询 网关指定月时间的详情
     * @param cid 网关
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getCidMonthDetail(String cid,String searchDate);

    /**
     * 根据网关 查询 网关指定年时间的详情
     * @param cid 网关
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getCidYearDetail(String cid,String searchDate);

    /**
     * 根据网关 查询 网关指定总时间的详情
     * @param cid 网关
     * @return
     */
    public List<CidDayData> getCidAllDetail(String cid);

    /**
     * 根据微逆  查询 微逆指定周时间的详情
     * @param vid 微逆
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getVidsWeekDetail(String vid,String roadType,String searchDate);


    /**
     * 根据微逆  查询 微逆指定月时间的详情
     * @param vid 微逆
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getVidsMonthDetail(String vid,String roadType, String searchDate);

    /**
     * 根据微逆  查询 微逆指定年时间的详情
     * @param vid 微逆
     * @param searchDate 查询日期
     * @return
     */
    public List<CidDayData> getVidsYearDetail(String vid,String roadType, String searchDate);

    /**
     * 根据微逆  查询 微逆指定总时间的详情
     * @param vid 微逆
     * @return
     */
    public List<CidDayData> getVidsAllDetail(String vid,String roadType);

    /**
     * 根据deptId与日期查询当日功率
     * @param deptId
     * @param searchDate
     * @return
     */
    public List<CidData> selectEnergyByDate(Long deptId,String searchDate);



    /**
     * 根据网关、微逆、起始日期、结束日期查询发电信息详情
     * @param cid 网关
     * @param vid 微逆
     * @param searchDateStart 起始日期
     * @param searchDateEnd 截止日期
     * @param searchType 日期类型
     * @return
     */
    public List<CidDayData> selectDetailByCidVidAndDate(String cid,String vid,String roadType,String searchDateStart,String searchDateEnd,String searchType);

    /**
     * 根据网关、微逆、起始日期、结束日期查询发电信息详情
     * @param cid 网关
     * @param vid 微逆
     * @param searchDateStart 起始日期
     * @param searchDateEnd 截止日期
     * @param searchType 日期类型
     * @return
     */
    public List<CidData> selectDetailByCidVidAndDateForDay(String cid,String vid,String roadType,String searchDateStart,String searchDateEnd,String searchType);

    public List<CidData> selectDetailByCidVidAndDateForDay(String plantID, String cid, String vid,String roadType, String searchDateStart, String searchDateEnd, String searchType) ;


    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     * (周、月、年、总)
     * @param cid 网关
     * @param vid 微逆
     * @param loop 路
     * @param searchDateStart 起始日期
     * @param searchDateEnd 截止日期
     * @param searchType 日期类型
     * @return
     */
    public List<CidDayData> selectMisDetailByDateType(String cid,String vid, String loop,String searchDateStart,String searchDateEnd,String searchType);

    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     * (天)
     * @param cid 网关
     * @param vid 微逆
     * @param loop 路
     * @param searchDateStart 起始日期
     * @param searchDateEnd 截止日期
     * @param searchType 日期类型
     * @return
     */
    public List<CidData> selectMisDetailByDateTypeForDay(String cid,String vid, String loop,String searchDateStart,String searchDateEnd,String searchType);

    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     * (天)
     * @param cid 网关
     * @param vid 微逆
     * @param loop 路
     * @param searchDate 日期
     * @return
     */
    public List<CidData> selectMisDetailByDateTypeForDayNew(String cid,String vid, String loop,String searchDate);

    /**
     * 根据网关、微逆、日期 查询 volt 电池板电压 power 发电功率 energy 发电量 temp 温度 grid_volt 并网电压 grid_freq 并网频率
     * (天) 5分钟sql语句
     * @param cid 网关
     * @param vid 微逆
     * @param loop 路
     * @param searchDate 日期
     * @return
     * @return
     */
    public List<Map> selectMisDetailByDateTypeForDayNewForMinute(Long plantID,String cid,String vid, String loop,String searchDate);

    /**
     * 根据网关、微逆 查询 Loop
     * @param cid 网关
     * @param vid 微逆
     * @return
     */
    public List<String> selectMisDetailLoop(String cid,String vid);


    /**
     * 根据 网关或者微逆 查询数据 日
     * @param cid 网关
     * @param vid 微逆
     * @param loop 路
     * @param searchDate 日期
     * @param searchType 日期类型
     * @return
     */
    public List<CidData> selectMisDetailBySearchDateForDay(String cid,String vid,String loop,String searchDate,String searchType);
    /**
     * 根据 网关或者微逆 查询数据 月、年
     * @param cid 网关
     * @param vid 微逆
     * @param loop 路
     * @param searchDate 日期
     * @param searchType 日期类型
     * @return
     */
    public List<CidDayData> selectMisDetailBySearchDate(String cid,String vid,String loop,String searchDate,String searchType);



    /**
     * 获取今天所有微逆发电量最后一条记录
     * @return
     */
    public List<CidData> selectTodayLastData(Long[] ids);


    /**
     * 获取今天所有微逆发电量最后一条记录 Ids
     * @param startDate 今天 yyyy-mm-dd 00:00:00
     * @param endDate 今天 yyyy-mm-dd 23:59:59
     * @return
     */
    public List<CidData> selectTodayLastDataGetMaxId(String startDate,String endDate);

    /**
     * 获获取今天所有微逆发电量第一条记录
     * @param startDate 今天 yyyy-mm-dd 00:00:00
     * @param endDate 今天 yyyy-mm-dd 23:59:59
     * @return
     */
    public List<CidData> selectTodayFirstData(String startDate,String endDate);

    /**
     * 获取昨天所有微逆发电量最后一条记录
     * @return
     */
    public List<CidData> selectLastDayLastData(Long[] ids);

    /**
     * 获取昨天所有微逆发电量最后一条记录 ids
     * @return
     */
    public List<CidData> selectLastDayLastDataGetMaxId();

    /**
     * 获取电站当前发电功率
     * @return
     */
    public List<CidData> selectCurrentPower(Long[] ids);


    /**
     * 获取今日发电量信息
     * @return
     */
    public List<CidData> selectLastEnergyDataByToday();


    /**
     * 自动绑定关系时查询所有发电信息 by group
     * @return
     */
    public List<CidData> selectCidVidLoopForRelation();


    /**
     * 获取最后一次发电时间
     * @return
     */
    public List<CidData> selectCidVidLoopDateForStatus();


    /**
     * 获取电站当前发电功率
     * @return
     */
    public List<CidData> selectCurrentPowerGetMaxId();


    /**
     * 获取电站最后发电的记录id
     * @param powerStationId
     * @return
     */
    public List<CidData> selectPlantCurrentPowerId(Long powerStationId);


    /**
     * 获取电站最后发电的记录id
     * @param powerStationId
     * @return
     */
    public List<CidData> selectPlantMinId(Long powerStationId);


    /**
     * 获取电站当前发电功率
     * @param ids
     * @return
     */
    public List<CidData> selectPlantCurrentPowerByIds(Long[] ids);


    /**
     * 获取某天所有发电的网关、微逆信息
     * @return
     */
    public List<CidData> selectTaskCidVidLoop(String startDate,String endDate);


    /**
     * 获取日期内最大发电记录
     * @param
     * @return
     */
    public Long selectTaskMaxIds(String cid,String vid,String roadType,String startDate,String endDate);

    /**
     * 获取指定日期内最大发电记录
     * @param
     * @return
     */
    public Long selectTaskMaxIdsByLastDate(String cid,String vid,String roadType,String searchDate);
    /**
     * 获取日期内最小发电记录
     * @param
     * @return
     */
    public Long selectTaskMinIds(String cid,String vid,String roadType,String startDate,String endDate);


    /**
     * 获取Tree cid最后记录id
     * @param cid
     * @return
     */
    public List<Long> selectEmuTreeEmuIds(String cid);

    /**
     * 获取Tree vid最后记录id
     * @param cid
     * @param vid
     * @param roadType
     * @return
     */
    public Long selectEmuTreeMisId(String cid,String vid,String roadType);


    /**
     * 根据电站id 获取当天发电量最后ids
     * @param powerStationId
     * @return
     */
    public List<CidData> selectPlantCurrentPowerIdNew(Long powerStationId);

    /**
     * 根据电站id 获取最后一条发电记录
     * @param powerStationId
     * @return
     */
    public Long selectPlantLastUpdateId(Long powerStationId);

    /**
     * 获取每小时的所有发电记录
     * @param powerStationId
     * @return
     */
    public List<CidData> selectPlantEnergyByHour(Long powerStationId);


    /**
     * 根据电站cid vid loop 获取最后一条发电记录
     * @param cid
     * @param vid
     * @param roadType
     * @return
     */
    public Long getMaxDataByCidVidLoop(String cid,String vid,String roadType);


    /**
     * 获取当前登陆用户所属机构的发电量
     * @param deptId
     * @return
     */
    public String getSumEnergyByLoginUser(Long deptId);

    /**
     * 获取当前登陆用户所属机构的发电量
     * @param deptId
     * @return
     */
    public String getSumTodayEnergyByLoginUser(Long deptId);


    /**
     * 获取cid，vid，road 日期的发电量
     * @param cid
     * @param vid
     * @param roadType
     * @return
     */
    public List<CidData> getStationEnergyDayByCidVidAndDate(String plantID,String cid,String vid,String roadType,String searchDate);


    /**
     * lof app 8小时内发电功率 20分钟一条
     * @param  searchDate 8小时前的时间
     * @param deptId
     * @return
     */
    public List<CidData> selectLofPower(String searchDate,Long deptId);

    /**
     * app 获取发电功率
     * @param deptId
     * @return
     */
    public List<CidData> selectPowerForAppIndex(Long deptId);
}
