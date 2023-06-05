package com.benyi.energy.mapper;

import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidData;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发电信息Mapper接口
 * 
 * @author wuqiguang
 * @date 2022-07-31
 */
@Mapper
public interface CidDataMapper 
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
     * @param paramMap 参数
     * @return 发电信息
     */
    public List<Map> getErrorCodeAndDescByCidAndVid(Map paramMap);


    /**
     * 查询发电信息
     * 
     * @param id 发电信息主键
     * @return 发电信息
     */
    public CidData selectCidDataById(Long id);

    /**
     * 查询最后电信息
     * 
     * @param cidData 发电信息
     * @return 发电信息集合
     */
    public List<CidData> selectLastCidList(CidData cidData);

    /**
     * 查询发电信息列表
     *
     * @param cidData 发电信息
     * @return 发电信息集合
     */
    public List<CidData> selectCidDataList(CidData cidData);

    /**
     * 查询发电故障信息列表
     *
     * @param
     * @return 发电信息集合
     */
    public List<CidData> selectCidDataErrorList(Map<String, Object> map);

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
     * 删除发电信息
     * 
     * @param id 发电信息主键
     * @return 结果
     */
    public int deleteCidDataById(Long id);

    /**
     * 批量删除发电信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidDataByIds(Long[] ids);


    /**
     * 获取网关列表
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getListByCids(Map<String, String[]> map);


    /**
     * 获取微逆列表
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getListByVids(Map<String, String[]> map);

    /**
     *
     * 获取当日发电信息数据with out order by
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getDayCountPowerInfo(Map<String, String[]> map);

    /**
     * 根据网关 查询当日发电量信息
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public Long getCountPowerInfo(Map<String, String[]> map);


    /**
     * 根据网关 查询当日发电量信息
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getDayPowerInfo(Map<String, Object> map);


    /**
     * 根据网关 查询当日发电量信息  sql语句5分钟
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getDayPowerInfoForMinute(Map<String, Object> map);


    /**
     * 根据网关 查询当日发电量信息  sql语句5分钟
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getStationPowerDayByCidAndVidAndPidAndDateForMinute(Map<String, Object> map);

    /**
     * 根据网关 查询周发电量信息 --迁移
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getWeekPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询月发电量信息 --迁移
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getMonthPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电量信息 --迁移
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidData> getYearPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电量信息-------迁移
     *
     * @param map
     * @return
     */
    public List<CidData> getAllPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电量信息 -------迁移
     *
     * @return
     */
    public List<CidData> getCidCountByDate(Long energyDeptId);


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
    public List<CidData> getEmuList(Map<String, Object> map);


    /**
     * 获取网关列表详情
     * @param map
     * @return
     */
    public List<CidData> getCidList(Map<String, Object> map);

    /**
     * 获取网关列表详情
     * @param map
     * @return
     */
    public List<CidData> getCidAppList(Map<String, Object> map);


    /**
     * 根据电站ID与查询时间 一天Power
     * @param map
     * @return
     */
    public List<CidData> getStationPowerDayByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一天Power sql5分钟
     * @param map
     * @return
     */
    public List<CidData> getStationPowerDayByPidAndDateForMinute(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一周Power ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationPowerWeekByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一月Power ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationPowerMonthByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一年Power ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationPowerYearByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 总Power ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationPowerAllByPidAndDate(Map<String, Object> map);


    /**
     * 根据电站ID与查询时间 一小时Energy
     * @param map
     * @return
     */
    public List<CidData> getStationEnergyHourByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一天Energy
     * @param map
     * @return
     */
    public List<CidData> getStationEnergyDayByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一周Energy ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationEnergyWeekByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一月Energy ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationEnergyMonthByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一年Energy ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationEnergyYearByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 总Energy ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getStationEnergyAllByPidAndDate(Map<String, Object> map);


    /**
     * 根据网关 查询 网关指定天时间的详情
     * @param map
     * @return
     */
    public List<CidData> getCidDayDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定天时间的详情
     * @param map
     * @return
     */
    public List<CidData> getVidsDayDetail(Map<String, Object> map);

    /**
     * 根据网关  查询 网关指定周时间的详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getCidWeekDetail(Map<String, Object> map);

    /**
     * 根据网关 查询 网关指定月时间的详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getCidMonthDetail(Map<String, Object> map);

    /**
     * 根据网关 查询 网关指定年时间的详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getCidYearDetail(Map<String, Object> map);

    /**
     * 根据网关 查询 网关指定天总的详情----------迁移
     * @param map
     * @return
     */
    public List<CidData> getCidAllDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定周时间的详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getVidsWeekDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定月时间的详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getVidsMonthDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定年时间的详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getVidsYearDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定总时间的详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> getVidsAllDetail(Map<String, Object> map);


    /**
     * 根据deptId与日期查询当日功率
     * @param map
     * @return
     */
    public List<CidData> selectEnergyByDate(Map<String, Object> map);


    /**
     * 根据网关、微逆、起始日期、结束日期查询发电信息详情 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> selectDetailByCidVidAndDate(Map<String, Object> map);


    /**
     * 根据网关、微逆、日期 查询pv电池板电压 day old
     * @param map
     * @return
     */
    public List<CidData> selectMisDetailVoltByDay(Map<String, Object> map);

    /**
     * 根据网关、微逆、日期 查询pv电池板电压 day new
     * @param map
     * @return
     */
    public List<CidData> selectMisDetailVoltByDayNew(Map<String, Object> map);

    /**
     * 根据网关、微逆、日期 查询pv电池板电压 day new 5分钟sql语句
     * @param map
     * @return
     */
    public List<Map> selectMisDetailVoltByDayNewForMinute(Map<String, Object> map);

    /**
     * 根据网关、微逆、日期 查询pv电池板电压 month ----------迁移
     * @param map
     * @return
     */
    public List<CidData> selectMisDetailVoltByMonth(Map<String, Object> map);

    /**
     * 根据网关、微逆、日期 查询pv电池板电压 year ----------迁移
     * @param map
     * @return
     */
    public List<CidData> selectMisDetailVoltByYear(Map<String, Object> map);

    /**
     * 根据网关、微逆 查询 Loop
     * @param map
     * @return
     */
    public List<String> selectMisDetailLoop(Map<String, Object> map);

    /**
     * 根据网关 获取 微逆总发电量信息
     * @param vid 微逆
     * @return
     */
    public CidData getEmuPowerCountByEmu(String vid);

    /**
     * 根据网关 获取 微逆当前发电量信息
     * @param vid 微逆
     * @return
     */
    public CidData getEmuCurrentPowerByEmu(String vid);


    /**
     * 根据 网关或者微逆 查询数据 日
     * @param map
     * @return
     */
    public List<CidData> selectMisDetailByDayAndSearchDate(Map<String, Object> map);


    /**
     * 根据 网关或者微逆 查询数据 月 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> selectMisDetailByMonthAndSearchDate(Map<String, Object> map);

    /**
     * 根据 网关或者微逆 查询数据 年 ----------迁移
     * @param map
     * @return
     */
    public List<CidData> selectMisDetailByYearAndSearchDate(Map<String, Object> map);


    /**
     * 获取今天所有微逆发电量最后一条记录
     * @param map ids
     * @return
     */
    public List<CidData> selectTodayLastData(Map<String, Object> map);

    /**
     * 获取今天所有微逆发电量最后一条记录
     * @param map
     * @return
     */
    public List<CidData> selectTodayLastDataGetMaxId(Map<String, Object> map);

    /**
     * 获获取今天所有微逆发电量第一条记录
     * @param map
     * @return
     */
    public List<CidData> selectTodayFirstData(Map<String, Object> map);

    /**
     * 获取昨天所有微逆发电量最后一条记录
     * @return
     */
    public List<CidData> selectLastDayLastData(Map<String, Object> map);

    /**
     * 获取昨天所有微逆发电量最后一条记录
     * @return ids
     */
    public List<CidData> selectLastDayLastDataGetMaxId();

    /**
     * 获取电站当前发电功率
     * @return
     */
    public List<CidData> selectCurrentPower(Map<String, Object> map);

    /**
     * 获取电站当前发电功率
     * @return
     */
    public List<CidData> selectCurrentPowerGetMaxId();


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
     * @param map
     * @return
     */
    public List<CidData> selectPlantCurrentPowerByIds(Map<String, Object> map);


    /**
     * 获取某天所有发电的网关、微逆信息
     * @param map
     * @return
     */
    public List<CidData> selectTaskCidVidLoop(Map<String, Object> map);

    /**
     * 获取日期内最大发电记录
     * @param map
     * @return
     */
    public Long selectTaskMaxIds(Map<String, Object> map);

    /**
     * 获取指定日期最后一条发电记录
     * @param map
     * @return
     */
    public Long selectTaskMaxIdsByLastDate(Map<String, Object> map);

    /**
     * 获取日期内最小发电记录
     * @param map
     * @return
     */
    public Long selectTaskMinIds(Map<String, Object> map);

    /**
     * 获取Tree cid最后记录id
     * @param map
     * @return
     */
    public List<Long> selectEmuTreeEmuIds(Map<String, Object> map);

    /**
     * 获取Tree vid最后记录id
     * @param map
     * @return
     */
    public Long selectEmuTreeMisId(Map<String, Object> map);


    /**
     * 根据电站id 获取当天发电量最后ids
     * @param map
     * @return
     */
    public List<CidData> selectPlantCurrentPowerIdNew(Map<String, Object> map);

    /**
     * 根据电站id 获取最后一条发电记录
     * @param map
     * @return
     */
    public Long selectPlantLastUpdateId(Map<String, Object> map);

    /**
     * 获取每小时的所有发电记录
     * @param map
     * @return
     */
    public List<CidData> selectPlantEnergyByHour(Map<String, Object> map);

    /**
     * 根据电站cid vid loop 获取最后一条发电记录
     * @param map
     * @return
     */
    public Long getMaxDataByCidVidLoop(Map<String, Object> map);

    /**
     * 获取当前登陆用户所属机构的发电量
     * @param map
     * @return
     */
    public String getSumEnergyByLoginUser(Map<String, Object> map);

    /**
     * 获取当前登陆用户所属机构的发电量 今日
     * @param map
     * @return
     */
    public String getSumTodayEnergyByLoginUser(Map<String, Object> map);

    /**
     * 获取当前cid/vid的发电情况
     * @param map
     * @return
     */
    public List<CidData> getStationEnergyDayByCidVidAndDate(Map<String, Object> map);

    /**
     * lof app 8小时内发电功率 20分钟一条
     * @param map
     * @return
     */
    public List<CidData> selectLofPower(Map<String, Object> map);


    /**
     * app 获取发电功率
     * @param deptId
     * @return
     */
    public List<CidData> selectPowerForAppIndex(Long deptId);

}
