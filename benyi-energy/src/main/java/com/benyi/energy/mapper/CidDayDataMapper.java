package com.benyi.energy.mapper;

import java.util.List;
import java.util.Map;


import com.benyi.energy.domain.CidDayData;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发电信息Mapper接口
 *
 * @author wuqiguang
 * @date 2022-09-15
 */
@Mapper
public interface CidDayDataMapper
{


    public List<Map> getStationEnergyInPeriodOfTime(Map paramMap);
    /**
     * 查询发电信息
     *
     * @param id 发电信息主键
     * @return 发电信息
     */
    public CidDayData selectCidDayDataById(Long id);

    /**
     * 查询发电信息列表
     *
     * @param cidDayData 发电信息
     * @return 发电信息集合
     */
    public List<CidDayData> selectCidDayDataList(CidDayData cidDayData);

    /**
     * 新增发电信息
     *
     * @param cidDayData 发电信息
     * @return 结果
     */
    public int insertCidDayData(CidDayData cidDayData);

    /**
     * 修改发电信息
     *
     * @param cidDayData 发电信息
     * @return 结果
     */
    public int updateCidDayData(CidDayData cidDayData);

    /**
     * 电站迁移与微逆修改绑定关系，设置历史数据delFlag和isRemove =1
     * @param cidDayData
     * @return
     */
    public int updateForRemoveAndDel(CidDayData cidDayData);


    /**
     * 电站迁移与微逆修改绑定关系，设置历史数据delFlag和isRemove =1
     * @param cidDayData
     * @return
     */
    public int updateBatchForRemoveAndDel(CidDayData cidDayData);

    /**
     * 删除发电信息
     *
     * @param id 发电信息主键
     * @return 结果
     */
    public int deleteCidDayDataById(Long id);

    /**
     * 批量删除发电信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidDayDataByIds(Long[] ids);


    /**
     * 根据网关 查询周发电量信息
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidDayData> getWeekPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询月发电功率信息
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidDayData> getMonthPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询月发电量信息
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidDayData> getMonthEnergyInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电功率信息
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidDayData> getYearPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电量信息
     *
     * @param map
     * @return
     */
    @MapKey("date")
    public List<CidDayData> getYearEnergyInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电功率信息
     *
     * @param map
     * @return
     */
    public List<CidDayData> getAllPowerInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电量信息
     *
     * @param map
     * @return
     */
    public List<CidDayData> getAllEnergyInfo(Map<String, Object> map);

    /**
     * 根据网关 查询年发电量信息
     *
     * @return
     */
    public List<CidDayData> getCidCountByDate(Long energyDeptId);


    /**
     * 根据电站ID与查询时间 一天Power
     * @param map
     * @return
     */
    public List<CidDayData> getStationPowerDayByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一周Power
     * @param map
     * @return
     */
    public List<CidDayData> getStationPowerWeekByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一月Power
     * @param map
     * @return
     */
    public List<CidDayData> getStationPowerMonthByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一年Power
     * @param map
     * @return
     */
    public List<CidDayData> getStationPowerYearByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 总Power
     * @param map
     * @return
     */
    public List<CidDayData> getStationPowerAllByPidAndDate(Map<String, Object> map);


    /**
     * 根据电站ID与查询时间 一周Energy
     * @param map
     * @return
     */
    public List<CidDayData> getStationEnergyWeekByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一月Energy
     * @param map
     * @return
     */
    public List<CidDayData> getStationEnergyMonthByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 一年Energy
     * @param map
     * @return
     */
    public List<CidDayData> getStationEnergyYearByPidAndDate(Map<String, Object> map);

    /**
     * 根据电站ID与查询时间 总Energy
     * @param map
     * @return
     */
    public List<CidDayData> getStationEnergyAllByPidAndDate(Map<String, Object> map);

    /**
     * 根据网关  查询 网关指定周时间的详情
     * @param map
     * @return
     */
    public List<CidDayData> getCidWeekDetail(Map<String, Object> map);

    /**
     * 根据网关 查询 网关指定月时间的详情
     * @param map
     * @return
     */
    public List<CidDayData> getCidMonthDetail(Map<String, Object> map);

    /**
     * 根据网关 查询 网关指定年时间的详情
     * @param map
     * @return
     */
    public List<CidDayData> getCidYearDetail(Map<String, Object> map);

    /**
     * 根据网关 查询 网关指定天总的详情
     * @param map
     * @return
     */
    public List<CidDayData> getCidAllDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定周时间的详情
     * @param map
     * @return
     */
    public List<CidDayData> getVidsWeekDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定月时间的详情
     * @param map
     * @return
     */
    public List<CidDayData> getVidsMonthDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定年时间的详情
     * @param map
     * @return
     */
    public List<CidDayData> getVidsYearDetail(Map<String, Object> map);

    /**
     * 根据微逆  查询 微逆指定总时间的详情
     * @param map
     * @return
     */
    public List<CidDayData> getVidsAllDetail(Map<String, Object> map);

    /**
     * 根据网关、微逆、起始日期、结束日期查询发电信息详情
     * @param map
     * @return
     */
    public List<CidDayData> selectDetailByCidVidAndDate(Map<String, Object> map);


    /**
     * 根据网关、微逆、日期 查询pv电池板电压 month
     * @param map
     * @return
     */
    public List<CidDayData> selectMisDetailVoltByMonth(Map<String, Object> map);

    /**
     * 根据网关、微逆、日期 查询pv电池板电压 year
     * @param map
     * @return
     */
    public List<CidDayData> selectMisDetailVoltByYear(Map<String, Object> map);


    /**
     * 根据 网关或者微逆 查询数据 月
     * @param map
     * @return
     */
    public List<CidDayData> selectMisDetailByMonthAndSearchDate(Map<String, Object> map);


    /**
     * 根据 网关或者微逆 查询数据 年
     * @param map
     * @return
     */
    public List<CidDayData> selectMisDetailByYearAndInPeriodOfTime(Map<String, Object> map);

    /**
     * 根据 网关或者微逆 查询数据 年
     * @param map
     * @return
     */
    public List<CidDayData> selectMisDetailByYearAndSearchDate(Map<String, Object> map);


    /**
     * 获取今年的总发电量（除去今日）
     * @return
     */
    public List<CidDayData> selectCurrentYearEnergy();

    /**
     * 获取本月的总发电量（除去今日）
     * @return
     */
    public List<CidDayData> selectCurrentMonthEnergy();

    /**
     * 更新昨日发电量
     * @param cidDayData
     * @return
     */
    public int updateCidDayDataEnergy(CidDayData cidDayData);

    /**
     * 获取今日发电量信息
     * @return
     */
    public List<CidDayData> selectTodayData();

    /**
     * 根据cid，vid，roadtype查询今日发电表信息
     * @param cidDayData
     * @return
     */
    public CidDayData selectTodayDataByCidVidRoad(CidDayData cidDayData);


    /**
     * 根据cid，vid，loop，date 更新发电日数据
     * @param cidDayData
     * @return
     */
    public int updateByCidVidLoopDate(CidDayData cidDayData);


    /**
     * 根据cid，vid，loop，date 获取月发电量
     * @param map
     * @return
     */
    public List<CidDayData> selectCidVidLoopSumEnergyWithMonth(Map<String, Object> map);

    /**
     * 根据cid，vid，loop，date 获取月发电量
     * @param map
     * @return
     */
    public List<CidDayData> selectCidVidLoopSumEnergyWithYear(Map<String, Object> map);

    /**
     * 根据cid，vid，loop，date 获取月发电量
     * @return
     */
    public List<CidDayData> selectCidVidLoopSumEnergyWithCount();

    /**
     * 根据cid vid loop ,date 查询是否已存在数据
     * @param map
     * @return
     */
    public Long selectForValInsert(Map<String, Object> map);

    /**
     * cid vid loop ,date 月发电量
     * @param map
     * @return
     */
    public List<CidDayData> getCidVidMonthByPidAndDate(Map<String, Object> map);

    /**
     * cid vid loop ,date 年发电量
     * @param map
     * @return
     */
    public List<CidDayData> getCidVidYearByPidAndDate(Map<String, Object> map);

    /**
     * cid vid loop ,date 总发电量
     * @param map
     * @return
     */
    public List<CidDayData> getCidVidAllByPidAndDate(Map<String, Object> map);

    /**
     * dept lof app energy数据 10天内
     * @param map
     * @return
     */
    public List<CidDayData> selectLofEnergy(Map<String, Object> map);

}
